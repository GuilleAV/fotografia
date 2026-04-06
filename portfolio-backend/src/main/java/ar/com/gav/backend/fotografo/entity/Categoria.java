package ar.com.gav.backend.fotografo.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categorias")
public class Categoria {


    private static final long serialVersionUID = 1L;

    // ============================================
    // ATRIBUTOS
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;


    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;


    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;


    @Column(name = "icono", length = 50)
    private String icono;

    @Column(name = "color", length = 20)
    private String color; // Color hex para mostrar en UI: #27ae60

    @Column(name = "orden", nullable = false, unique = true)
    private Integer orden = 0 ;
    @Column(name = "activo", nullable = false,  unique = true)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;


    @Column(name = "fecha_actualizacion", nullable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "categoria",cascade = CascadeType.ALL,fetch = FetchType.LAZY, orphanRemoval = false)
    private List<Foto> fotos = new ArrayList<>();

    public Categoria() {
        this.activo = true;
        this.orden = 0;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Categoria(String nombre, String slug) {
        this();
        this.nombre = nombre;
        this.slug = slug;
    }

    public Categoria(String nombre, String slug, String descripcion) {
        this(nombre, slug);
        this.descripcion = descripcion;
    }

    public Categoria(String nombre, String slug, String descripcion, String icono, String color) {
        this(nombre, slug, descripcion);
        this.icono = icono;
        this.color = color;
    }

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

    public List<Foto> getFotos() {
        return fotos;
    }

    public void setFotos(List<Foto> fotos) {
        this.fotos = fotos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categoria categoria = (Categoria) o;
        return Objects.equals(idCategoria, categoria.idCategoria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCategoria);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", slug='" + slug + '\'' +
                ", icono='" + icono + '\'' +
                ", color='" + color + '\'' +
                ", orden=" + orden +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
