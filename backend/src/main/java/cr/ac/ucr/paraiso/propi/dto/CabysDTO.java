package cr.ac.ucr.paraiso.propi.dto;

public class CabysDTO {

    private Integer cabysId;

    private String codigoCABYS;

    private String descripcion;

    private String estado;

    public Integer getCabysId() {
        return cabysId;
    }

    public void setCabysId(
            Integer cabysId) {

        this.cabysId = cabysId;
    }

    public String getCodigoCABYS() {
        return codigoCABYS;
    }

    public void setCodigoCABYS(
            String codigoCABYS) {

        this.codigoCABYS = codigoCABYS;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(
            String descripcion) {

        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(
            String estado) {

        this.estado = estado;
    }
}
