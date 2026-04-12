package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.dto.CategoriaDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;
import ar.com.gav.backend.fotografo.mapper.CategoriaMapper;
import java.time.LocalDateTime;

import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class CategoriaService extends BaseService<Categoria> {

    private static final Logger LOG = Logger.getLogger(CategoriaService.class.getName());
    private static final Set<String> RESERVED_SLUGS = new HashSet<>(Arrays.asList(
            "about", "contacto", "contact", "login", "recuperar", "reset-password",
            "dashboard", "admin", "usuarios", "unauthorized", "foto", "api", "auth"
    ));

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
            validarSlug(entidad.getSlug());
            validarSlugDisponible(entidad.getSlug(), null);
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
                String slugNormalizado = normalizarSlug(dto.getSlug());
                validarSlug(slugNormalizado);
                validarSlugDisponible(slugNormalizado, dto.getIdCategoria());
                existing.setSlug(slugNormalizado);
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

    private void validarSlug(String slug) {
        if (slug == null || slug.trim().isEmpty()) {
            throw new RuntimeException("Slug inválido. No puede estar vacío");
        }

        if (esSlugReservado(slug)) {
            throw new RuntimeException("Slug reservado para rutas del sistema");
        }
    }

    private boolean esSlugReservado(String slug) {
        return RESERVED_SLUGS.contains(slug);
    }

    private void validarSlugDisponible(String slug, Integer excluirId) {
        Long total;

        if (excluirId == null) {
            total = em.createQuery("SELECT COUNT(c) FROM Categoria c WHERE c.slug = :slug", Long.class)
                    .setParameter("slug", slug)
                    .getSingleResult();
        } else {
            total = em.createQuery("SELECT COUNT(c) FROM Categoria c WHERE c.slug = :slug AND c.idCategoria <> :id", Long.class)
                    .setParameter("slug", slug)
                    .setParameter("id", excluirId)
                    .getSingleResult();
        }

        if (total != null && total > 0) {
            throw new RuntimeException("El slug ya está en uso");
        }
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

    /**
     * Busca una categoría activa por slug (uso público).
     */
    public Categoria buscarActivaPorSlug(String slug) {
        String slugNormalizado = normalizarSlug(slug);
        List<Categoria> categorias = em.createQuery(
                        "SELECT c FROM Categoria c WHERE c.slug = :slug AND c.activo = TRUE", Categoria.class)
                .setParameter("slug", slugNormalizado)
                .setMaxResults(1)
                .getResultList();

        return categorias.isEmpty() ? null : categorias.get(0);
    }
}
