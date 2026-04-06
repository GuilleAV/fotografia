package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.dao.EstadisticaDAO;
import ar.com.gav.backend.fotografo.entity.Estadistica;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class EstadisticaService extends BaseService<Estadistica> {

    @Inject
    private EstadisticaDAO estadisticaDAO;

    public EstadisticaService() {
        super(Estadistica.class);
    }

    public List<Estadistica> listarRecientes() {
        return estadisticaDAO.listarRecientes();
    }

    public Estadistica buscarPorFecha(Date fecha) {
        return estadisticaDAO.buscarPorFecha(fecha);
    }
}
