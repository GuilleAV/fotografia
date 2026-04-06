package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Categoria
 * Transferencia de datos de categorías con estadísticas
 */
public class CategoriaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idCategoria;
    private String nombre;
    private String descripcion;
    private String slug;
    private String icono;
    private String color;
    private Integer orden;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer cantidadFotos;
    private Integer cantidadFotosActivas;
    private Integer totalVisitas;

    // Constructores
    public CategoriaDTO() {
    }

    public CategoriaDTO(Integer idCategoria, String nombre, String slug) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.slug = slug;
    }

    public CategoriaDTO(Integer idCategoria, String nombre, String descripcion, String slug, String icono, String color, Integer orden) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.slug = slug;
        this.icono = icono;
        this.color = color;
        this.orden = orden;
    }

    // Getters y Setters
    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getCantidadFotos() {
        return cantidadFotos;
    }

    public void setCantidadFotos(Integer cantidadFotos) {
        this.cantidadFotos = cantidadFotos;
    }

    public Integer getCantidadFotosActivas() {
        return cantidadFotosActivas;
    }

    public void setCantidadFotosActivas(Integer cantidadFotosActivas) {
        this.cantidadFotosActivas = cantidadFotosActivas;
    }

    public Integer getTotalVisitas() {
        return totalVisitas;
    }

    public void setTotalVisitas(Integer totalVisitas) {
        this.totalVisitas = totalVisitas;
    }
}