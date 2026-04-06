package ar.com.gav.backend.fotografo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Etiqueta
 * Información de tags con estadísticas de uso
 */
public class EtiquetaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idEtiqueta;
    private String nombre;
    private String slug;
    private String color;
    private LocalDateTime fechaCreacion;
    private Integer cantidadFotos;

    // Constructores
    public EtiquetaDTO() {
    }

    public EtiquetaDTO(Integer idEtiqueta, String nombre, String slug) {
        this.idEtiqueta = idEtiqueta;
        this.nombre = nombre;
        this.slug = slug;
    }

    // Getters y Setters
    public Integer getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(Integer idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Integer getCantidadFotos() {
        return cantidadFotos;
    }

    public void setCantidadFotos(Integer cantidadFotos) {
        this.cantidadFotos = cantidadFotos;
    }
}