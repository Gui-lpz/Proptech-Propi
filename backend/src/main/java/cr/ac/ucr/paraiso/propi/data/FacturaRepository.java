package cr.ac.ucr.paraiso.propi.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.FacturaDetalleRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;

@Repository
public class FacturaRepository {

    private final JdbcTemplate jdbcTemplate;

    public FacturaRepository(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer crear(
            FacturaRequestDTO dto,
            String usuario,
            String sesion) {

        return jdbcTemplate.execute(

                (ConnectionCallback<Integer>) connection -> {

                    String sql = """
                            EXEC facturacion.sp_Factura_Crear
                                ?, ?, ?, ?, ?, ?, ?, ?
                            """;

                    try (
                            PreparedStatement ps = connection.prepareStatement(sql)) {

                        ps.setInt(
                                1,
                                dto.getClienteId());

                        ps.setString(
                                2,
                                dto.getCondicionVenta());

                        ps.setString(
                                3,
                                dto.getMedioPago());

                        ps.setString(
                                4,
                                dto.getMoneda());

                        ps.setBigDecimal(
                                5,
                                dto.getTipoCambio());

                        ps.setString(
                                6,
                                dto.getObservaciones());

                        ps.setString(
                                7,
                                usuario);

                        ps.setString(
                                8,
                                sesion);

                        try (
                                ResultSet rs = ps.executeQuery()) {

                            if (!rs.next()) {

                                throw new RuntimeException(
                                        "No se obtuvo FacturaID.");
                            }

                            Integer facturaId = rs.getInt(
                                    "FacturaID");

                            insertarDetalles(
                                    connection,
                                    facturaId,
                                    dto.getDetalles(),
                                    usuario,
                                    sesion);

                            return facturaId;
                        }
                    }
                });
    }

    private void insertarDetalles(
            Connection connection,
            Integer facturaId,
            List<FacturaDetalleRequestDTO> detalles,
            String usuario,
            String sesion)
            throws SQLException {

        if (detalles == null
                || detalles.isEmpty()) {

            throw new IllegalArgumentException(
                    "La factura debe tener al menos un detalle.");
        }

        String sql = """
                EXEC facturacion.sp_FacturaDetalle_Insertar
                    ?, ?, ?, ?, ?
                """;

        for (FacturaDetalleRequestDTO detalle : detalles) {

            try (
                    PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(
                        1,
                        facturaId);

                ps.setInt(
                        2,
                        detalle.getProductoServicioId());

                ps.setBigDecimal(
                        3,
                        detalle.getCantidad());

                ps.setString(
                        4,
                        usuario);

                ps.setString(
                        5,
                        sesion);

                ps.executeUpdate();
            }
        }
    }

    public List<java.util.Map<String, Object>> listar() {

        return jdbcTemplate.queryForList(
                "EXEC facturacion.sp_Factura_Listar");
    }
}