package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;

/**
 * DTO para cambiar el estado de una foto (moderación).
 */
public class FotoEstadoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Nuevo estado: APROBADA o RECHAZADA
     */
    private String estado;

    /**
     * Comentario opcional del moderador
     */
    private String comentario;

    public FotoEstadoDTO() {
    }

    public FotoEstadoDTO(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
