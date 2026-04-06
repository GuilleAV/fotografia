package ar.com.gav.backend.fotografo.dao;

import ar.com.gav.backend.fotografo.entity.Usuario;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class UsuarioDAO extends GenericDAO<Usuario> {

    public UsuarioDAO() {
        super(Usuario.class);
    }

    public Usuario buscarPorUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :user", Usuario.class)
                    .setParameter("user", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Usuario buscarPorEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Usuario> listarActivos() {
        return em.createQuery("SELECT u FROM Usuario u WHERE u.activo = TRUE", Usuario.class)
                .getResultList();
    }
}
