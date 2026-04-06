package ar.com.gav.backend.fotografo.dao;

import ar.com.gav.backend.fotografo.entity.Configuracion;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class ConfiguracionDAO extends GenericDAO<Configuracion> {

    public ConfiguracionDAO() {
        super(Configuracion.class);
    }

    public Configuracion buscarPorClave(String clave) {
        try {
            return em.createQuery("SELECT c FROM Configuracion c WHERE c.clave = :clave", Configuracion.class)
                    .setParameter("clave", clave)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
