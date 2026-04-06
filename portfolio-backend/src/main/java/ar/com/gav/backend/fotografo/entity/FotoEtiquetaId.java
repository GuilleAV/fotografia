package ar.com.gav.backend.fotografo.entity;

import java.io.Serializable;
import java.util.Objects;

public class FotoEtiquetaId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer foto;
    private Integer etiqueta;

    public FotoEtiquetaId() {
    }

    public FotoEtiquetaId(Integer foto, Integer etiqueta) {
        this.foto = foto;
        this.etiqueta = etiqueta;
    }
    public Integer getFoto() {
        return foto;
    }

    public void setFoto(Integer foto) {
        this.foto = foto;
    }

    public Integer getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(Integer etiqueta) {
        this.etiqueta = etiqueta;
    }


    @Override
    public int hashCode() {
        return Objects.hash(foto, etiqueta);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FotoEtiquetaId that = (FotoEtiquetaId) obj;
        return Objects.equals(foto, that.foto) && Objects.equals(etiqueta, that.etiqueta);
    }
}