package ar.com.gav.backend.fotografo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Sesión Activa
 * Información de sesiones JWT del usuario
 */
public class SesionActivaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idSesion;
    private String ipAddress;
    private String navegador;
    private String sistemaOperativo;
    private Boolean esDispositivoMovil;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaExpiracion;
    private Long minutosRestantes;
    private Boolean activa;
    private Boolean esVigente;

    // Constructores
    public SesionActivaDTO() {
    }

    // Getters y Setters
    public Integer getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(Integer idSesion) {
        this.idSesion = idSesion;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNavegador() {
        return navegador;
    }

    public void setNavegador(String navegador) {
        this.navegador = navegador;
    }

    public String getSistemaOperativo() {
        return sistemaOperativo;
    }

    public void setSistemaOperativo(String sistemaOperativo) {
        this.sistemaOperativo = sistemaOperativo;
    }

    public Boolean getEsDispositivoMovil() {
        return esDispositivoMovil;
    }

    public void setEsDispositivoMovil(Boolean esDispositivoMovil) {
        this.esDispositivoMovil = esDispositivoMovil;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Long getMinutosRestantes() {
        return minutosRestantes;
    }

    public void setMinutosRestantes(Long minutosRestantes) {
        this.minutosRestantes = minutosRestantes;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }

    public Boolean getEsVigente() {
        return esVigente;
    }

    public void setEsVigente(Boolean esVigente) {
        this.esVigente = esVigente;
    }
}