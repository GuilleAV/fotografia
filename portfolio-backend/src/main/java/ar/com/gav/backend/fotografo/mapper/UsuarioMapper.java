package ar.com.gav.backend.fotografo.mapper;

import ar.com.gav.backend.fotografo.dto.UsuarioDTO;
import ar.com.gav.backend.fotografo.entity.Usuario;

public class UsuarioMapper {

    public static UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setIdUsuario(entity.getId());
        dto.setUsername(entity.getNombreUsuario());
        dto.setEmail(entity.getEmail());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setRol(entity.getRol());
        dto.setActivo(entity.getActivo());
        dto.setFotoPerfil(entity.getFotoPerfil());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setUltimaSesion(entity.getUltimaSesion());
        return dto;
    }

    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        Usuario entity = new Usuario();
        entity.setId(dto.getIdUsuario());
        entity.setNombreUsuario(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setRol(dto.getRol());
        entity.setActivo(dto.getActivo());
        entity.setFotoPerfil(dto.getFotoPerfil());
        return entity;
    }
}
