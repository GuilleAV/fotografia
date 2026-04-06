package ar.com.gav.backend.fotografo.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "fotos_etiquetas")
@IdClass(FotoEtiquetaId.class)
public class FotoEtiqueta implements Serializable {
   private static final long serialVersionUID = 1L;

    // ============================================
    // CLAVE COMPUESTA (Primary Key)
    // ============================================

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_foto", nullable = false)
    private Foto foto;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_etiqueta", nullable = false)
    private Etiqueta etiqueta;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }

    public Etiqueta getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(Etiqueta etiqueta) {
        this.etiqueta = etiqueta;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FotoEtiqueta that = (FotoEtiqueta) o;
        return Objects.equals(foto, that.foto) && Objects.equals(etiqueta, that.etiqueta) && Objects.equals(fechaAsignacion, that.fechaAsignacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foto, etiqueta, fechaAsignacion);
    }

    @Override
    public String toString() {
        return "FotoEtiqueta{" +
                "foto=" + (foto != null ? foto.getIdFoto() : "null") +
                ", etiqueta=" + (etiqueta != null ? etiqueta.getNombre() : "null") +
                ", fechaAsignacion=" + fechaAsignacion +
                '}';
    }
}