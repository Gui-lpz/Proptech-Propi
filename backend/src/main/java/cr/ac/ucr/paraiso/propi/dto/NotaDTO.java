package cr.ac.ucr.paraiso.propi.dto;

public class NotaDTO {

    private Integer notaId;
    private String codigo;
    private Integer facturaId;
    private String tipo;
    private String motivo;

    public Integer getNotaId() {
        return notaId;
    }

    public void setNotaId(Integer notaId) {
        this.notaId = notaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Integer facturaId) {
        this.facturaId = facturaId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
