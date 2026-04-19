package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Request para cambio de estado por lote (moderación admin).
 */
public class FotoEstadoLoteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> ids;
    private String estado;

    public FotoEstadoLoteDTO() {
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
