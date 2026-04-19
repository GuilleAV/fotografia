package ar.com.gav.backend.fotografo.service;


import ar.com.gav.backend.fotografo.config.AppConfig;
import ar.com.gav.backend.fotografo.dao.FotoDAO;
import ar.com.gav.backend.fotografo.dto.FileUploadResponseDTO;
import ar.com.gav.backend.fotografo.dto.FotoDTO;
import ar.com.gav.backend.fotografo.dto.FotoEstadoLoteResultadoDTO;
import ar.com.gav.backend.fotografo.dto.FotoUpdateDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;
import ar.com.gav.backend.fotografo.entity.Foto;
import ar.com.gav.backend.fotografo.entity.Usuario;
import ar.com.gav.backend.fotografo.mapper.FotoMapper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
public class FotoService extends BaseService<Foto> {

    private static final Logger LOG = Logger.getLogger(FotoService.class.getName());

    @Inject
    private ImageProcessor imageProcessor;

    @Inject
    private UsuarioService usuarioService;

    // Directorio base para almacenamiento de fotos (configurable)
    private static final String UPLOAD_DIR = AppConfig.getUploadDir();

    /**
     * Retorna el directorio base de uploads.
     * Necesario para servir imágenes desde FotoResource.
     */
    public String getUploadDir() {
        return UPLOAD_DIR;
    }

    public FotoService() {
        super(Foto.class);
    }

    // ============================================
    // CRUD BÁSICO
    // ============================================

    public void crear(FotoDTO dto) {
        Foto entidad = FotoMapper.toEntity(dto);
        super.crear(entidad);
        dto.setIdFoto(entidad.getIdFoto());
    }

    public FotoDTO actualizar(FotoDTO dto) {
        Foto entidad = FotoMapper.toEntity(dto);
        Foto actualizado = super.actualizar(entidad);
        return FotoMapper.toDTO(actualizado);
    }

    public void eliminar(Integer id) {
        super.eliminar(id);
    }

    public FotoDTO buscarPorId(Integer id) {
        Foto entidad = super.buscarPorId(id);
        return FotoMapper.toDTO(entidad);
    }

    public List<FotoDTO> listarDto() {
        return super.listar().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ============================================
    // CONSULTAS ESPECÍFICAS
    // ============================================

    /**
     * Lista solo fotos aprobadas (visibles al público).
     */
    public List<FotoDTO> listarAprobadas() {
        return em.createQuery(
                "SELECT f FROM Foto f WHERE f.estado = 'APROBADA' AND f.activo = TRUE ORDER BY f.fechaSubida DESC",
                Foto.class).getResultList().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista fotos por categoría (solo aprobadas).
     */
    public List<FotoDTO> listarPorCategoriaAprobadas(Integer idCategoria) {
        return em.createQuery(
                "SELECT f FROM Foto f WHERE f.categoria.idCategoria = :idCat AND f.estado = 'APROBADA' AND f.activo = TRUE ORDER BY f.fechaSubida DESC",
                Foto.class)
                .setParameter("idCat", idCategoria)
                .getResultList().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista fotos para el carousel (fotos con orden asignado).
     * Solo aprobadas, ordenadas por el campo orden ASC.
     */
    public List<FotoDTO> listarParaCarousel() {
        return em.createQuery(
                "SELECT f FROM Foto f WHERE f.estado = 'APROBADA' AND f.activo = TRUE AND f.orden IS NOT NULL ORDER BY f.orden ASC",
                Foto.class)
                .setMaxResults(5)
                .getResultList().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista fotos destacadas (para la sección de fotos destacadas).
     * Solo aprobadas, ordenadas por fecha DESC.
     */
    public List<FotoDTO> listarDestacadas() {
        return em.createQuery(
                "SELECT f FROM Foto f WHERE f.estado = 'APROBADA' AND f.activo = TRUE AND f.destacada = TRUE ORDER BY f.fechaSubida DESC",
                Foto.class).getResultList().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista fotos por username del usuario.
     */
    public List<FotoDTO> listarPorUsuario(String username) {
        return em.createQuery(
                "SELECT f FROM Foto f JOIN f.usuario u WHERE u.nombreUsuario = :username ORDER BY f.fechaSubida DESC",
                Foto.class)
                .setParameter("username", username)
                .getResultList().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista fotos por estado (para moderación).
     */
    public List<FotoDTO> listarPorEstado(String estado) {
        return em.createQuery(
                "SELECT f FROM Foto f WHERE f.estado = :estado ORDER BY f.fechaSubida DESC",
                Foto.class)
                .setParameter("estado", estado)
                .getResultList().stream()
                .map(FotoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ============================================
    // UPLOAD CON PROCESAMIENTO
    // ============================================

    /**
     * Sube una foto, procesa la imagen y la guarda en BD.
     */
    public FileUploadResponseDTO subirFoto(InputStream archivoStream, String nombreArchivo,
                                           String titulo, String descripcion, String comentario, Integer idCategoria,
                                           String username) throws Exception {
        LOG.info("=== SUBIR FOTO === User: " + username + ", File: " + nombreArchivo);
        // Obtener usuario
        Usuario usuario = usuarioService.buscarPorUsernameEntity(username);
        if (usuario == null) {
            LOG.warning("User NOT FOUND for upload: " + username);
            throw new Exception("Usuario no encontrado");
        }
        LOG.info("User found: " + usuario.getNombreUsuario() + " (id: " + usuario.getId() + ")");

        // Obtener categoría
        Categoria categoria = em.find(Categoria.class, idCategoria);
        if (categoria == null) {
            LOG.warning("Category NOT FOUND: " + idCategoria);
            throw new Exception("Categoría no encontrada");
        }
        LOG.info("Category found: " + categoria.getNombre());

        // Crear directorio del usuario
        String userUploadDir = UPLOAD_DIR + File.separator + username;
        File dir = new File(userUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            LOG.info("Created upload directory: " + userUploadDir);
        }

        // Procesar imagen (genera original, thumbnail, web)
        LOG.info("Processing image...");
        ImageProcessor.ImageResult result = imageProcessor.processImage(archivoStream, nombreArchivo, userUploadDir);
        LOG.info("Image processed - Original: " + result.originalName
                + ", Thumbnail: " + result.thumbnailName
                + ", Web: " + result.webName
                + ", Dimensions: " + result.originalWidth + "x" + result.originalHeight);

        // Crear entidad Foto
        Foto foto = new Foto();
        foto.setTitulo(titulo);
        foto.setDescripcion(descripcion);
        foto.setComentario(comentario);
        foto.setNombreArchivo(nombreArchivo);
        foto.setRutaArchivo(result.originalName);
        foto.setRutaThumbnail(result.thumbnailName);
        foto.setRutaWeb(result.webName);
        foto.setAnchoPx(result.originalWidth);
        foto.setAltoPx(result.originalHeight);
        foto.setTamanioKb((int) (result.originalSizeBytes / 1024));
        foto.setCategoria(categoria);
        foto.setUsuario(usuario);
        foto.setEstado("PENDIENTE");
        foto.setActivo(true);

        LOG.info("File size: " + foto.getTamanioKb() + " KB, Dimensions: " + result.originalWidth + "x" + result.originalHeight);

        super.crear(foto);
        // Con IDENTITY, el ID no se asigna hasta flush
        em.flush();
        LOG.info("Photo SAVED to DB - ID: " + foto.getIdFoto());

        // Generar URL completa con el ID ya disponible
        String baseUrl = AppConfig.getPublicApiBaseUrl();
        foto.setUrlCompleta(baseUrl + "/fotos/" + foto.getIdFoto());
        em.merge(foto);

        // Construir response
        FileUploadResponseDTO response = new FileUploadResponseDTO();
        response.setIdFoto(foto.getIdFoto());
        response.setTitulo(titulo);
        response.setNombreArchivo(nombreArchivo);
        response.setRutaOriginal(result.originalName);
        response.setRutaThumbnail(result.thumbnailName);
        response.setRutaWeb(result.webName);
        response.setEstado("PENDIENTE");
        response.setMensaje("Foto subida correctamente. Pendiente de aprobación.");

        return response;
    }

    // ============================================
    // ACTUALIZACIÓN CON PERMISOS
    // ============================================

    /**
     * Actualiza metadata de una foto. Solo el dueño o admin pueden.
     */
    public FotoDTO actualizarFoto(Integer id, FotoUpdateDTO dto, String username) {
        LOG.info("=== ACTUALIZAR FOTO === ID: " + id + ", User: " + username);
        Foto foto = em.find(Foto.class, id);
        if (foto == null) {
            LOG.warning("Photo NOT FOUND: " + id);
            throw new RuntimeException("Foto no encontrada");
        }

        // Verificar permiso
        if (!esAdmin(username) && !foto.getUsuario().getNombreUsuario().equals(username)) {
            LOG.warning("FORBIDDEN - User: " + username + " trying to edit photo: " + id + " (owner: " + foto.getUsuario().getNombreUsuario() + ")");
            throw new SecurityException("No tienes permiso para editar esta foto");
        }

        // Actualizar campos opcionales
        if (dto.getTitulo() != null) foto.setTitulo(dto.getTitulo());
        if (dto.getDescripcion() != null) foto.setDescripcion(dto.getDescripcion());
        if (dto.getComentario() != null) foto.setComentario(dto.getComentario());
        if (dto.getIdCategoria() != null) {
            if (!esAdmin(username)) {
                throw new SecurityException("Solo administradores pueden cambiar la categoría");
            }
            Categoria cat = em.find(Categoria.class, dto.getIdCategoria());
            if (cat != null) foto.setCategoria(cat);
        }
        if (dto.getDestacada() != null) foto.setDestacada(dto.getDestacada());
        // Orden: permitir setear (1-5) o remove (null)
        if (dto.getOrden() != null) {
            if (dto.getOrden() >= 1 && dto.getOrden() <= 5) {
                foto.setOrden(dto.getOrden());
            }
        } else {
            // Si explicitly se envía null, remover del carousel
            foto.setOrden(null);
        }
        if (dto.getActivo() != null) foto.setActivo(dto.getActivo());

        foto.setFechaActualizacion(java.time.LocalDateTime.now());

        em.merge(foto);
        LOG.info("Photo UPDATED: " + id);
        return FotoMapper.toDTO(foto);
    }

    /**
     * Elimina una foto. Solo el dueño o admin pueden.
     */
    public void eliminarFoto(Integer id, String username) {
        LOG.info("=== ELIMINAR FOTO === ID: " + id + ", User: " + username);
        Foto foto = em.find(Foto.class, id);
        if (foto == null) {
            LOG.warning("Photo NOT FOUND: " + id);
            throw new RuntimeException("Foto no encontrada");
        }

        if (!esAdmin(username) && !foto.getUsuario().getNombreUsuario().equals(username)) {
            LOG.warning("FORBIDDEN - User: " + username + " trying to delete photo: " + id);
            throw new SecurityException("No tienes permiso para eliminar esta foto");
        }

        // Eliminar archivos del disco
        String userUploadDir = UPLOAD_DIR + File.separator + foto.getUsuario().getNombreUsuario();
        eliminarArchivosFoto(userUploadDir, foto);
        LOG.info("Photo files deleted from disk");

        em.remove(foto);
        LOG.info("Photo DELETED from DB: " + id);
    }

    // ============================================
    // MODERACIÓN
    // ============================================

    /**
     * Cambia el estado de una foto (solo admin).
     */
    public FotoDTO cambiarEstado(Integer id, String nuevoEstado) {
        Foto foto = em.find(Foto.class, id);
        if (foto == null) {
            throw new RuntimeException("Foto no encontrada");
        }

        foto.setEstado(nuevoEstado);
        if ("RECHAZADA".equals(nuevoEstado)) {
            foto.setActivo(false);
        } else if ("APROBADA".equals(nuevoEstado)) {
            foto.setActivo(true);
        }

        em.merge(foto);
        return FotoMapper.toDTO(foto);
    }

    /**
     * Cambia estado por lote (solo admin en resource).
     * No falla todo el lote ante errores puntuales: devuelve resumen.
     */
    public FotoEstadoLoteResultadoDTO cambiarEstadoLote(List<Integer> ids, String nuevoEstado) {
        FotoEstadoLoteResultadoDTO resultado = new FotoEstadoLoteResultadoDTO();
        resultado.setEstadoSolicitado(nuevoEstado);
        resultado.setTotalSolicitadas(ids != null ? ids.size() : 0);

        if (ids == null || ids.isEmpty()) {
            return resultado;
        }

        Set<Integer> procesados = new HashSet<>();

        for (Integer id : ids) {
            if (id == null) {
                resultado.setOmitidas(resultado.getOmitidas() + 1);
                resultado.agregarDetalle(null, "OMITIDA", "ID nulo");
                continue;
            }

            if (!procesados.add(id)) {
                resultado.setOmitidas(resultado.getOmitidas() + 1);
                resultado.agregarDetalle(id, "OMITIDA", "ID duplicado en el lote");
                continue;
            }

            try {
                Foto foto = em.find(Foto.class, id);
                if (foto == null) {
                    resultado.setErrores(resultado.getErrores() + 1);
                    resultado.agregarDetalle(id, "ERROR", "Foto no encontrada");
                    continue;
                }

                if (nuevoEstado.equals(foto.getEstado())) {
                    resultado.setOmitidas(resultado.getOmitidas() + 1);
                    resultado.agregarDetalle(id, "OMITIDA", "La foto ya estaba en estado " + nuevoEstado);
                    continue;
                }

                foto.setEstado(nuevoEstado);
                if ("RECHAZADA".equals(nuevoEstado)) {
                    foto.setActivo(false);
                } else if ("APROBADA".equals(nuevoEstado)) {
                    foto.setActivo(true);
                }

                em.merge(foto);
                resultado.setProcesadas(resultado.getProcesadas() + 1);
                resultado.agregarDetalle(id, "PROCESADA", "Estado actualizado");
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Error procesando foto en lote: " + id, e);
                resultado.setErrores(resultado.getErrores() + 1);
                resultado.agregarDetalle(id, "ERROR", "No se pudo procesar el cambio de estado");
            }
        }

        return resultado;
    }

    /**
     * Verifica si el usuario tiene rol de administrador.
     */
    public boolean esAdmin(String username) {
        Usuario usuario = usuarioService.buscarPorUsernameEntity(username);
        if (usuario == null) return false;
        String rol = usuario.getRol();
        return "ADMIN".equals(rol) || "SUPER_ADMIN".equals(rol);
    }

    /**
     * Verifica si un usuario autenticado puede ver una foto privada
     * (pendiente/rechazada): dueño o administrador.
     */
    public boolean puedeVerFotoPrivada(Integer idFoto, String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        Foto foto = em.find(Foto.class, idFoto);
        if (foto == null || foto.getUsuario() == null) {
            return false;
        }

        return esAdmin(username) || foto.getUsuario().getNombreUsuario().equals(username);
    }

    // ============================================
    // DESCARGA
    // ============================================

    /**
     * Obtiene el archivo original para descarga.
     */
    public File obtenerArchivoOriginal(Integer id, String username) {
        Foto foto = em.find(Foto.class, id);
        if (foto == null) return null;

        // Solo el dueño o admin pueden descargar
        if (!esAdmin(username) && !foto.getUsuario().getNombreUsuario().equals(username)) {
            throw new SecurityException("No tienes permiso para descargar esta foto");
        }

        String userUploadDir = UPLOAD_DIR + File.separator + foto.getUsuario().getNombreUsuario();
        return new File(userUploadDir + File.separator + foto.getRutaArchivo());
    }

    // ============================================
    // VISITAS
    // ============================================

    /**
     * Incrementa el contador de visitas de una foto.
     */
    public void incrementarVisitas(Integer id) {
        Foto foto = em.find(Foto.class, id);
        if (foto != null) {
            foto.setVisitas(foto.getVisitas() + 1);
            em.merge(foto);
        }
    }

    // ============================================
    // HELPERS
    // ============================================

    private void eliminarArchivosFoto(String directorio, Foto foto) {
        String[] archivos = {foto.getRutaArchivo(), foto.getRutaThumbnail(), foto.getRutaWeb()};
        for (String archivo : archivos) {
            if (archivo != null) {
                File f = new File(directorio + File.separator + archivo);
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }
}
