
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

        /* CONSTRUCTOR */

        public FacturaRepository(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        /*
         * CREAR FACTURA
         */

        public Integer crear(
                        FacturaRequestDTO dto,
                        String usuario,
                        String sesion) {

                LocalDateTime fecha = LocalDateTime.now();

                // 1. Crear encabezado de factura.
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
                                                Types.INTEGER, // FacturaID
                                                Types.INTEGER, // ClienteID
                                                Types.TIMESTAMP, // Fecha
                                                Types.INTEGER, // CondicionVentaID
                                                Types.INTEGER, // MedioPagoID
                                                Types.INTEGER, // MonedaID
                                                Types.DECIMAL, // TipoCambio
                                                Types.VARCHAR, // Observaciones
                                                Types.VARCHAR, // UsuarioRegistro
                                                Types.VARCHAR, // SesionRegistro
                                                Types.DECIMAL, // MontoRecibido
                                                Types.DECIMAL // Vuelto
                                },
                                (rs, rowNum) -> rs.getInt("FacturaID"));

                if (facturaId == null || facturaId <= 0) {
                        throw new IllegalStateException(
                                        "No se obtuvo un FacturaID válido.");
                }

                // 2. Insertar detalles de factura.
                for (FacturaDetalleRequestDTO detalle : dto.getDetalles()) {
                        insertarDetalle(facturaId, detalle);
                }

                // 3. Recalcular totales e impuestos.
                jdbcTemplate.update(
                                "EXEC dbo.sp_Factura_Recalcular ?",
                                facturaId);

                // 4. Consultar total final de factura.
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

                totalFactura = totalFactura.setScale(
                                2,
                                RoundingMode.HALF_UP);

                // 5. Calcular pago y vuelto.
                BigDecimal montoRecibidoFinal = calcularMontoRecibidoFinal(
                                dto,
                                totalFactura);

                BigDecimal vueltoFinal = calcularVueltoFinal(
                                dto,
                                totalFactura,
                                montoRecibidoFinal);

                // 6. Actualizar factura con datos de cobro.
                Integer facturaIdActualizada = jdbcTemplate.queryForObject(
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
                                                Types.INTEGER, // FacturaID
                                                Types.INTEGER, // ClienteID
                                                Types.TIMESTAMP, // Fecha
                                                Types.INTEGER, // CondicionVentaID
                                                Types.INTEGER, // MedioPagoID
                                                Types.INTEGER, // MonedaID
                                                Types.DECIMAL, // TipoCambio
                                                Types.VARCHAR, // Observaciones
                                                Types.VARCHAR, // UsuarioRegistro
                                                Types.VARCHAR, // SesionRegistro
                                                Types.DECIMAL, // MontoRecibido
                                                Types.DECIMAL // Vuelto
                                },
                                (rs, rowNum) -> rs.getInt("FacturaID"));

                // 7. Comprobar que se actualizó la misma factura.
                if (!facturaId.equals(facturaIdActualizada)) {
                        throw new IllegalStateException(
                                        "No se confirmó la actualización del pago de la factura.");
                }

                // 8. Devolver el identificador de la factura registrada.
                return facturaId;
        }

        /* INSERTAR DETALLE DE FACTURA */

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

                // Calcular cantidad.
                BigDecimal cantidad = detalle.getCantidad()
                                .setScale(2, RoundingMode.HALF_UP);

                // Calcular subtotal.
                BigDecimal subtotalLinea = producto.precio()
                                .multiply(cantidad)
                                .setScale(4, RoundingMode.HALF_UP);

                // Calcular impuesto.
                BigDecimal montoImpuesto = subtotalLinea
                                .multiply(producto.porcentaje())
                                .divide(
                                                new BigDecimal("100"),
                                                4,
                                                RoundingMode.HALF_UP);

                // El procedimiento devuelve DetalleID,
                // por eso se utiliza queryForObject().
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

        /* CALCULAR MONTO RECIBIDO */

        private BigDecimal calcularMontoRecibidoFinal(
                        FacturaRequestDTO dto,
                        BigDecimal totalFactura) {

                // Pago en efectivo.
                if (dto.getMedioPagoId() != null
                                && dto.getMedioPagoId() == 1) {

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

                // Tarjeta, SINPE, transferencia y otros pagos.
                return totalFactura.setScale(
                                2,
                                RoundingMode.HALF_UP);
        }

        /*
         * CALCULAR VUELTO
         * 
         * Solo para pagos en efectivo.
         */

        private BigDecimal calcularVueltoFinal(
                        FacturaRequestDTO dto,
                        BigDecimal totalFactura,
                        BigDecimal montoRecibidoFinal) {

                // Pago en efectivo.
                if (dto.getMedioPagoId() != null
                                && dto.getMedioPagoId() == 1) {

                        return montoRecibidoFinal
                                        .subtract(totalFactura)
                                        .setScale(2, RoundingMode.HALF_UP);
                }

                // Otros medios de pago no generan vuelto.
                return BigDecimal.ZERO.setScale(
                                2,
                                RoundingMode.HALF_UP);
        }

        /*
         * LISTAR FACTURAS
         * 
         * Procedimiento:
         * dbo.sp_Facturas_Listar
         */

        public List<Map<String, Object>> listar() {

                return jdbcTemplate.queryForList(
                                "EXEC dbo.sp_Facturas_Listar");
        }

        /*
         * ELIMINAR FACTURA
         * 
         * Procedimiento:
         * dbo.sp_Facturas_Eliminar
         */

        public void eliminar(Integer facturaId) {

                if (facturaId == null || facturaId <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe proporcionar un FacturaID valido.");
                }

                jdbcTemplate.update(
                                "EXEC dbo.sp_Facturas_Eliminar @FacturaID = ?",
                                facturaId);
        }

        /* ESTRUCTURA INTERNA DE DATOS DEL PRODUCTO */

        private record DatosProducto(
                        BigDecimal precio,
                        BigDecimal porcentaje) {
        }

        /* ACTUALIZAR FACTURA COMPLETA */

        public void actualizar(
                        Integer facturaId,
                        FacturaRequestDTO dto,
                        String usuario,
                        String sesion) {

                // 1. Recuperar la fecha original y bloquear
                // la factura durante la transaccion.
                Timestamp fechaOriginal = jdbcTemplate.queryForObject(
                                """
                                                SELECT Fecha
                                                FROM dbo.Facturas WITH (UPDLOCK, HOLDLOCK)
                                                WHERE FacturaID = ?
                                                """,
                                Timestamp.class,
                                facturaId);

                if (fechaOriginal == null) {
                        throw new IllegalStateException(
                                        "No se encontró la fecha de la factura.");
                }

                // 2. No modificar facturas que ya tengan
                // notas de credito o debito relacionadas.
                Integer cantidadNotas = jdbcTemplate.queryForObject(
                                """
                                                SELECT COUNT(*)
                                                FROM dbo.NotasCreditoDebito
                                                WHERE FacturaID = ?
                                                """,
                                Integer.class,
                                facturaId);

                if (cantidadNotas != null && cantidadNotas > 0) {
                        throw new IllegalStateException(
                                        "No se puede modificar una factura que " +
                                                        "tiene notas de crédito o débito asociadas.");
                }

                // 3. Obtener los detalles anteriores.
                List<Integer> detallesAnteriores = jdbcTemplate.queryForList(
                                """
                                                SELECT DetalleID
                                                FROM dbo.FacturasDetalle
                                                WHERE FacturaID = ?
                                                """,
                                Integer.class,
                                facturaId);

                // 4. Eliminar los detalles anteriores
                // utilizando el procedimiento almacenado.
                for (Integer detalleId : detallesAnteriores) {

                        jdbcTemplate.update(
                                        "EXEC dbo.sp_FacturasDetalle_Eliminar ?",
                                        detalleId);
                }

                // 5. Insertar los nuevos detalles.
                for (FacturaDetalleRequestDTO detalle : dto.getDetalles()) {

                        insertarDetalle(facturaId, detalle);
                }

                // 6. Recalcular subtotal e impuestos.
                jdbcTemplate.update(
                                "EXEC dbo.sp_Factura_Recalcular ?",
                                facturaId);

                // 7. Consultar el nuevo total.
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
                                        "No se pudo calcular el nuevo total.");
                }

                totalFactura = totalFactura.setScale(
                                2,
                                RoundingMode.HALF_UP);

                // 8. Calcular monto recibido y vuelto.
                BigDecimal recibido = calcularMontoRecibidoFinal(dto, totalFactura);

                BigDecimal vuelto = calcularVueltoFinal(dto, totalFactura, recibido);

                // 9. Actualizar encabezado conservando
                // el FacturaID y la fecha originales.
                Integer idActualizado = jdbcTemplate.queryForObject(
                                """
                                                EXEC dbo.sp_Facturas_Guardar
                                                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                                                """,
                                new Object[] {
                                                facturaId,
                                                dto.getClienteId(),
                                                fechaOriginal,
                                                dto.getCondicionVentaId(),
                                                dto.getMedioPagoId(),
                                                dto.getMonedaId(),
                                                dto.getTipoCambio(),
                                                dto.getObservaciones(),
                                                usuario,
                                                sesion,
                                                recibido,
                                                vuelto
                                },
                                new int[] {
                                                Types.INTEGER,
                                                Types.INTEGER,
                                                Types.TIMESTAMP,
                                                Types.INTEGER,
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

                if (!facturaId.equals(idActualizado)) {
                        throw new IllegalStateException(
                                        "No se confirmó la actualización de la factura.");
                }
        }

}
