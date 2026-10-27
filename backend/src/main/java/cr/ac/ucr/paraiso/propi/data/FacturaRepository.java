package cr.ac.ucr.paraiso.propi.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.paraiso.propi.dto.FacturaDetalleRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;

@Repository
public class FacturaRepository {

        private final JdbcTemplate jdbcTemplate;

        public FacturaRepository(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        public Integer crear(
                        FacturaRequestDTO dto,
                        String usuario,
                        String sesion) {

                LocalDateTime fecha = LocalDateTime.now();

                // 1. Crear encabezado de factura
                Integer facturaId = jdbcTemplate.queryForObject(
                                """
                                                EXEC dbo.sp_Facturas_Guardar
                                                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                                                """,
                                new Object[] {
                                                new SqlParameterValue(Types.INTEGER, null),
                                                dto.getClienteId(),
                                                Timestamp.valueOf(fecha),
                                                dto.getCondicionVentaId(),
                                                dto.getMedioPagoId(),
                                                dto.getMonedaId(),
                                                dto.getTipoCambio(),
                                                dto.getObservaciones(),
                                                usuario,
                                                sesion,
                                                null,
                                                null
                                },
                                new int[] {
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.TIMESTAMP,
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.DECIMAL,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.DECIMAL,
                                                Types.DECIMAL
                                },
                                (rs, rowNum) -> rs.getInt("FacturaID"));

                if (facturaId == null) {
                        throw new IllegalStateException(
                                        "No se obtuvo el FacturaID.");
                }

                // 2. Insertar detalles
                for (FacturaDetalleRequestDTO detalle : dto.getDetalles()) {
                        insertarDetalle(facturaId, detalle);
                }

                // 3. Recalcular totales
                jdbcTemplate.update(
                                "EXEC dbo.sp_Factura_Recalcular ?",
                                facturaId);

                // 4. Consultar el total final de la factura
                BigDecimal totalFactura = jdbcTemplate.queryForObject(
                                """
                                                SELECT MontoTotal
                                                FROM dbo.Facturas
                                                WHERE FacturaID = ?
                                                """,
                                BigDecimal.class,
                                facturaId);

                if (totalFactura == null) {
                        throw new IllegalStateException(
                                        "No se pudo obtener el total final de la factura.");
                }

                totalFactura = totalFactura.setScale(2, RoundingMode.HALF_UP);

                // 5. Calcular pago y vuelto
                BigDecimal montoRecibidoFinal = calcularMontoRecibidoFinal(dto, totalFactura);
                BigDecimal vueltoFinal = calcularVueltoFinal(dto, totalFactura, montoRecibidoFinal);

                // 6. Actualizar factura con usuario, sesión, monto recibido y vuelto
                jdbcTemplate.queryForObject(
                                """
                                                EXEC dbo.sp_Facturas_Guardar
                                                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                                                """,
                                new Object[] {
                                                facturaId,
                                                dto.getClienteId(),
                                                Timestamp.valueOf(fecha),
                                                dto.getCondicionVentaId(),
                                                dto.getMedioPagoId(),
                                                dto.getMonedaId(),
                                                dto.getTipoCambio(),
                                                dto.getObservaciones(),
                                                usuario,
                                                sesion,
                                                montoRecibidoFinal,
                                                vueltoFinal
                                },
                                new int[] {
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.TIMESTAMP,
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.DECIMAL,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.VARCHAR,
                                                Types.DECIMAL,
                                                Types.DECIMAL
                                },
                                (rs, rowNum) -> rs.getInt("FacturaID"));

                return facturaId;
        }

        private void insertarDetalle(
                        Integer facturaId,
                        FacturaDetalleRequestDTO detalle) {

                DatosProducto producto = jdbcTemplate.queryForObject(
                                """
                                                SELECT
                                                    p.Precio,
                                                    t.Porcentaje
                                                FROM dbo.ProductoServicio p
                                                INNER JOIN dbo.TipoImpuesto t
                                                    ON p.TipoImpuestoID = t.TipoImpuestoID
                                                WHERE p.ProductoServicioID = ?
                                                  AND p.Estado = 'A'
                                                """,
                                (rs, rowNum) -> new DatosProducto(
                                                rs.getBigDecimal("Precio"),
                                                rs.getBigDecimal("Porcentaje")),
                                detalle.getProductoServicioId());

                if (producto == null
                                || producto.precio() == null
                                || producto.porcentaje() == null) {
                        throw new IllegalArgumentException(
                                        "No se pudo obtener el precio o impuesto del producto.");
                }

                BigDecimal cantidad = detalle.getCantidad().setScale(2, RoundingMode.HALF_UP);

                BigDecimal subtotalLinea = producto.precio()
                                .multiply(cantidad)
                                .setScale(4, RoundingMode.HALF_UP);

                BigDecimal montoImpuesto = subtotalLinea
                                .multiply(producto.porcentaje())
                                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                // OJO:
                // sp_FacturasDetalle_Guardar devuelve un resultset con DetalleID,
                // por eso se usa queryForObject y NO update.
                jdbcTemplate.queryForObject(
                                """
                                                EXEC dbo.sp_FacturasDetalle_Guardar
                                                    ?, ?, ?, ?, ?, ?
                                                """,
                                new Object[] {
                                                new SqlParameterValue(Types.INTEGER, null),
                                                facturaId,
                                                detalle.getProductoServicioId(),
                                                cantidad,
                                                montoImpuesto,
                                                subtotalLinea
                                },
                                new int[] {
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.DECIMAL,
                                                Types.DECIMAL,
                                                Types.DECIMAL
                                },
                                (rs, rowNum) -> rs.getInt("DetalleID"));
        }

        private BigDecimal calcularMontoRecibidoFinal(
                        FacturaRequestDTO dto,
                        BigDecimal totalFactura) {

                // Efectivo
                if (dto.getMedioPagoId() != null && dto.getMedioPagoId() == 1) {

                        if (dto.getMontoRecibido() == null) {
                                throw new IllegalArgumentException(
                                                "Debe indicar el monto recibido en efectivo.");
                        }

                        BigDecimal recibido = dto.getMontoRecibido()
                                        .setScale(2, RoundingMode.HALF_UP);

                        if (recibido.compareTo(totalFactura) < 0) {
                                throw new IllegalArgumentException(
                                                "El monto recibido no puede ser menor al total de la factura.");
                        }

                        return recibido;
                }

                // Para tarjeta, SINPE, transferencia, etc.
                // normalmente se paga exacto.
                return totalFactura.setScale(2, RoundingMode.HALF_UP);
        }

        private BigDecimal calcularVueltoFinal(
                        FacturaRequestDTO dto,
                        BigDecimal totalFactura,
                        BigDecimal montoRecibidoFinal) {

                // Efectivo
                if (dto.getMedioPagoId() != null && dto.getMedioPagoId() == 1) {
                        return montoRecibidoFinal
                                        .subtract(totalFactura)
                                        .setScale(2, RoundingMode.HALF_UP);
                }

                return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        public List<Map<String, Object>> listar() {
                return jdbcTemplate.queryForList(
                                "EXEC dbo.sp_Facturas_Listar");
        }

        private record DatosProducto(
                        BigDecimal precio,
                        BigDecimal porcentaje) {
        }
}