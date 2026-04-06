package ar.com.gav.backend.fotografo.dao;

import ar.com.gav.backend.fotografo.entity.Estadistica;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TemporalType;

@Stateless
public class EstadisticaDAO extends GenericDAO<Estadistica> {

    public EstadisticaDAO() {
        super(Estadistica.class);
    }

    public List<Estadistica> listarRecientes() {
        return em.createQuery("SELECT e FROM Estadistica e ORDER BY e.fecha DESC", Estadistica.class)
                .getResultList();
    }

    public Estadistica buscarPorFecha(Date fecha) {
        try {
            return em.createQuery("SELECT e FROM Estadistica e WHERE e.fecha = :fecha", Estadistica.class)
                    .setParameter("fecha", fecha, TemporalType.DATE)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
