package ar.com.gav.backend.fotografo.dao;


import ar.com.gav.backend.fotografo.entity.SesionActiva;
import ar.com.gav.backend.fotografo.entity.Usuario;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class SesionActivaDAO extends GenericDAO<SesionActiva> {

    private static final Logger LOG = Logger.getLogger(SesionActivaDAO.class.getName());

    public SesionActivaDAO() {
        super(SesionActiva.class);
    }

    @Override
    public void crear(SesionActiva entidad) {
        // Merge del usuario para que esté managed en este persistence context
        if (entidad.getUsuario() != null && entidad.getUsuario().getId() != null) {
            Usuario managedUser = em.getReference(Usuario.class, entidad.getUsuario().getId());
            entidad.setUsuario(managedUser);
            LOG.fine("Got managed reference for user id: " + entidad.getUsuario().getId());
        }
        try {
            em.persist(entidad);
            em.flush();
            LOG.info("Session SAVED to DB - id: " + entidad.getIdSesion() + ", user: " + entidad.getUsuario().getNombreUsuario());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "FAILED to persist session", e);
            throw e;
        }
    }

    public SesionActiva buscarPorToken(String token) {
        try {
            return em.createQuery("SELECT s FROM SesionActiva s WHERE s.token = :token", SesionActiva.class)
                    .setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            LOG.fine("Session NOT FOUND for token: " + token.substring(0, Math.min(20, token.length())) + "...");
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
        int count = em.createQuery("UPDATE SesionActiva s SET s.activa = FALSE WHERE s.fechaExpiracion < CURRENT_TIMESTAMP AND s.activa = TRUE")
                .executeUpdate();
        LOG.info("Cleaned up " + count + " expired sessions");
    }
}
