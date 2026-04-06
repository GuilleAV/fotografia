package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.dao.ConfiguracionDAO;
import ar.com.gav.backend.fotografo.entity.Configuracion;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ConfiguracionService extends BaseService<Configuracion> {

    @Inject
    private ConfiguracionDAO configuracionDAO;

    public ConfiguracionService() {
        super(Configuracion.class);
    }

    public Configuracion buscarPorClave(String clave) {
        return configuracionDAO.buscarPorClave(clave);
    }
}
