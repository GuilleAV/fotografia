package ar.com.gav.backend.fotografo.service;


import ar.com.gav.backend.fotografo.dto.CategoriaDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;
import ar.com.gav.backend.fotografo.mapper.CategoriaMapper;

import javax.ejb.Stateless;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CategoriaService extends BaseService<Categoria> {

    public CategoriaService() {
        super(Categoria.class);
    }

    public void crear(CategoriaDTO dto) {
        Categoria entidad = CategoriaMapper.toEntity(dto);
        super.crear(entidad);
        dto.setIdCategoria(entidad.getIdCategoria());
    }

    public CategoriaDTO actualizar(CategoriaDTO dto) {
        Categoria entidad = CategoriaMapper.toEntity(dto);
        Categoria actualizado = super.actualizar(entidad);
        return CategoriaMapper.toDTO(actualizado);
    }

    public void eliminar(Long id) {
        super.eliminar(id);
    }

    public CategoriaDTO buscarPorId(Long id) {
        Categoria entidad = super.buscarPorId(id);
        return CategoriaMapper.toDTO(entidad);
    }

    public List<CategoriaDTO> listarDto() {
        return super.listar().stream()
                .map(CategoriaMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista solo las categorías activas (para el público).
     */
    public List<Categoria> listarActivas() {
        return em.createQuery("SELECT c FROM Categoria c WHERE c.activo = TRUE ORDER BY c.orden ASC", Categoria.class)
                .getResultList();
    }
}
