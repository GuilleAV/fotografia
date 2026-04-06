package ar.com.gav.backend.fotografo.mapper;

import ar.com.gav.backend.fotografo.dto.SesionActivaDTO;
import ar.com.gav.backend.fotografo.entity.SesionActiva;

public class SesionActivaMapper {

    public static SesionActivaDTO toDTO(SesionActiva entity) {
        if (entity == null) return null;
        SesionActivaDTO dto = new SesionActivaDTO();
        dto.setIdSesion(entity.getIdSesion());
        dto.setIpAddress(entity.getIpAddress());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaExpiracion(entity.getFechaExpiracion());
        dto.setActiva(entity.getActiva());

        return dto;
    }

    public static SesionActiva toEntity(SesionActivaDTO dto) {
        if (dto == null) return null;
        SesionActiva entity = new SesionActiva();
        entity.setIdSesion(dto.getIdSesion());
        entity.setIpAddress(dto.getIpAddress());
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaExpiracion(dto.getFechaExpiracion());
        entity.setActiva(dto.getActiva());

        return entity;
    }
}
