package ar.com.gav.backend.fotografo.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public abstract class GenericDAO<T> {

    @PersistenceContext(unitName = "portfolioPU")
    protected EntityManager em;

    private final Class<T> entityClass;

    protected GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void crear(T entidad) {
        em.persist(entidad);
    }

    public T actualizar(T entidad) {
        return em.merge(entidad);
    }

    public void eliminar(Object id) {
        T entidad = em.find(entityClass, id);
        if (entidad != null) {
            em.remove(entidad);
        }
    }

    public T buscarPorId(Object id) {
        return em.find(entityClass, id);
    }

    public List<T> listar() {
        return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }
}
