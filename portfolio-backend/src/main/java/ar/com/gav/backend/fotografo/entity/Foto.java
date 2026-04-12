package ar.com.gav.backend.fotografo.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "fotos")
public class Foto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto", nullable = false)
    private Integer idFoto;


    @Column(name = "titulo", nullable = false,  length = 200)
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;


    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Column(name = "ruta_thumbnail", length = 500)
    private String rutaThumbnail;

    @Column(name = "ruta_web", length = 500)
    private String rutaWeb;

    @Column(name = "url_completa", length = 500)
    private String urlCompleta;

    @Column(name = "tamanio_kb")
    private Integer tamanioKb;

    @Column(name = "ancho_px")
    private Integer anchoPx;

    @Column(name = "alto_px")
    private Integer altoPx;

    @Column(name = "destacada", nullable = false)
    private Boolean destacada = false;

    @Column(name = "orden")
    private Integer orden; // Para поряд del carousel (1, 2, 3...)

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, APROBADA, RECHAZADA

    @Column(name = "visitas", nullable = false)
    private Integer visitas = 0;

    @Column(name = "fecha_subida", nullable = false, updatable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "foto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FotoEtiqueta> fotoEtiquetas = new ArrayList<>();



    public Foto() {
        this.activo = true;
        this.destacada = false;
        this.visitas = 0;
        this.fechaSubida = LocalDateTime.now();
    }

    public Foto(String titulo, String nombreArchivo, String rutaArchivo, Categoria categoria, Usuario usuario) {
        this();
        this.titulo = titulo;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.categoria = categoria;
        this.usuario = usuario;
    }


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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<FotoEtiqueta> getFotoEtiquetas() {
        return fotoEtiquetas;
    }

    public void setFotoEtiquetas(List<FotoEtiqueta> fotoEtiquetas) {
        this.fotoEtiquetas = fotoEtiquetas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foto foto = (Foto) o;
        return Objects.equals(idFoto, foto.idFoto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFoto);
    }

    @Override
    public String toString() {
        return "Foto{" +
                "idFoto=" + idFoto +
                ", titulo='" + titulo + '\'' +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", destacada=" + destacada +
                ", visitas=" + visitas +
                ", categoria=" + (categoria != null ? categoria.getNombre() : "null") +
                '}';
    }
}
