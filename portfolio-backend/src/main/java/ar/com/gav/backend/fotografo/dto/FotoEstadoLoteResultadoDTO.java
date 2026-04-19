package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de moderación por lote.
 */
public class FotoEstadoLoteResultadoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String estadoSolicitado;
    private int totalSolicitadas;
    private int procesadas;
    private int omitidas;
    private int errores;
    private List<Detalle> detalles = new ArrayList<>();

    public String getEstadoSolicitado() {
        return estadoSolicitado;
    }

    public void setEstadoSolicitado(String estadoSolicitado) {
        this.estadoSolicitado = estadoSolicitado;
    }

    public int getTotalSolicitadas() {
        return totalSolicitadas;
    }

    public void setTotalSolicitadas(int totalSolicitadas) {
        this.totalSolicitadas = totalSolicitadas;
    }

    public int getProcesadas() {
        return procesadas;
    }

    public void setProcesadas(int procesadas) {
        this.procesadas = procesadas;
    }

    public int getOmitidas() {
        return omitidas;
    }

    public void setOmitidas(int omitidas) {
        this.omitidas = omitidas;
    }

    public int getErrores() {
        return errores;
    }

    public void setErrores(int errores) {
        this.errores = errores;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<Detalle> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(Integer idFoto, String resultado, String mensaje) {
        this.detalles.add(new Detalle(idFoto, resultado, mensaje));
    }

    public static class Detalle implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer idFoto;
        private String resultado; // PROCESADA | OMITIDA | ERROR
        private String mensaje;

        public Detalle() {
        }

        public Detalle(Integer idFoto, String resultado, String mensaje) {
            this.idFoto = idFoto;
            this.resultado = resultado;
            this.mensaje = mensaje;
        }

        public Integer getIdFoto() {
            return idFoto;
        }

        public void setIdFoto(Integer idFoto) {
            this.idFoto = idFoto;
        }

        public String getResultado() {
            return resultado;
        }

        public void setResultado(String resultado) {
            this.resultado = resultado;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}
