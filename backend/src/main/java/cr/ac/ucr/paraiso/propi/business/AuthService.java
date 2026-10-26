
package cr.ac.ucr.paraiso.propi.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.propi.dto.AuthRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.AuthResponseDTO;
import cr.ac.ucr.paraiso.propi.security.JwtTokenProvider;

@Service
public class AuthService {

        private final JwtTokenProvider jwtTokenProvider;
        private final JdbcTemplate jdbcTemplate;

        // Dirección SQL Server de application.properties
        @Value("${spring.datasource.url}")
        private String url;

        // Constructor
        public AuthService(
                        JwtTokenProvider jwtTokenProvider,
                        JdbcTemplate jdbcTemplate) {

                this.jwtTokenProvider = jwtTokenProvider;
                this.jdbcTemplate = jdbcTemplate;
        }

        /*
         * REQ 003: INICIO DE SESIÓN
         *
         * SQL Server valida las credenciales.
         * Se registra el ingreso en BitacoraAcceso.
         * Se genera un token JWT para el frontend.
         */
        public AuthResponseDTO login(
                        AuthRequestDTO request,
                        String terminal,
                        String ip) {

                try (Connection conexion = DriverManager.getConnection(
                                url,
                                request.getUsername(),
                                request.getPassword())) {

                        // Consultar rol del usuario
                        String rol = obtenerRol(conexion);

                        if (rol == null) {
                                throw new RuntimeException(
                                                "El usuario no tiene un rol autorizado.");
                        }

                        // Registrar ingreso
                        String sessionId = registrarIngreso(conexion);

                        // Crear JWT
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

                        throw new RuntimeException(
                                        "No fue posible autenticar con SQL Server. "
                                                        + "Verifica las credenciales y la conexión.");
                }
        }

        /*
         * REQ 004: OBTENER ROL
         *
         * SQL Server devuelve 1 cuando el usuario
         * pertenece al rol indicado.
         *
         * Los nombres ROLE_ coinciden con app.js.
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
         * Ejecuta sp_RegistrarIngreso con la
         * identidad SQL del usuario autenticado.
         *
         * Devuelve el ID de BitacoraAcceso.
         */
        private String registrarIngreso(Connection conexion)
                        throws SQLException {

                String sql = "EXEC dbo.sp_RegistrarIngreso";

                try (Statement consulta = conexion.createStatement();
                                ResultSet resultado = consulta.executeQuery(sql)) {

                        if (resultado.next()) {
                                return resultado.getString("BitacoraAccesoID");
                        }
                }

                throw new SQLException(
                                "No se pudo registrar el ingreso.");
        }

        /*
         * REQ 003: REGISTRAR SALIDA
         *
         * El usuario debe provenir del JWT validado,
         * no de un campo editable del frontend.
         *
         * El procedimiento comprueba el usuario
         * y el ID antes de cerrar la sesión.
         */
        public void cerrarSesion(
                        String usuario,
                        String sessionId) {

                if (sessionId == null || sessionId.isBlank()) {
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
