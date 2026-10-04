package cr.ac.ucr.paraiso.propi.data;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.ClienteDTO;

@Repository
public class ClienteRepository {

        private final JdbcTemplate jdbcTemplate;

        public ClienteRepository(
                        JdbcTemplate jdbcTemplate) {

                this.jdbcTemplate = jdbcTemplate;
        }

        public List<ClienteDTO> listar() {

                return jdbcTemplate.query(
                                "EXEC facturacion.sp_Cliente_Listar",
                                (rs, rowNum) -> mapear(rs));
        }

        public ClienteDTO guardar(
                        ClienteDTO dto,
                        String usuario,
                        String sesion) {

                return jdbcTemplate.execute(
                                (ConnectionCallback<ClienteDTO>) connection -> {

                                        try (CallableStatement cs = connection.prepareCall(
                                                        "{call facturacion.sp_Cliente_Guardar"
                                                                        + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}")) {

                                                cs.setObject(
                                                                1,
                                                                dto.getClienteId());

                                                cs.setString(
                                                                2,
                                                                dto.getNombre());

                                                cs.setString(
                                                                3,
                                                                dto.getTipoIdentificacion());

                                                cs.setString(
                                                                4,
                                                                dto.getNumeroIdentificacion());

                                                cs.setObject(
                                                                5,
                                                                dto.getDistritoId());

                                                cs.setString(
                                                                6,
                                                                dto.getBarrio());

                                                cs.setString(
                                                                7,
                                                                dto.getOtrasSenas());

                                                cs.setString(
                                                                8,
                                                                dto.getProfesionOficio());

                                                cs.setObject(
                                                                9,
                                                                dto.getActividadEconomicaId());

                                                cs.setString(
                                                                10,
                                                                dto.getCorreoElectronico());

                                                cs.setString(
                                                                11,
                                                                dto.getTelefono());

                                                cs.setString(
                                                                12,
                                                                dto.getEstado());

                                                cs.setString(
                                                                13,
                                                                usuario);

                                                cs.setString(
                                                                14,
                                                                sesion);

                                                try (ResultSet rs = cs.executeQuery()) {

                                                        if (rs.next()) {
                                                                return mapear(rs);
                                                        }
                                                }
                                        }

                                        return null;
                                });
        }

        public void eliminar(
                        Integer clienteId,
                        String usuario,
                        String sesion) {

                jdbcTemplate.update(
                                "EXEC facturacion.sp_Cliente_Eliminar ?, ?, ?",
                                clienteId,
                                usuario,
                                sesion);
        }

        private ClienteDTO mapear(
                        ResultSet rs)
                        throws SQLException {

                ClienteDTO dto = new ClienteDTO();

                dto.setClienteId(
                                rs.getInt("ClienteID"));

                dto.setNombre(
                                rs.getString("Nombre"));

                dto.setTipoIdentificacion(
                                rs.getString(
                                                "TipoIdentificacion"));

                dto.setNumeroIdentificacion(
                                rs.getString(
                                                "NumeroIdentificacion"));

                dto.setProvinciaId(
                                rs.getInt("ProvinciaID"));

                dto.setProvincia(
                                rs.getString("Provincia"));

                dto.setCantonId(
                                rs.getInt("CantonID"));

                dto.setCanton(
                                rs.getString("Canton"));

                dto.setDistritoId(
                                rs.getInt("DistritoID"));

                dto.setDistrito(
                                rs.getString("Distrito"));

                dto.setBarrio(
                                rs.getString("Barrio"));

                dto.setOtrasSenas(
                                rs.getString("OtrasSenas"));

                dto.setProfesionOficio(
                                rs.getString(
                                                "ProfesionOficio"));

                dto.setActividadEconomicaId(
                                rs.getInt(
                                                "ActividadEconomicaID"));

                dto.setCodigoActividad(
                                rs.getString(
                                                "CodigoActividad"));

                dto.setActividadEconomica(
                                rs.getString(
                                                "ActividadEconomica"));

                dto.setCorreoElectronico(
                                rs.getString(
                                                "CorreoElectronico"));

                dto.setTelefono(
                                rs.getString("Telefono"));

                if (rs.getTimestamp(
                                "FechaRegistro") != null) {

                        dto.setFechaRegistro(
                                        rs.getTimestamp(
                                                        "FechaRegistro")
                                                        .toLocalDateTime());
                }

                dto.setEstado(
                                rs.getString("Estado"));

                return dto;
        }
}
