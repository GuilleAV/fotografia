package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para Foto
 * Incluye información de categoría y etiquetas
 */
public class FotoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idFoto;
    private String titulo;
    private String descripcion;
    private String nombreArchivo;
    private String urlCompleta;
    private Integer tamanioKb;
    private Integer anchoPx;
    private Integer altoPx;
    private String dimensiones;
    private String relacionAspecto;
    private Boolean destacada;
    private Boolean activo;
    private String estado; // PENDIENTE, APROBADA, RECHAZADA
    private Integer visitas;
    private LocalDateTime fechaSubida;
    private LocalDateTime fechaActualizacion;

    // Rutas de las versiones de imagen
    private String rutaArchivo;
    private String rutaThumbnail;
    private String rutaWeb;

    // Datos de la categoría
    private Integer idCategoria;
    private String categoriaNombre;
    private String categoriaSlug;
    private String categoriaColor;
    private String categoriaIcono;

    // Datos del usuario
    private Integer idUsuario;
    private String usuarioNombre;
    private String usuarioUsername;

    // Etiquetas
    private List<String> etiquetas = new ArrayList<>();

    // Constructores
    public FotoDTO() {
    }

    public FotoDTO(Integer idFoto, String titulo, String urlCompleta, String categoriaNombre) {
        this.idFoto = idFoto;
        this.titulo = titulo;
        this.urlCompleta = urlCompleta;
        this.categoriaNombre = categoriaNombre;
    }

    // Getters y Setters
    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getUrlCompleta() {
        return urlCompleta;
    }

    public void setUrlCompleta(String urlCompleta) {
        this.urlCompleta = urlCompleta;
    }

    public Integer getTamanioKb() {
        return tamanioKb;
    }

    public void setTamanioKb(Integer tamanioKb) {
        this.tamanioKb = tamanioKb;
    }

    public Integer getAnchoPx() {
        return anchoPx;
    }

    public void setAnchoPx(Integer anchoPx) {
        this.anchoPx = anchoPx;
    }

    public Integer getAltoPx() {
        return altoPx;
    }

    public void setAltoPx(Integer altoPx) {
        this.altoPx = altoPx;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public String getRelacionAspecto() {
        return relacionAspecto;
    }

    public void setRelacionAspecto(String relacionAspecto) {
        this.relacionAspecto = relacionAspecto;
    }

    public Boolean getDestacada() {
        return destacada;
    }

    public void setDestacada(Boolean destacada) {
        this.destacada = destacada;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getRutaThumbnail() {
        return rutaThumbnail;
    }

    public void setRutaThumbnail(String rutaThumbnail) {
        this.rutaThumbnail = rutaThumbnail;
    }

    public String getRutaWeb() {
        return rutaWeb;
    }

    public void setRutaWeb(String rutaWeb) {
        this.rutaWeb = rutaWeb;
    }

    public Integer getVisitas() {
        return visitas;
    }

    public void setVisitas(Integer visitas) {
        this.visitas = visitas;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getCategoriaSlug() {
        return categoriaSlug;
    }

    public void setCategoriaSlug(String categoriaSlug) {
        this.categoriaSlug = categoriaSlug;
    }

    public String getCategoriaColor() {
        return categoriaColor;
    }

    public void setCategoriaColor(String categoriaColor) {
        this.categoriaColor = categoriaColor;
    }

    public String getCategoriaIcono() {
        return categoriaIcono;
    }

    public void setCategoriaIcono(String categoriaIcono) {
        this.categoriaIcono = categoriaIcono;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public List<String> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<String> etiquetas) {
        this.etiquetas = etiquetas;
    }
}