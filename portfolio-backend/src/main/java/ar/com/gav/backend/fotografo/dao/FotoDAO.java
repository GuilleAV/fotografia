package ar.com.gav.backend.fotografo.dao;



import ar.com.gav.backend.fotografo.entity.Foto;

import java.util.List;
import javax.ejb.Stateless;

@Stateless
public class FotoDAO extends GenericDAO<Foto> {

    public FotoDAO() {
        super(Foto.class);
    }

    public List<Foto> listarActivas() {
        return em.createQuery("SELECT f FROM Foto f WHERE f.activo = TRUE", Foto.class)
                .getResultList();
    }

    public List<Foto> listarDestacadas() {
        return em.createQuery("SELECT f FROM Foto f WHERE f.destacada = TRUE AND f.activo = TRUE", Foto.class)
                .getResultList();
    }

    public void incrementarVisitas(Integer idFoto) {
        Foto f = buscarPorId(idFoto);
        if (f != null) {
            f.setVisitas(f.getVisitas() + 1);
            em.merge(f);
        }
    }
}
