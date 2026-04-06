package ar.com.gav.backend.fotografo.service;



import ar.com.gav.backend.fotografo.dao.FotoEtiquetaDAO;
import ar.com.gav.backend.fotografo.entity.FotoEtiqueta;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class FotoEtiquetaService extends BaseService<FotoEtiqueta> {

    @Inject
    private FotoEtiquetaDAO fotoEtiquetaDAO;

    public FotoEtiquetaService() {
        super(FotoEtiqueta.class);
    }
}
