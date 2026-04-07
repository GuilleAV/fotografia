package ar.com.gav.backend.fotografo.mapper;


import ar.com.gav.backend.fotografo.dto.CategoriaDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;

public class CategoriaMapper {

    public static CategoriaDTO toDTO(Categoria entity) {
        if (entity == null) return null;
        CategoriaDTO dto = new CategoriaDTO();
        dto.setIdCategoria(entity.getIdCategoria());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setSlug(entity.getSlug());
        dto.setIcono(entity.getIcono());
        dto.setColor(entity.getColor());
        dto.setOrden(entity.getOrden());
        dto.setActivo(entity.getActivo());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        return dto;
    }

    public static Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) return null;
        Categoria entity = new Categoria();
        entity.setIdCategoria(dto.getIdCategoria());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setSlug(dto.getSlug());
        entity.setIcono(dto.getIcono());
        entity.setColor(dto.getColor());
        entity.setOrden(dto.getOrden());
        entity.setActivo(dto.getActivo());
        return entity;
    }
}
