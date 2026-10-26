package cr.ac.ucr.paraiso.propi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FacturaRequestDTO {

    private Integer clienteId;
    private Integer condicionVentaId;
    private Integer medioPagoId;
    private Integer monedaId;
    private LocalDateTime fecha;
    private String condicionVenta;
    private String medioPago;
    private String moneda;
    private BigDecimal tipoCambio;
    private String observaciones;
    private List<FacturaDetalleRequestDTO> detalles;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getCondicionVenta() {
        return condicionVenta;
    }

    public void setCondicionVenta(String condicionVenta) {
        this.condicionVenta = condicionVenta;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<FacturaDetalleRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<FacturaDetalleRequestDTO> detalles) {
        this.detalles = detalles;
    }

    public Integer getCondicionVentaId() {
        return condicionVentaId;
    }

    public void setCondicionVentaId(Integer v) {
        condicionVentaId = v;
    }

    public Integer getMedioPagoId() {
        return medioPagoId;
    }

    public void setMedioPagoId(Integer v) {
        medioPagoId = v;
    }

    public Integer getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(Integer v) {
        monedaId = v;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime v) {
        fecha = v;
    }
}
