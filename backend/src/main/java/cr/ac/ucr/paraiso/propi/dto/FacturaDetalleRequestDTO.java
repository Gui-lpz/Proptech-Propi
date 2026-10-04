package cr.ac.ucr.paraiso.propi.dto;

import java.math.BigDecimal;

public class FacturaDetalleRequestDTO {

    private Integer productoServicioId;
    private BigDecimal cantidad;

    public Integer getProductoServicioId() {
        return productoServicioId;
    }

    public void setProductoServicioId(Integer productoServicioId) {
        this.productoServicioId = productoServicioId;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
}
