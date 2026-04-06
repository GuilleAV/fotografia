package ar.com.gav.backend.fotografo.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Estadistica implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estadistica")
    private Integer idEstadistica;


    @Column(name = "fecha", nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "visitas_totales", nullable = false)
    private Integer visitasTotales = 0;

    @Column(name = "fotos_vistas", nullable = false)
    private Integer fotosVistas = 0;

    @Column(name = "categorias_vistas", nullable = false)
    private Integer categoriasVistas = 0;

    @Column(name = "descargas", nullable = false)
    private Integer descargas = 0;

    @Column(name = "tiempo_promedio_segundos")
    private Integer tiempoPromedioSegundos;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public Estadistica() {
        this.fecha = LocalDate.now();
        this.visitasTotales = 0;
        this.fotosVistas = 0;
        this.categoriasVistas = 0;
        this.descargas = 0;
    }

    public Estadistica(LocalDate fecha) {
        this();
        this.fecha = fecha;
    }

    public Integer getIdEstadistica() {
        return idEstadistica;
    }

    public void setIdEstadistica(Integer idEstadistica) {
        this.idEstadistica = idEstadistica;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getVisitasTotales() {
        return visitasTotales;
    }

    public void setVisitasTotales(Integer visitasTotales) {
        this.visitasTotales = visitasTotales;
    }

    public Integer getFotosVistas() {
        return fotosVistas;
    }

    public void setFotosVistas(Integer fotosVistas) {
        this.fotosVistas = fotosVistas;
    }

    public Integer getCategoriasVistas() {
        return categoriasVistas;
    }

    public void setCategoriasVistas(Integer categoriasVistas) {
        this.categoriasVistas = categoriasVistas;
    }

    public Integer getDescargas() {
        return descargas;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }

    public Integer getTiempoPromedioSegundos() {
        return tiempoPromedioSegundos;
    }

    public void setTiempoPromedioSegundos(Integer tiempoPromedioSegundos) {
        this.tiempoPromedioSegundos = tiempoPromedioSegundos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Estadistica that = (Estadistica) o;
        return Objects.equals(idEstadistica, that.idEstadistica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEstadistica);
    }

    @Override
    public String toString() {
        return "Estadistica{" +
                "idEstadistica=" + idEstadistica +
                ", fecha=" + fecha +
                ", visitasTotales=" + visitasTotales +
                ", fotosVistas=" + fotosVistas +
                ", categoriasVistas=" + categoriasVistas +
                ", descargas=" + descargas +
                ", tiempoPromedioSegundos=" + tiempoPromedioSegundos +
                '}';
    }
}
