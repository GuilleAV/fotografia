package ar.com.gav.backend.fotografo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Configuración
 * Parámetros configurables del sistema
 */
public class ConfiguracionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idConfig;
    private String clave;
    private String valor;
    private String tipo;
    private String descripcion;
    private LocalDateTime fechaActualizacion;

    // Constructores
    public ConfiguracionDTO() {
    }

    public ConfiguracionDTO(String clave, String valor, String tipo) {
        this.clave = clave;
        this.valor = valor;
        this.tipo = tipo;
    }

    // Getters y Setters
    public Integer getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Integer idConfig) {
        this.idConfig = idConfig;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}