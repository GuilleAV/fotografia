package ar.com.gav.backend.fotografo.dao;


import ar.com.gav.backend.fotografo.entity.SesionActiva;
import ar.com.gav.backend.fotografo.entity.Usuario;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SesionActivaDAO extends GenericDAO<SesionActiva> {

    public SesionActivaDAO() {
        super(SesionActiva.class);
    }

    public SesionActiva buscarPorToken(String token) {
        try {
            return em.createQuery("SELECT s FROM SesionActiva s WHERE s.token = :token", SesionActiva.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<SesionActiva> buscarPorUsuario(Usuario usuario) {
        try {
            return em.createQuery("SELECT s FROM SesionActiva s WHERE s.usuario = :usuario", SesionActiva.class)
                    .setParameter("usuario", usuario)
                    .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public void limpiarExpiradas() {
        em.createQuery("UPDATE SesionActiva s SET s.activa = FALSE WHERE s.fechaExpiracion < CURRENT_TIMESTAMP AND s.activa = TRUE")
                .executeUpdate();
    }
}
