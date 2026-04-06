package ar.com.gav.backend.fotografo.dao;

import ar.com.gav.backend.fotografo.entity.Etiqueta;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class EtiquetaDAO extends GenericDAO<Etiqueta> {

    public EtiquetaDAO() {
        super(Etiqueta.class);
    }

    public Etiqueta buscarPorSlug(String slug) {
        try {
            return em.createQuery("SELECT e FROM Etiqueta e WHERE e.slug = :slug", Etiqueta.class)
                    .setParameter("slug", slug)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
