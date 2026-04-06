package ar.com.gav.backend.fotografo.mapper;


import ar.com.gav.backend.fotografo.dto.ConfiguracionDTO;
import ar.com.gav.backend.fotografo.entity.Configuracion;

public class ConfiguracionMapper {

    public static ConfiguracionDTO toDTO(Configuracion entity) {
        if (entity == null) return null;
        ConfiguracionDTO dto = new ConfiguracionDTO();
        dto.setIdConfig(entity.getIdConfig());
        dto.setClave(entity.getClave());
        dto.setValor(entity.getValor());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }

    public static Configuracion toEntity(ConfiguracionDTO dto) {
        if (dto == null) return null;
        Configuracion entity = new Configuracion();
        entity.setIdConfig(dto.getIdConfig());
        entity.setClave(dto.getClave());
        entity.setValor(dto.getValor());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }
}
