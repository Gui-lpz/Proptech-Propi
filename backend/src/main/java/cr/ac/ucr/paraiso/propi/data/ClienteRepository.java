
package cr.ac.ucr.paraiso.propi.data;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.ClienteDTO;

@Repository
public class ClienteRepository {

        private final JdbcTemplate jdbcTemplate;

        public ClienteRepository(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        // READ: CONSULTAR CLIENTES

        public List<ClienteDTO> listar() {

                // Catalogo de tipos de identificacion.
                Map<Integer, String[]> identificaciones = new HashMap<>();

                jdbcTemplate.query(
                                """
                                                SELECT
                                                    TipoIdentificacionID,
                                                    Codigo,
                                                    Descripcion
                                                FROM dbo.TipoIdentificacion
                                                """,
                                rs -> {
                                        identificaciones.put(
                                                        rs.getInt("TipoIdentificacionID"),
                                                        new String[] {
                                                                        rs.getString("Codigo"),
                                                                        rs.getString("Descripcion")
                                                        });
                                });

                // Catalogos de provincia, canton y distrito.
                Map<Integer, String[]> ubicaciones = new HashMap<>();

                jdbcTemplate.query(
                                """
                                                SELECT
                                                    d.DistritoID,
                                                    d.Nombre AS Distrito,
                                                    c.CantonID,
                                                    c.Nombre AS Canton,
                                                    p.ProvinciaID,
                                                    p.Nombre AS Provincia
                                                FROM dbo.Distrito d
                                                INNER JOIN dbo.Canton c
                                                    ON d.CantonID = c.CantonID
                                                INNER JOIN dbo.Provincia p
                                                    ON c.ProvinciaID = p.ProvinciaID
                                                """,
                                rs -> {
                                        ubicaciones.put(
                                                        rs.getInt("DistritoID"),
                                                        new String[] {
                                                                        rs.getString("Distrito"),
                                                                        rs.getString("Canton"),
                                                                        rs.getString("Provincia"),
                                                                        String.valueOf(
                                                                                        rs.getInt("CantonID")),
                                                                        String.valueOf(
                                                                                        rs.getInt("ProvinciaID"))
                                                        });
                                });

                // Catalogo de actividades economicas.
                Map<Integer, String[]> actividades = new HashMap<>();

                jdbcTemplate.query(
                                """
                                                SELECT
                                                    ActividadEconomicaID,
                                                    Codigo,
                                                    Descripcion
                                                FROM dbo.ActividadEconomica
                                                """,
                                rs -> {
                                        actividades.put(
                                                        rs.getInt("ActividadEconomicaID"),
                                                        new String[] {
                                                                        rs.getString("Codigo"),
                                                                        rs.getString("Descripcion")
                                                        });
                                });

                // El procedimiento devuelve los datos sensibles
                // descifrados mediante DecryptByKey().
                return jdbcTemplate.query(
                                "EXEC dbo.sp_Cliente_Listar",
                                (rs, rowNum) -> mapear(
                                                rs,
                                                identificaciones,
                                                ubicaciones,
                                                actividades));
        }

        // CREATE / UPDATE: CREAR O ACTUALIZAR CLIENTES

        public ClienteDTO guardar(
                        ClienteDTO dto,
                        String usuario,
                        String sesion) {

                // El frontend ya envia el identificador numerico.
                Integer tipoIdentificacionId = dto.getTipoIdentificacionId();

                if (tipoIdentificacionId == null
                                || tipoIdentificacionId <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar un tipo de identificacion valido.");
                }

                if (dto.getNombre() == null
                                || dto.getNombre().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Debe ingresar el nombre del cliente.");
                }

                if (dto.getNumeroIdentificacion() == null
                                || dto.getNumeroIdentificacion().isBlank()) {

                        throw new IllegalArgumentException(
                                        "Debe ingresar el numero de identificacion.");
                }

                if (dto.getDistritoId() == null
                                || dto.getActividadEconomicaId() == null) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar distrito y actividad economica.");
                }

                // El procedimiento devuelve el ClienteID
                // generado o actualizado.
                Integer clienteId = jdbcTemplate.execute(
                                (ConnectionCallback<Integer>) connection -> {

                                        try (CallableStatement cs = connection.prepareCall(
                                                        "{call dbo.sp_Cliente_Guardar"
                                                                        + "(?,?,?,?,?,?,?,?,?,?,?,?)}")) {

                                                // 1. ClienteID
                                                if (dto.getClienteId() == null) {
                                                        cs.setNull(1, Types.INTEGER);
                                                } else {
                                                        cs.setInt(1, dto.getClienteId());
                                                }

                                                // 2. TipoIdentificacionID
                                                cs.setInt(
                                                                2,
                                                                tipoIdentificacionId);

                                                // 3. NumeroIdentificacion
                                                cs.setString(
                                                                3,
                                                                dto.getNumeroIdentificacion());

                                                // 4. Nombre
                                                cs.setString(
                                                                4,
                                                                dto.getNombre());

                                                // 5. DistritoID
                                                cs.setInt(
                                                                5,
                                                                dto.getDistritoId());

                                                // 6. Barrio
                                                cs.setString(
                                                                6,
                                                                dto.getBarrio());

                                                // 7. OtrasSenas
                                                cs.setString(
                                                                7,
                                                                dto.getOtrasSenas());

                                                // 8. ProfesionOficio
                                                cs.setString(
                                                                8,
                                                                dto.getProfesionOficio());

                                                // 9. ActividadEconomicaID
                                                cs.setInt(
                                                                9,
                                                                dto.getActividadEconomicaId());

                                                // 10. CorreoElectronico
                                                cs.setString(
                                                                10,
                                                                dto.getCorreoElectronico());

                                                // 11. Telefono
                                                cs.setString(
                                                                11,
                                                                dto.getTelefono());

                                                // 12. Estado
                                                cs.setString(
                                                                12,
                                                                dto.getEstado() == null
                                                                                ? "A"
                                                                                : dto.getEstado());

                                                // El SP devuelve una fila con ClienteID.
                                                try (ResultSet rs = cs.executeQuery()) {

                                                        if (rs.next()) {
                                                                return rs.getInt("ClienteID");
                                                        }
                                                }

                                                throw new SQLException(
                                                                "El procedimiento no devolvio ClienteID.");
                                        }
                                });

                if (clienteId == null) {
                        throw new IllegalStateException(
                                        "No se pudo recuperar el identificador del cliente.");
                }

                // Consultar nuevamente para devolver
                // la informacion completa y descifrada.
                return listar()
                                .stream()
                                .filter(cliente -> clienteId.equals(cliente.getClienteId()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                "El cliente fue guardado, "
                                                                + "pero no se pudo recuperar su informacion."));
        }

        // DELETE: ELIMINAR CLIENTE

        public void eliminar(
                        Integer clienteId,
                        String usuario,
                        String sesion) {

                if (clienteId == null || clienteId <= 0) {
                        throw new IllegalArgumentException(
                                        "El identificador del cliente es invalido.");
                }

                jdbcTemplate.update(
                                "EXEC dbo.sp_Cliente_Eliminar ?",
                                clienteId);
        }

        private ClienteDTO mapear(
                        ResultSet rs,
                        Map<Integer, String[]> identificaciones,
                        Map<Integer, String[]> ubicaciones,
                        Map<Integer, String[]> actividades)
                        throws SQLException {

                ClienteDTO dto = new ClienteDTO();

                // Identificador principal.
                dto.setClienteId(
                                rs.getInt("ClienteID"));

                // Nombre.
                dto.setNombre(
                                rs.getString("Nombre"));

                // Tipo de identificacion.
                int tipoId = rs.getInt("TipoIdentificacionID");

                // Este ID es el que necesita el frontend
                // cuando el usuario edita un cliente.
                dto.setTipoIdentificacionId(tipoId);

                String[] identificacion = identificaciones.get(tipoId);

                if (identificacion != null) {
                        dto.setTipoIdentificacion(
                                        identificacion[1]);
                }

                // Numero de identificacion descifrado.
                dto.setNumeroIdentificacion(
                                rs.getString("NumeroIdentificacion"));

                // Distrito y ubicacion.
                int distritoId = rs.getInt("DistritoID");

                dto.setDistritoId(distritoId);

                String[] ubicacion = ubicaciones.get(distritoId);

                if (ubicacion != null) {

                        dto.setDistrito(
                                        ubicacion[0]);

                        dto.setCanton(
                                        ubicacion[1]);

                        dto.setProvincia(
                                        ubicacion[2]);

                        dto.setCantonId(
                                        Integer.valueOf(ubicacion[3]));

                        dto.setProvinciaId(
                                        Integer.valueOf(ubicacion[4]));
                }

                // Informacion de direccion.
                dto.setBarrio(
                                rs.getString("Barrio"));

                dto.setOtrasSenas(
                                rs.getString("OtrasSenas"));

                // Profesion u oficio.
                dto.setProfesionOficio(
                                rs.getString("ProfesionOficio"));

                // Actividad economica.
                int actividadId = rs.getInt("ActividadEconomicaID");

                dto.setActividadEconomicaId(
                                actividadId);

                String[] actividad = actividades.get(actividadId);

                if (actividad != null) {

                        dto.setCodigoActividad(
                                        actividad[0]);

                        dto.setActividadEconomica(
                                        actividad[1]);
                }

                // Datos sensibles descifrados desde SQL Server.
                dto.setCorreoElectronico(
                                rs.getString("CorreoElectronico"));

                dto.setTelefono(
                                rs.getString("Telefono"));

                // Fecha de registro.
                Timestamp fecha = rs.getTimestamp("FechaRegistro");

                if (fecha != null) {
                        dto.setFechaRegistro(
                                        fecha.toLocalDateTime());
                }

                // Estado activo o inactivo.
                dto.setEstado(
                                rs.getString("Estado"));

                return dto;
        }
}
