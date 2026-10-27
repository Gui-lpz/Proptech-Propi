package cr.ac.ucr.paraiso.propi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FacturaComprobanteDTO {

    private Integer facturaId;
    private String numeroFactura;
    private String claveSimulada;
    private LocalDateTime fecha;

    private String cajero;

    private String clienteNombre;
    private String clienteIdentificacion;

    private Integer monedaId;
    private String monedaCodigo;
    private String monedaDescripcion;

    private Integer medioPagoId;
    private String medioPagoCodigo;
    private String medioPagoDescripcion;

    private String observaciones;

    private BigDecimal subtotal;
    private BigDecimal totalImpuestos;
    private BigDecimal total;

    private BigDecimal montoRecibido;
    private BigDecimal vuelto;

    private List<Detalle> detalles = new ArrayList<>();

    public Integer getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Integer facturaId) {
        this.facturaId = facturaId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public String getClaveSimulada() {
        return claveSimulada;
    }

    public void setClaveSimulada(String claveSimulada) {
        this.claveSimulada = claveSimulada;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getCajero() {
        return cajero;
    }

    public void setCajero(String cajero) {
        this.cajero = cajero;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteIdentificacion() {
        return clienteIdentificacion;
    }

    public void setClienteIdentificacion(String clienteIdentificacion) {
        this.clienteIdentificacion = clienteIdentificacion;
    }

    public Integer getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(Integer monedaId) {
        this.monedaId = monedaId;
    }

    public String getMonedaCodigo() {
        return monedaCodigo;
    }

    public void setMonedaCodigo(String monedaCodigo) {
        this.monedaCodigo = monedaCodigo;
    }

    public String getMonedaDescripcion() {
        return monedaDescripcion;
    }

    public void setMonedaDescripcion(String monedaDescripcion) {
        this.monedaDescripcion = monedaDescripcion;
    }

    public Integer getMedioPagoId() {
        return medioPagoId;
    }

    public void setMedioPagoId(Integer medioPagoId) {
        this.medioPagoId = medioPagoId;
    }

    public String getMedioPagoCodigo() {
        return medioPagoCodigo;
    }

    public void setMedioPagoCodigo(String medioPagoCodigo) {
        this.medioPagoCodigo = medioPagoCodigo;
    }

    public String getMedioPagoDescripcion() {
        return medioPagoDescripcion;
    }

    public void setMedioPagoDescripcion(String medioPagoDescripcion) {
        this.medioPagoDescripcion = medioPagoDescripcion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(BigDecimal totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(BigDecimal montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }

    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }

    public static class Detalle {

        private String descripcion;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal montoLinea;
        private BigDecimal montoImpuesto;

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public BigDecimal getCantidad() {
            return cantidad;
        }

        public void setCantidad(BigDecimal cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public BigDecimal getMontoLinea() {
            return montoLinea;
        }

        public void setMontoLinea(BigDecimal montoLinea) {
            this.montoLinea = montoLinea;
        }

        public BigDecimal getMontoImpuesto() {
            return montoImpuesto;
        }

        public void setMontoImpuesto(BigDecimal montoImpuesto) {
            this.montoImpuesto = montoImpuesto;
        }
    }
}