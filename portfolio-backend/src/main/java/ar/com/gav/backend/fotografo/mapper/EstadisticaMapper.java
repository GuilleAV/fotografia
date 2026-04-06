package ar.com.gav.backend.fotografo.mapper;


import ar.com.gav.backend.fotografo.dto.EstadisticasDTO;
import ar.com.gav.backend.fotografo.entity.Estadistica;

public class EstadisticaMapper {

    public static EstadisticasDTO toDTO(Estadistica entity) {
        if (entity == null) return null;
        EstadisticasDTO dto = new EstadisticasDTO();
        dto.setFecha(entity.getFecha());
        dto.setVisitasTotales(entity.getVisitasTotales());

        return dto;
    }

    public static Estadistica toEntity(EstadisticasDTO dto) {
        if (dto == null) return null;
        Estadistica entity = new Estadistica();
        entity.setFecha(dto.getFecha());
        entity.setVisitasTotales(dto.getVisitasTotales());

        return entity;
    }
}
