package ar.com.gav.backend.fotografo.mapper;


import ar.com.gav.backend.fotografo.dto.FotoDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;
import ar.com.gav.backend.fotografo.entity.Foto;

public class FotoMapper {

    private static final String BASE_URL = "http://localhost:8080/portfolio-backend/api";

    public static FotoDTO toDTO(Foto entity) {
        if (entity == null) return null;
        FotoDTO dto = new FotoDTO();
        dto.setIdFoto(entity.getIdFoto());
        dto.setTitulo(entity.getTitulo());
        dto.setDescripcion(entity.getDescripcion());
        dto.setNombreArchivo(entity.getNombreArchivo());
        dto.setRutaArchivo(entity.getRutaArchivo());
        dto.setRutaThumbnail(entity.getRutaThumbnail());
        dto.setRutaWeb(entity.getRutaWeb());
        dto.setTamanioKb(entity.getTamanioKb());
        dto.setAnchoPx(entity.getAnchoPx());
        dto.setAltoPx(entity.getAltoPx());
        dto.setDestacada(entity.getDestacada());
        dto.setOrden(entity.getOrden());
        dto.setActivo(entity.getActivo());
        dto.setEstado(entity.getEstado());
        dto.setVisitas(entity.getVisitas());
        dto.setFechaSubida(entity.getFechaSubida());
        dto.setFechaActualizacion(entity.getFechaActualizacion());

        // Campos computados
        if (entity.getAnchoPx() != null && entity.getAltoPx() != null) {
            dto.setDimensiones(entity.getAnchoPx() + " x " + entity.getAltoPx());
        }

        // Generar URL completa dinámicamente — nunca depende de la BD
        if (entity.getIdFoto() != null) {
            dto.setUrlCompleta(BASE_URL + "/fotos/" + entity.getIdFoto());
        }

        if (entity.getCategoria() != null) {
            dto.setIdCategoria(entity.getCategoria().getIdCategoria());
            dto.setCategoriaNombre(entity.getCategoria().getNombre());
            dto.setCategoriaSlug(entity.getCategoria().getSlug());
            dto.setCategoriaColor(entity.getCategoria().getColor());
            dto.setCategoriaIcono(entity.getCategoria().getIcono());
        }

        if (entity.getUsuario() != null) {
            dto.setIdUsuario(entity.getUsuario().getId());
            dto.setUsuarioNombre(entity.getUsuario().getNombre());
            dto.setUsuarioUsername(entity.getUsuario().getNombreUsuario());
        }

        if (entity.getFotoEtiquetas() != null) {
            entity.getFotoEtiquetas().forEach(fe ->
                dto.getEtiquetas().add(fe.getEtiqueta().getNombre())
            );
        }

        return dto;
    }

    public static Foto toEntity(FotoDTO dto) {
        if (dto == null) return null;
        Foto entity = new Foto();
        entity.setIdFoto(dto.getIdFoto());
        entity.setTitulo(dto.getTitulo());
        entity.setDescripcion(dto.getDescripcion());
        entity.setUrlCompleta(dto.getUrlCompleta());
        entity.setActivo(dto.getActivo());
        entity.setFechaSubida(dto.getFechaSubida());
        entity.setVisitas(dto.getVisitas());

        if (dto.getIdCategoria() != null) {
            Categoria categoria = new Categoria();
            categoria.setIdCategoria(dto.getIdCategoria());
            entity.setCategoria(categoria);
        }



        return entity;
    }
}
