
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

        /* CONSTRUCTOR */

        public FacturaService(
                        FacturaRepository facturaRepository,
                        JdbcTemplate jdbcTemplate) {

                this.facturaRepository = facturaRepository;
                this.jdbcTemplate = jdbcTemplate;
        }

        /*
         * FACTURA
         * 
         * REQ 002: Procedimientos almacenados
         * REQ 005: Auditoria con SESSION_CONTEXT
         */

        @Transactional(rollbackFor = Exception.class)
        public Integer crear(
                        FacturaRequestDTO dto,
                        String usuarioAutenticado,
                        String sesionAutenticada) {

                // Validaciones de la factura.
                validarFactura(dto);

                // Validar el usuario autenticado.
                if (usuarioAutenticado == null ||
                                usuarioAutenticado.isBlank()) {

                        throw new SecurityException(
                                        "No se identificó al usuario autenticado.");
                }

                // Validar sesion.
                if (sesionAutenticada == null ||
                                sesionAutenticada.isBlank()) {

                        throw new SecurityException(
                                        "No se identificó la sesión del usuario.");
                }

                // Convertir el identificador de sesion
                // al tipo BIGINT de SQL Server.
                Long sesionId;

                try {

                        sesionId = Long.valueOf(sesionAutenticada);

                } catch (NumberFormatException e) {

                        throw new SecurityException(
                                        "El identificador de sesión no es válido.",
                                        e);
                }

                // Validar identificador positivo.
                if (sesionId <= 0) {

                        throw new SecurityException(
                                        "El identificador de sesión debe ser positivo.");
                }

                // Comprobar que la sesion exista,
                // pertenezca al usuario y siga abierta.
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

                if (sesionesValidas == null ||
                                sesionesValidas != 1) {

                        throw new SecurityException(
                                        "La sesión no existe, está cerrada "
                                                        + "o no corresponde al usuario autenticado.");
                }

                // Configurar SESSION_CONTEXT en la conexion
                // asociada a la transaccion.
                try {

                        jdbcTemplate.update(
                                        """
                                                        EXEC sys.sp_set_session_context
                                                            @key = N'BitacoraAccesoID',
                                                            @value = ?
                                                        """,
                                        sesionId);

                        // Crear encabezado, detalles,
                        // recalcular impuestos y registrar
                        // el pago y el vuelto.
                        return facturaRepository.crear(
                                        dto,
                                        usuarioAutenticado,
                                        sesionAutenticada);

                } finally {

                        // Limpiar SESSION_CONTEXT para evitar
                        // que el contexto de la sesion permanezca
                        // en una conexion reutilizada.
                        jdbcTemplate.update(
                                        """
                                                        EXEC sys.sp_set_session_context
                                                            @key = N'BitacoraAccesoID',
                                                            @value = NULL
                                                        """);
                }
        }

        /* LISTAR FACTURAS */

        @Transactional(readOnly = true)
        public List<Map<String, Object>> listar() {

                return facturaRepository.listar();
        }

        /*
         * ELIMINAR FACTURA
         * Procedimiento:
         * dbo.sp_Facturas_Eliminar
         */

        @Transactional(rollbackFor = Exception.class)
        public void eliminar(Integer facturaId) {

                // Validar identificador de factura.
                if (facturaId == null || facturaId <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe proporcionar un FacturaID válido.");
                }

                // Ejecutar eliminacion mediante Repository.
                facturaRepository.eliminar(facturaId);
        }

        /* 4. VALIDACIONES DE FACTURA */

        private void validarFactura(FacturaRequestDTO dto) {

                // Verificar que exista el DTO.
                if (dto == null) {

                        throw new IllegalArgumentException(
                                        "Debe proporcionar los datos de la factura.");
                }

                // Validar cliente.
                if (dto.getClienteId() == null ||
                                dto.getClienteId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar un cliente.");
                }

                // Validar condicion de venta.
                if (dto.getCondicionVentaId() == null ||
                                dto.getCondicionVentaId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar una condición de venta.");
                }

                // Validar medio de pago.
                if (dto.getMedioPagoId() == null ||
                                dto.getMedioPagoId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar un medio de pago.");
                }

                // Validar moneda.
                if (dto.getMonedaId() == null ||
                                dto.getMonedaId() <= 0) {

                        throw new IllegalArgumentException(
                                        "Debe seleccionar una moneda.");
                }

                // Validar tipo de cambio.
                if (dto.getTipoCambio() == null ||
                                dto.getTipoCambio()
                                                .compareTo(BigDecimal.ZERO) <= 0) {

                        throw new IllegalArgumentException(
                                        "El tipo de cambio debe ser mayor que cero.");
                }

                // Verificar que existan detalles.
                if (dto.getDetalles() == null ||
                                dto.getDetalles().isEmpty()) {

                        throw new IllegalArgumentException(
                                        "La factura debe tener al menos un detalle.");
                }

                // Validar cada detalle de factura.
                for (FacturaDetalleRequestDTO detalle : dto.getDetalles()) {

                        // Validar producto o servicio.
                        if (detalle == null ||
                                        detalle.getProductoServicioId() == null ||
                                        detalle.getProductoServicioId() <= 0) {

                                throw new IllegalArgumentException(
                                                "Debe seleccionar un producto "
                                                                + "o servicio válido.");
                        }

                        // Validar cantidad.
                        if (detalle.getCantidad() == null ||
                                        detalle.getCantidad()
                                                        .compareTo(BigDecimal.ZERO) <= 0) {

                                throw new IllegalArgumentException(
                                                "La cantidad debe ser mayor que cero.");
                        }
                }

                // MedioPagoID = 1 corresponde a efectivo.
                if (dto.getMedioPagoId() == 1) {

                        // Validar monto recibido.
                        if (dto.getMontoRecibido() == null ||
                                        dto.getMontoRecibido()
                                                        .compareTo(BigDecimal.ZERO) <= 0) {

                                throw new IllegalArgumentException(
                                                "Debe indicar el monto recibido en efectivo.");
                        }
                }
        }

        /*
         * ACTUALIZAR FACTURA COMPLETA
         * 
         */

        @Transactional(rollbackFor = Exception.class)
        public void actualizar(
                        Integer facturaId,
                        FacturaRequestDTO dto,
                        String usuarioAutenticado,
                        String sesionAutenticada) {

                // 1. Validar identificador.
                if (facturaId == null || facturaId <= 0) {
                        throw new IllegalArgumentException(
                                        "Debe proporcionar un FacturaID válido.");
                }

                // 2. Reutilizar las validaciones originales.
                validarFactura(dto);

                // 3. Validar usuario y sesion.
                if (usuarioAutenticado == null ||
                                usuarioAutenticado.isBlank()) {

                        throw new SecurityException(
                                        "No se identificó al usuario autenticado.");
                }

                if (sesionAutenticada == null ||
                                sesionAutenticada.isBlank()) {

                        throw new SecurityException(
                                        "No se identificó la sesión del usuario.");
                }

                // 4. Convertir la sesion a BIGINT.
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

                // 5. Verificar que la sesion continúe activa.
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
                                        "La sesión no existe, está cerrada " +
                                                        "o no corresponde al usuario autenticado.");
                }

                // 6. Establecer contexto de auditoria.
                try {

                        jdbcTemplate.update(
                                        """
                                                        EXEC sys.sp_set_session_context
                                                            @key = N'BitacoraAccesoID',
                                                            @value = ?
                                                        """,
                                        sesionId);

                        // 7. Actualizar la factura y sus detalles.
                        facturaRepository.actualizar(
                                        facturaId,
                                        dto,
                                        usuarioAutenticado,
                                        sesionAutenticada);

                } finally {

                        // 8. Limpiar el contexto antes de devolver
                        // la conexion al pool.
                        jdbcTemplate.update(
                                        """
                                                        EXEC sys.sp_set_session_context
                                                            @key = N'BitacoraAccesoID',
                                                            @value = NULL
                                                        """);
                }
        }

}
