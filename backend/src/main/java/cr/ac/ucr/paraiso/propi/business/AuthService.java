
package cr.ac.ucr.paraiso.propi.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.dto.AuthRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.AuthResponseDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@Service
public class AuthService {

        private static final Logger log = LoggerFactory.getLogger(AuthService.class);

        private final JwtTokenProvider jwtTokenProvider;
        private final JdbcTemplate jdbcTemplate;

        // Direccion SQL Server de application.properties
        @Value("${spring.datasource.url}")
        private String url;

        /* CONSTRUCTOR */

        public AuthService(
                        JwtTokenProvider jwtTokenProvider,
                        JdbcTemplate jdbcTemplate) {

                this.jwtTokenProvider = jwtTokenProvider;
                this.jdbcTemplate = jdbcTemplate;
        }

        /*
         * REQ 003: INICIO DE SESION
         *
         * SQL Server valida las credenciales.
         * Se registra el ingreso en BitacoraAcceso.
         * Se genera un token JWT.
         */

        public AuthResponseDTO login(
                        AuthRequestDTO request,
                        String terminal,
                        String ip) {

                if (request == null ||
                                request.getUsername() == null ||
                                request.getUsername().isBlank() ||
                                request.getPassword() == null ||
                                request.getPassword().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Debe proporcionar usuario y contraseña.");
                }

                String etapa = "Conexion SQL Server";

                try (Connection conexion = DriverManager.getConnection(
                                url,
                                request.getUsername(),
                                request.getPassword())) {

                        log.info("Conexion SQL establecida para usuario {}",
                                        request.getUsername());

                        // 1. Consultar rol del usuario.
                        etapa = "Consultar rol";

                        String rol = obtenerRol(conexion);

                        if (rol == null) {
                                throw new SecurityException(
                                                "El usuario no tiene un rol autorizado.");
                        }

                        log.info("Rol identificado: {}", rol);

                        // 2. Registrar ingreso.
                        etapa = "Registrar ingreso en BitacoraAcceso";

                        String sessionId = registrarIngreso(
                                        conexion,
                                        terminal,
                                        ip);

                        log.info("Ingreso registrado correctamente.");

                        // 3. Generar JWT.
                        etapa = "Generar JWT";

                        List<String> roles = List.of(rol);

                        String token = jwtTokenProvider.generateToken(
                                        request.getUsername(),
                                        roles,
                                        sessionId);

                        return new AuthResponseDTO(
                                        token,
                                        request.getUsername(),
                                        roles,
                                        jwtTokenProvider.getExpirationTime(token),
                                        sessionId);

                } catch (SQLException error) {

                        log.error(
                                        "ERROR SQL durante login. Etapa: {}. " +
                                                        "Codigo SQL: {}. SQLState: {}. Detalle: {}",
                                        etapa,
                                        error.getErrorCode(),
                                        error.getSQLState(),
                                        error.getMessage(),
                                        error);

                        throw new RuntimeException(
                                        "No fue posible autenticar con SQL Server. " +
                                                        "Verifica las credenciales y la conexión.",
                                        error);
                }
        }

        /*
         * REQ 004: OBTENER ROL
         */

        private String obtenerRol(Connection conexion)
                        throws SQLException {

                String sql = """
                                SELECT
                                    IS_ROLEMEMBER('rol_Administrador') AS Admin,
                                    IS_ROLEMEMBER('rol_Digitador') AS Digitador,
                                    IS_ROLEMEMBER('rol_Consulta') AS Consulta
                                """;

                try (Statement consulta = conexion.createStatement();
                                ResultSet resultado = consulta.executeQuery(sql)) {

                        if (resultado.next()) {

                                if (resultado.getInt("Admin") == 1) {
                                        return "ROLE_ADMINISTRADOR";
                                }

                                if (resultado.getInt("Digitador") == 1) {
                                        return "ROLE_DIGITADOR";
                                }

                                if (resultado.getInt("Consulta") == 1) {
                                        return "ROLE_CONSULTA";
                                }
                        }
                }

                return null;
        }

        /*
         * REQ 003: REGISTRAR INGRESO
         *
         * Utiliza la identidad SQL autenticada.
         */

        private String registrarIngreso(
                        Connection conexion,
                        String terminal,
                        String ip) throws SQLException {

                String sql = """
                                EXEC dbo.sp_RegistrarIngreso
                                    @NombreTerminal = ?,
                                    @DireccionIP = ?
                                """;

                try (PreparedStatement ps = conexion.prepareStatement(sql)) {

                        ps.setString(1, terminal);
                        ps.setString(2, ip);

                        try (ResultSet resultado = ps.executeQuery()) {

                                if (resultado.next()) {

                                        return resultado.getString(
                                                        "BitacoraAccesoID");
                                }
                        }
                }

                throw new SQLException(
                                "No se pudo registrar el ingreso.");
        }

        /*
         * REQ 003: REGISTRAR SALIDA
         *
         * El usuario proviene del JWT validado.
         */

        public void cerrarSesion(
                        String usuario,
                        String sessionId) {

                if (sessionId == null ||
                                sessionId.isBlank()) {

                        throw new IllegalArgumentException(
                                        "Identificador de sesión inválido.");
                }

                try {

                        long id = Long.parseLong(sessionId);

                        jdbcTemplate.update(
                                        "EXEC dbo.sp_RegistrarSalidaWeb ?, ?",
                                        id,
                                        usuario);

                } catch (NumberFormatException error) {

                        throw new IllegalArgumentException(
                                        "Identificador de sesión inválido.");
                }
        }
}
