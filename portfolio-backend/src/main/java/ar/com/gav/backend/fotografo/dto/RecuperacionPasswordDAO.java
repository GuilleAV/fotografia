package ar.com.gav.backend.fotografo.dto;


import ar.com.gav.backend.fotografo.dao.GenericDAO;
import ar.com.gav.backend.fotografo.entity.RecuperacionPassword;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Stateless
public class RecuperacionPasswordDAO extends GenericDAO<RecuperacionPassword> {

    public RecuperacionPasswordDAO() {
        super(RecuperacionPassword.class);
    }

    public RecuperacionPassword buscarPorToken(String token) {
        try {
            TypedQuery<RecuperacionPassword> query = em.createQuery(
                    "SELECT r FROM RecuperacionPassword r WHERE r.token = :token", RecuperacionPassword.class);
            query.setParameter("token", token);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void eliminarTokensViejos() {
        em.createQuery("DELETE FROM RecuperacionPassword r WHERE r.expiracion < CURRENT_TIMESTAMP")
                .executeUpdate();
    }
}
