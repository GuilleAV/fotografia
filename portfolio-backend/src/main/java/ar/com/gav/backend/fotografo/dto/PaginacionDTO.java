package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO para respuestas paginadas
 * Wrapper genérico para listas con paginación
 */
public class PaginacionDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> contenido;
    private Integer paginaActual;
    private Integer elementosPorPagina;
    private Long totalElementos;
    private Integer totalPaginas;
    private Boolean esPrimera;
    private Boolean esUltima;
    private Boolean tieneAnterior;
    private Boolean tieneSiguiente;

    // Constructores
    public PaginacionDTO() {
    }

    public PaginacionDTO(List<T> contenido, Integer paginaActual, Integer elementosPorPagina, Long totalElementos) {
        this.contenido = contenido;
        this.paginaActual = paginaActual;
        this.elementosPorPagina = elementosPorPagina;
        this.totalElementos = totalElementos;

        // Calcular automáticamente
        this.totalPaginas = (int) Math.ceil((double) totalElementos / elementosPorPagina);
        this.esPrimera = paginaActual == 0;
        this.esUltima = paginaActual >= totalPaginas - 1;
        this.tieneAnterior = paginaActual > 0;
        this.tieneSiguiente = paginaActual < totalPaginas - 1;
    }

    // Getters y Setters
    public List<T> getContenido() {
        return contenido;
    }

    public void setContenido(List<T> contenido) {
        this.contenido = contenido;
    }

    public Integer getPaginaActual() {
        return paginaActual;
    }

    public void setPaginaActual(Integer paginaActual) {
        this.paginaActual = paginaActual;
    }

    public Integer getElementosPorPagina() {
        return elementosPorPagina;
    }

    public void setElementosPorPagina(Integer elementosPorPagina) {
        this.elementosPorPagina = elementosPorPagina;
    }

    public Long getTotalElementos() {
        return totalElementos;
    }

    public void setTotalElementos(Long totalElementos) {
        this.totalElementos = totalElementos;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }

    public Boolean getEsPrimera() {
        return esPrimera;
    }

    public void setEsPrimera(Boolean esPrimera) {
        this.esPrimera = esPrimera;
    }

    public Boolean getEsUltima() {
        return esUltima;
    }

    public void setEsUltima(Boolean esUltima) {
        this.esUltima = esUltima;
    }

    public Boolean getTieneAnterior() {
        return tieneAnterior;
    }

    public void setTieneAnterior(Boolean tieneAnterior) {
        this.tieneAnterior = tieneAnterior;
    }

    public Boolean getTieneSiguiente() {
        return tieneSiguiente;
    }

    public void setTieneSiguiente(Boolean tieneSiguiente) {
        this.tieneSiguiente = tieneSiguiente;
    }
}