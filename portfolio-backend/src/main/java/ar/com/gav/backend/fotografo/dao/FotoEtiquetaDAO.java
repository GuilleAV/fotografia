package ar.com.gav.backend.fotografo.dao;

import ar.com.gav.backend.fotografo.entity.FotoEtiqueta;

import javax.ejb.Stateless;

@Stateless
public class FotoEtiquetaDAO extends GenericDAO<FotoEtiqueta> {

    public FotoEtiquetaDAO() {
        super(FotoEtiqueta.class);
    }
}
