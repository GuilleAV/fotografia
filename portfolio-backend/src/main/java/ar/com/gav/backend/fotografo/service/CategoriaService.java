package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.dto.CategoriaDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;
import ar.com.gav.backend.fotografo.mapper.CategoriaMapper;
import java.time.LocalDateTime;

import javax.ejb.Stateless;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class CategoriaService extends BaseService<Categoria> {

    private static final Logger LOG = Logger.getLogger(CategoriaService.class.getName());

    public CategoriaService() {
        super(Categoria.class);
    }

    public void crear(CategoriaDTO dto) {
        LOG.info("=== CREAR CATEGORIA === Nombre: " + dto.getNombre());
        try {
            Categoria entidad = CategoriaMapper.toEntity(dto);
            // Normalizar slug: lowercase, sin espacios ni caracteres especiales
            if (entidad.getSlug() != null) {
                entidad.setSlug(normalizarSlug(entidad.getSlug()));
            }
            entidad.setFechaActualizacion(LocalDateTime.now());
            super.crear(entidad);
            dto.setIdCategoria(entidad.getIdCategoria());
            LOG.info("Category CREATED: " + dto.getNombre() + " (id: " + entidad.getIdCategoria() + ")");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error creating category: " + dto.getNombre(), e);
            throw e;
        }
    }

    public CategoriaDTO actualizar(CategoriaDTO dto) {
        LOG.info("=== ACTUALIZAR CATEGORIA === ID: " + dto.getIdCategoria());
        try {
            Categoria existing = em.find(Categoria.class, dto.getIdCategoria());
            if (existing == null) {
                throw new RuntimeException("Categoría no encontrada");
            }

            existing.setNombre(dto.getNombre());
            if (dto.getSlug() != null) {
                existing.setSlug(normalizarSlug(dto.getSlug()));
            }
            if (dto.getIcono() != null) {
                existing.setIcono(dto.getIcono());
            }
            if (dto.getColor() != null) {
                existing.setColor(dto.getColor());
            }
            if (dto.getOrden() != null) {
                existing.setOrden(dto.getOrden());
            }
            if (dto.getActivo() != null) {
                existing.setActivo(dto.getActivo());
            }
            existing.setFechaActualizacion(java.time.LocalDateTime.now());

            Categoria actualizado = em.merge(existing);
            LOG.info("Category UPDATED: " + dto.getIdCategoria());
            return CategoriaMapper.toDTO(actualizado);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error updating category: " + dto.getIdCategoria(), e);
            throw e;
        }
    }

    /**
     * Normaliza un slug: lowercase, reemplaza espacios/puntuación con guiones,
     * elimina caracteres especiales. Ej: "Naturaleza , paisaje" →
     * "naturaleza-paisaje"
     */
    private String normalizarSlug(String slug) {
        return slug.trim()
                .toLowerCase()
                .replaceAll("[\\s,;]+", "-")
                .replaceAll("[^a-z0-9\\-]", "")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

    public void eliminar(Integer id) {
        LOG.info("=== ELIMINAR CATEGORIA === ID: " + id);
        try {
            super.eliminar(id);
            LOG.info("Category DELETED: " + id);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error deleting category: " + id, e);
            throw e;
        }
    }

    public CategoriaDTO buscarPorId(Integer id) {
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
        LOG.fine("=== LISTAR CATEGORIAS ACTIVAS ===");
        List<Categoria> categorias = em.createQuery("SELECT c FROM Categoria c WHERE c.activo = TRUE ORDER BY c.orden ASC", Categoria.class)
                .getResultList();
        LOG.fine("Found " + categorias.size() + " active categories");
        return categorias;
    }
}
