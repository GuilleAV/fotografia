package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.dao.SesionActivaDAO;
import ar.com.gav.backend.fotografo.entity.SesionActiva;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class SesionActivaService extends BaseService<SesionActiva> {

    @Inject
    private SesionActivaDAO sesionActivaDAO;

    public SesionActivaService() {
        super(SesionActiva.class);
    }

    public SesionActiva buscarPorToken(String token) {
        return sesionActivaDAO.buscarPorToken(token);
    }

    public void limpiarExpiradas() {
        sesionActivaDAO.limpiarExpiradas();
    }
}
