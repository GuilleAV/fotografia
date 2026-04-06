package ar.com.gav.backend.fotografo.mapper;


import ar.com.gav.backend.fotografo.dto.EtiquetaDTO;
import ar.com.gav.backend.fotografo.entity.Etiqueta;

public class EtiquetaMapper {

    public static EtiquetaDTO toDTO(Etiqueta entidad) {
        if (entidad == null) {
            return null;
        }

        EtiquetaDTO dto = new EtiquetaDTO();
        dto.setIdEtiqueta(entidad.getIdEtiqueta());
        dto.setNombre(entidad.getNombre());
        dto.setSlug(entidad.getSlug());
        dto.setColor(entidad.getColor());
        dto.setFechaCreacion(entidad.getFechaCreacion());

        return dto;
    }

    public static Etiqueta toEntity(EtiquetaDTO dto) {
        if (dto == null) {
            return null;
        }

        Etiqueta entidad = new Etiqueta();
        entidad.setIdEtiqueta(dto.getIdEtiqueta());
        entidad.setNombre(dto.getNombre());
        entidad.setSlug(dto.getSlug());
        entidad.setColor(dto.getColor());
        entidad.setFechaCreacion(dto.getFechaCreacion());

        return entidad;
    }
}
