package cr.ac.ucr.paraiso.propi.dto;

import java.time.LocalDateTime;

public class ClienteDTO {

    private Integer clienteId;

    private String nombre;

    private String tipoIdentificacion;

    private String numeroIdentificacion;

    private Integer provinciaId;

    private String provincia;

    private Integer cantonId;

    private String canton;

    private Integer distritoId;

    private String distrito;

    private String barrio;

    private String otrasSenas;

    private String profesionOficio;

    private Integer actividadEconomicaId;

    private String codigoActividad;

    private String actividadEconomica;

    private String correoElectronico;

    private String telefono;

    private LocalDateTime fechaRegistro;

    private String estado;

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(
            Integer clienteId) {

        this.clienteId = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(
            String nombre) {

        this.nombre = nombre;
    }

    public String getTipoIdentificacion() {
        return tipoIdentificacion;
    }

    public void setTipoIdentificacion(
            String tipoIdentificacion) {

        this.tipoIdentificacion = tipoIdentificacion;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(
            String numeroIdentificacion) {

        this.numeroIdentificacion = numeroIdentificacion;
    }

    public Integer getProvinciaId() {
        return provinciaId;
    }

    public void setProvinciaId(
            Integer provinciaId) {

        this.provinciaId = provinciaId;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(
            String provincia) {

        this.provincia = provincia;
    }

    public Integer getCantonId() {
        return cantonId;
    }

    public void setCantonId(
            Integer cantonId) {

        this.cantonId = cantonId;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(
            String canton) {

        this.canton = canton;
    }

    public Integer getDistritoId() {
        return distritoId;
    }

    public void setDistritoId(
            Integer distritoId) {

        this.distritoId = distritoId;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(
            String distrito) {

        this.distrito = distrito;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(
            String barrio) {

        this.barrio = barrio;
    }

    public String getOtrasSenas() {
        return otrasSenas;
    }

    public void setOtrasSenas(
            String otrasSenas) {

        this.otrasSenas = otrasSenas;
    }

    public String getProfesionOficio() {
        return profesionOficio;
    }

    public void setProfesionOficio(
            String profesionOficio) {

        this.profesionOficio = profesionOficio;
    }

    public Integer getActividadEconomicaId() {
        return actividadEconomicaId;
    }

    public void setActividadEconomicaId(
            Integer actividadEconomicaId) {

        this.actividadEconomicaId = actividadEconomicaId;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public void setCodigoActividad(
            String codigoActividad) {

        this.codigoActividad = codigoActividad;
    }

    public String getActividadEconomica() {
        return actividadEconomica;
    }

    public void setActividadEconomica(
            String actividadEconomica) {

        this.actividadEconomica = actividadEconomica;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(
            String correoElectronico) {

        this.correoElectronico = correoElectronico;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(
            String telefono) {

        this.telefono = telefono;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(
            LocalDateTime fechaRegistro) {

        this.fechaRegistro = fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(
            String estado) {

        this.estado = estado;
    }
}
