package cr.ac.ucr.paraiso.propi.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.paraiso.propi.data.FacturaRepository;
import cr.ac.ucr.paraiso.propi.dto.FacturaDetalleRequestDTO;
import cr.ac.ucr.paraiso.propi.dto.FacturaRequestDTO;

@Service
public class FacturaService {

        private final FacturaRepository repository;

        public FacturaService(FacturaRepository repository) {
                this.repository = repository;
        }

        @Transactional(rollbackFor = Exception.class)
        public Integer crear(
                        FacturaRequestDTO dto,
                        String usuario,
                        String sesion) {

                if (dto == null) {
                        throw new IllegalArgumentException(
                                        "Debe proporcionar los datos de la factura.");
                }

                if (dto.getClienteId() == null || dto.getClienteId() <= 0) {
                        throw new IllegalArgumentException(
                                        "Debe seleccionar un cliente.");
                }

                if (dto.getCondicionVentaId() == null || dto.getCondicionVentaId() <= 0) {
                        throw new IllegalArgumentException(
                                        "Debe seleccionar una condición de venta.");
                }

                if (dto.getMedioPagoId() == null || dto.getMedioPagoId() <= 0) {
                        throw new IllegalArgumentException(
                                        "Debe seleccionar un medio de pago.");
                }

                if (dto.getMonedaId() == null || dto.getMonedaId() <= 0) {
                        throw new IllegalArgumentException(
                                        "Debe seleccionar una moneda.");
                }

                if (dto.getTipoCambio() == null
                                || dto.getTipoCambio().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException(
                                        "El tipo de cambio debe ser mayor que cero.");
                }

                if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
                        throw new IllegalArgumentException(
                                        "La factura debe tener al menos un detalle.");
                }

                for (FacturaDetalleRequestDTO detalle : dto.getDetalles()) {

                        if (detalle == null
                                        || detalle.getProductoServicioId() == null
                                        || detalle.getProductoServicioId() <= 0) {
                                throw new IllegalArgumentException(
                                                "Debe seleccionar un producto o servicio válido.");
                        }

                        if (detalle.getCantidad() == null
                                        || detalle.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
                                throw new IllegalArgumentException(
                                                "La cantidad debe ser mayor que cero.");
                        }
                }

                // MedioPagoID = 1 => Efectivo
                if (dto.getMedioPagoId() == 1) {
                        if (dto.getMontoRecibido() == null
                                        || dto.getMontoRecibido().compareTo(BigDecimal.ZERO) <= 0) {
                                throw new IllegalArgumentException(
                                                "Debe indicar el monto recibido en efectivo.");
                        }
                }

                return repository.crear(dto, usuario, sesion);
        }

        @Transactional(readOnly = true)
        public List<Map<String, Object>> listar() {
                return repository.listar();
        }
}