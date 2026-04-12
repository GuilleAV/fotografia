package ar.com.gav.backend.fotografo.dto;


import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * DTO para actualizar una foto existente
 * Todos los campos son opcionales
 */
public class FotoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;

    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    private String comentario;

    private Integer idCategoria;

    private Boolean destacada;

    private Integer orden; // Para carousel (1-5)

    private Boolean activo;

    private List<Integer> etiquetasIds;

    // Constructores
    public FotoUpdateDTO() {
    }

    // Getters y Setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Boolean getDestacada() {
        return destacada;
    }

    public void setDestacada(Boolean destacada) {
        this.destacada = destacada;
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

    public List<Integer> getEtiquetasIds() {
        return etiquetasIds;
    }

    public void setEtiquetasIds(List<Integer> etiquetasIds) {
        this.etiquetasIds = etiquetasIds;
    }
}
