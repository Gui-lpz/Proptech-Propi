
package cr.ac.ucr.paraiso.propi.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.propi.data.FacturaRepository;
import cr.ac.ucr.paraiso.propi.dto.FacturaDetalleRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;

@Service
public class FacturaService {

        private final FacturaRepository facturaRepository;
        private final JdbcTemplate jdbcTemplate;

        public FacturaService(
                        FacturaRepository facturaRepository,
                        JdbcTemplate jdbcTemplate) {

                this.facturaRepository = facturaRepository;
                this.jdbcTemplate = jdbcTemplate;
        }

        // REQ 002: Procedimientos almacenados
        // REQ 005: Auditoría con SESSION_CONTEXT

        @Transactional(rollbackFor = Exception.class)
        public Integer crear(
                        FacturaRequestDTO dto,
                        String usuarioAutenticado,
                        String sesionAutenticada) {

                // Validaciones de la factura
                validarFactura(dto);

                // Valida datos de autenticación recibidos
                if (usuarioAutenticado == null
                                || usuarioAutenticado.isBlank()) {

                        throw new SecurityException(
                                        "No se identificó al usuario autenticado.");
                }

                if (sesionAutenticada == null
                                || sesionAutenticada.isBlank()) {

                        throw new SecurityException(
                                        "No se identificó la sesión del usuario.");
                }

                // Convierte el ID de sesión a BIGINT de SQL Server
                Long sesionId;

                try {
                        sesionId = Long.valueOf(sesionAutenticada);
                } catch (NumberFormatException e) {

                        throw new SecurityException(
                                        "El identificador de sesión no es válido.", e);
                }

                if (sesionId <= 0) {
                        throw new SecurityException(
                                        "El identificador de sesión debe ser positivo.");
                }

                // Confirma que la sesión existe, corresponde
                // al usuario y todavía no ha sido cerrada.

                Integer sesionesValidas = jdbcTemplate.queryForObject(
                                """
                                                SELECT COUNT(*)
                                                FROM dbo.BitacoraAcceso
                                                WHERE BitacoraAccesoID = ?
                                                  AND NombreUsuario = ?
                                                  AND FechaHoraSalida IS NULL
                                                """,
                                Integer.class,
                                sesionId,
                                usuarioAutenticado);

                if (sesionesValidas == null || sesionesValidas != 1) {

                        throw new SecurityException(
                                        "La sesión no existe, está cerrada "
                                                        + "o no corresponde al usuario autenticado.");
                }

                // Establece el contexto en la conexión JDBC
                // asociada a la transacción.

                try {

                        jdbcTemplate.update(
                                        """
                                                        EXEC sys.sp_set_session_context
                                                            @key = N'BitacoraAccesoID',
                                                            @value = ?
                                                        """,
                                        sesionId);

                        // Crea encabezado, detalles, recalcula
                        // impuestos y guarda el monto recibido/vuelto.

                        return facturaRepository.crear(
                                        dto,
                                        usuarioAutenticado,
                                        sesionAutenticada);

                } finally {

                        // Limpia SESSION_CONTEXT antes de que
                        // la conexión vuelva al pool.

                        jdbcTemplate.update(
                                        """
                                                        EXEC sys.sp_set_session_context
                                                            @key = N'BitacoraAccesoID',
                                                            @value = NULL
                                                        """);
                }
        }

        // VALIDACIONES DE FACTURA

        private void validarFactura(FacturaRequestDTO dto) {

                if (dto == null) {
                        throw new IllegalArgumentException(
                                        "Debe proporcionar los datos de la factura.");
                }

                if (dto.getClienteId() == null
                                || dto.getClienteId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar un cliente.");
                }

                if (dto.getCondicionVentaId() == null
                                || dto.getCondicionVentaId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar una condición de venta.");
                }

                if (dto.getMedioPagoId() == null
                                || dto.getMedioPagoId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar un medio de pago.");
                }

                if (dto.getMonedaId() == null
                                || dto.getMonedaId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar una moneda.");
                }

                if (dto.getTipoCambio() == null
                                || dto.getTipoCambio().compareTo(BigDecimal.ZERO) <= 0) {

                        throw new IllegalArgumentException(
                                        "El tipo de cambio debe ser mayor que cero.");
                }

                if (dto.getDetalles() == null
                                || dto.getDetalles().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "La factura debe tener al menos un detalle.");
                }

                for (FacturaDetalleRequestDTO detalle : dto.getDetalles()) {

                        if (detalle == null
                                        || detalle.getProductoServicioId() == null
                                        || detalle.getProductoServicioId() <= 0) {

                                throw new IllegalArgumentException(
                                                "Debe seleccionar un producto "
                                                                + "o servicio válido.");
                        }

                        if (detalle.getCantidad() == null
                                        || detalle.getCantidad()
                                                        .compareTo(BigDecimal.ZERO) <= 0) {

                                throw new IllegalArgumentException(
                                                "La cantidad debe ser mayor que cero.");
                        }
                }

                // MedioPagoID = 1 corresponde a efectivo
                if (dto.getMedioPagoId() == 1) {

                        if (dto.getMontoRecibido() == null
                                        || dto.getMontoRecibido()
                                                        .compareTo(BigDecimal.ZERO) <= 0) {

                                throw new IllegalArgumentException(
                                                "Debe indicar el monto recibido en efectivo.");
                        }
                }
        }

        // LISTA FACTURAS

        @Transactional(readOnly = true)
        public List<Map<String, Object>> listar() {

                return facturaRepository.listar();
        }
}
