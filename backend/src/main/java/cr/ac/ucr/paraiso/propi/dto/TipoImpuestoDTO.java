package cr.ac.ucr.paraiso.propi.dto;

import java.math.BigDecimal;

public class TipoImpuestoDTO {

    private Integer tipoImpuestoId;
    private String codigo;
    private String descripcion;
    private BigDecimal porcentaje;
    private BigDecimal tarifa;
    private String estado;

    public Integer getTipoImpuestoId() {
        return tipoImpuestoId;
    }

    public void setTipoImpuestoId(Integer tipoImpuestoId) {
        this.tipoImpuestoId = tipoImpuestoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public BigDecimal getTarifa() {
        return tarifa;
    }

    public void setTarifa(BigDecimal tarifa) {
        this.tarifa = tarifa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
