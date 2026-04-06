package ar.com.gav.backend.fotografo.dto;


import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para Estadísticas del Dashboard
 * Incluye métricas del día y resúmenes
 */
public class EstadisticasDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate fecha;
    private Integer visitasTotales;
    private Integer fotosVistas;
    private Integer categoriasVistas;
    private Integer descargas;
    private Integer tiempoPromedioSegundos;
    private Double tiempoPromedioMinutos;

    // Constructores
    public EstadisticasDTO() {
    }

    public EstadisticasDTO(LocalDate fecha, Integer visitasTotales, Integer fotosVistas) {
        this.fecha = fecha;
        this.visitasTotales = visitasTotales;
        this.fotosVistas = fotosVistas;
    }

    // Getters y Setters
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

    public Double getTiempoPromedioMinutos() {
        return tiempoPromedioMinutos;
    }

    public void setTiempoPromedioMinutos(Double tiempoPromedioMinutos) {
        this.tiempoPromedioMinutos = tiempoPromedioMinutos;
    }
}