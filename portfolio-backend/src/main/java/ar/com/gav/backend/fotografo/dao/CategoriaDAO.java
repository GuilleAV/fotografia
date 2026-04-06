package ar.com.gav.backend.fotografo.dao;


import ar.com.gav.backend.fotografo.entity.Categoria;

import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class CategoriaDAO extends GenericDAO<Categoria> {

    public CategoriaDAO() {
        super(Categoria.class);
    }

    public List<Categoria> listarActivas() {
        return em.createQuery("SELECT c FROM Categoria c WHERE c.activo = TRUE ORDER BY c.orden", Categoria.class)
                .getResultList();
    }
}
