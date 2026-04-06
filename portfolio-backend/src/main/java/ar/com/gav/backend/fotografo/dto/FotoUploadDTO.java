package ar.com.gav.backend.fotografo.dto;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para subir una foto
 * Incluye validaciones para el formulario de carga
 */
public class FotoUploadDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "El título es obligatorio")
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;

    private Boolean destacada = false;

    private List<Integer> etiquetasIds = new ArrayList<>();

    // El archivo viene por separado en un multipart/form-data
    // Este DTO solo contiene los metadatos

    // Constructores
    public FotoUploadDTO() {
    }

    public FotoUploadDTO(String titulo, Integer idCategoria) {
        this.titulo = titulo;
        this.idCategoria = idCategoria;
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

    public List<Integer> getEtiquetasIds() {
        return etiquetasIds;
    }

    public void setEtiquetasIds(List<Integer> etiquetasIds) {
        this.etiquetasIds = etiquetasIds;
    }
}
