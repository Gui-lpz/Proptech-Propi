package cr.ac.ucr.paraiso.propi.dto;

import java.math.BigDecimal;

public class ProductoServicioDTO {

    private Integer productoServicioId;

    private Integer cabysId;

    private String codigoCabys;

    private String descripcion;

    private String unidadMedida;

    private BigDecimal precio;

    private Integer tipoImpuestoId;

    private String estado;

    public Integer getProductoServicioId() {
        return productoServicioId;
    }

    public void setProductoServicioId(
            Integer productoServicioId) {

        this.productoServicioId = productoServicioId;
    }

    public Integer getCabysId() {
        return cabysId;
    }

    public void setCabysId(
            Integer cabysId) {

        this.cabysId = cabysId;
    }

    public String getCodigoCabys() {
        return codigoCabys;
    }

    public void setCodigoCabys(
            String codigoCabys) {

        this.codigoCabys = codigoCabys;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion) {

        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(
            String unidadMedida) {

        this.unidadMedida = unidadMedida;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(
            BigDecimal precio) {

        this.precio = precio;
    }

    public Integer getTipoImpuestoId() {
        return tipoImpuestoId;
    }

    public void setTipoImpuestoId(
            Integer tipoImpuestoId) {

        this.tipoImpuestoId = tipoImpuestoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(
            String estado) {

        this.estado = estado;
    }
}
