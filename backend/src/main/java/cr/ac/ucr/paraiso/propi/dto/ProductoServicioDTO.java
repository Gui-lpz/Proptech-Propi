package cr.ac.ucr.paraiso.propi.dto;

import java.math.BigDecimal;

public class ProductoServicioDTO {
    private Integer productoServicioId;
    private String codigoCabys;
    private String descripcion;
    private Integer unidadMedidaId;
    private String unidadMedida;
    private BigDecimal precio;
    private Integer tipoImpuestoId;
    private String estado;

    public Integer getProductoServicioId() {
        return productoServicioId;
    }

    public void setProductoServicioId(Integer v) {
        productoServicioId = v;
    }

    public String getCodigoCabys() {
        return codigoCabys;
    }

    public void setCodigoCabys(String v) {
        codigoCabys = v;
    }

    public Integer getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public void setUnidadMedidaId(Integer v) {
        unidadMedidaId = v;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String v) {
        unidadMedida = v;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String v) {
        descripcion = v;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal v) {
        precio = v;
    }

    public Integer getTipoImpuestoId() {
        return tipoImpuestoId;
    }

    public void setTipoImpuestoId(Integer v) {
        tipoImpuestoId = v;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String v) {
        estado = v;
    }
}
