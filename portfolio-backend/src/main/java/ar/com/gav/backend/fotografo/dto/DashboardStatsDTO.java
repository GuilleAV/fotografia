package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;

/**
 * DTO para estadísticas generales del Dashboard
 * Resumen de métricas principales
 */
public class DashboardStatsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer totalFotos;
    private Integer totalCategorias;
    private Integer fotosDestacadas;
    private Integer visitasTotales;
    private Integer totalEtiquetas;
    private Integer fotosHoy;
    private Integer visitasHoy;

    // Constructores
    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(Integer totalFotos, Integer totalCategorias, Integer fotosDestacadas, Integer visitasTotales) {
        this.totalFotos = totalFotos;
        this.totalCategorias = totalCategorias;
        this.fotosDestacadas = fotosDestacadas;
        this.visitasTotales = visitasTotales;
    }

    // Getters y Setters
    public Integer getTotalFotos() {
        return totalFotos;
    }

    public void setTotalFotos(Integer totalFotos) {
        this.totalFotos = totalFotos;
    }

    public Integer getTotalCategorias() {
        return totalCategorias;
    }

    public void setTotalCategorias(Integer totalCategorias) {
        this.totalCategorias = totalCategorias;
    }

    public Integer getFotosDestacadas() {
        return fotosDestacadas;
    }

    public void setFotosDestacadas(Integer fotosDestacadas) {
        this.fotosDestacadas = fotosDestacadas;
    }

    public Integer getVisitasTotales() {
        return visitasTotales;
    }

    public void setVisitasTotales(Integer visitasTotales) {
        this.visitasTotales = visitasTotales;
    }

    public Integer getTotalEtiquetas() {
        return totalEtiquetas;
    }

    public void setTotalEtiquetas(Integer totalEtiquetas) {
        this.totalEtiquetas = totalEtiquetas;
    }

    public Integer getFotosHoy() {
        return fotosHoy;
    }

    public void setFotosHoy(Integer fotosHoy) {
        this.fotosHoy = fotosHoy;
    }

    public Integer getVisitasHoy() {
        return visitasHoy;
    }

    public void setVisitasHoy(Integer visitasHoy) {
        this.visitasHoy = visitasHoy;
    }
}