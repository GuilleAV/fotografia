package ar.com.gav.backend.fotografo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "etiquetas")
public class Etiqueta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etiqueta")
    private Integer idEtiqueta;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    private String slug;

    @Column(name = "color", length = 20)
    private String color; // Color hex para mostrar en UI: #27ae60

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FotoEtiqueta> fotoEtiquetas = new ArrayList<>();

    public Etiqueta() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Etiqueta(String nombre, String slug) {
        this();
        this.nombre = nombre;
        this.slug = slug;
    }

    public Etiqueta(String nombre, String slug, String color) {
        this(nombre, slug);
        this.color = color;
    }

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
        Etiqueta etiqueta = (Etiqueta) o;
        return Objects.equals(idEtiqueta, etiqueta.idEtiqueta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEtiqueta);
    }

    @Override
    public String toString() {
        return "Etiqueta{" +
                "idEtiqueta=" + idEtiqueta +
                ", nombre='" + nombre + '\'' +
                ", slug='" + slug + '\'' +
                ", color='" + color + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
