package ar.com.gav.backend.fotografo.service;

import java.util.List;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class BaseService<T> {

    @PersistenceContext(unitName = "portfolioPU")
    protected EntityManager em;

    private final Class<T> entityClass;

    protected BaseService(Class<T> entityClass) {
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
