package ar.com.gav.backend.fotografo.service;


import ar.com.gav.backend.fotografo.dao.FotoDAO;
import ar.com.gav.backend.fotografo.dto.FileUploadResponseDTO;
import ar.com.gav.backend.fotografo.dto.FotoDTO;
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
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class FotoService extends BaseService<Foto> {

    @Inject
    private ImageProcessor imageProcessor;

    @Inject
    private UsuarioService usuarioService;

    // Directorio base para almacenamiento de fotos (configurable)
    private static final String UPLOAD_DIR = System.getProperty("user.home") + File.separator + "portfolio-uploads";

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

    public void eliminar(Long id) {
        super.eliminar(id);
    }

    public FotoDTO buscarPorId(Long id) {
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
                                           String titulo, String descripcion, Integer idCategoria,
                                           String username) throws Exception {
        // Obtener usuario
        Usuario usuario = usuarioService.buscarPorUsernameEntity(username);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado");
        }

        // Obtener categoría
        Categoria categoria = em.find(Categoria.class, idCategoria);
        if (categoria == null) {
            throw new Exception("Categoría no encontrada");
        }

        // Crear directorio del usuario
        String userUploadDir = UPLOAD_DIR + File.separator + username;
        File dir = new File(userUploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Procesar imagen (genera original, thumbnail, web)
        String[] rutas = imageProcessor.processImage(archivoStream, nombreArchivo, userUploadDir);

        // Crear entidad Foto
        Foto foto = new Foto();
        foto.setTitulo(titulo);
        foto.setDescripcion(descripcion);
        foto.setNombreArchivo(nombreArchivo);
        foto.setRutaArchivo(rutas[0]);
        foto.setRutaThumbnail(rutas[1]);
        foto.setRutaWeb(rutas[2]);
        foto.setCategoria(categoria);
        foto.setUsuario(usuario);
        foto.setEstado("PENDIENTE");
        foto.setActivo(true);

        // Obtener tamaño del archivo original
        File originalFile = new File(userUploadDir + File.separator + rutas[0]);
        foto.setTamanioKb((int) (originalFile.length() / 1024));

        super.crear(foto);

        // Construir response
        FileUploadResponseDTO response = new FileUploadResponseDTO();
        response.setIdFoto(foto.getIdFoto());
        response.setTitulo(titulo);
        response.setNombreArchivo(nombreArchivo);
        response.setRutaOriginal(rutas[0]);
        response.setRutaThumbnail(rutas[1]);
        response.setRutaWeb(rutas[2]);
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
    public FotoDTO actualizarFoto(Long id, FotoUpdateDTO dto, String username) {
        Foto foto = em.find(Foto.class, id.intValue());
        if (foto == null) {
            throw new RuntimeException("Foto no encontrada");
        }

        // Verificar permiso
        if (!esAdmin(username) && !foto.getUsuario().getNombreUsuario().equals(username)) {
            throw new SecurityException("No tienes permiso para editar esta foto");
        }

        // Actualizar campos opcionales
        if (dto.getTitulo() != null) foto.setTitulo(dto.getTitulo());
        if (dto.getDescripcion() != null) foto.setDescripcion(dto.getDescripcion());
        if (dto.getIdCategoria() != null) {
            Categoria cat = em.find(Categoria.class, dto.getIdCategoria());
            if (cat != null) foto.setCategoria(cat);
        }
        if (dto.getDestacada() != null) foto.setDestacada(dto.getDestacada());
        if (dto.getActivo() != null) foto.setActivo(dto.getActivo());

        foto.setFechaActualizacion(java.time.LocalDateTime.now());

        em.merge(foto);
        return FotoMapper.toDTO(foto);
    }

    /**
     * Elimina una foto. Solo el dueño o admin pueden.
     */
    public void eliminarFoto(Long id, String username) {
        Foto foto = em.find(Foto.class, id.intValue());
        if (foto == null) {
            throw new RuntimeException("Foto no encontrada");
        }

        if (!esAdmin(username) && !foto.getUsuario().getNombreUsuario().equals(username)) {
            throw new SecurityException("No tienes permiso para eliminar esta foto");
        }

        // Eliminar archivos del disco
        String userUploadDir = UPLOAD_DIR + File.separator + foto.getUsuario().getNombreUsuario();
        eliminarArchivosFoto(userUploadDir, foto);

        em.remove(foto);
    }

    // ============================================
    // MODERACIÓN
    // ============================================

    /**
     * Cambia el estado de una foto (solo admin).
     */
    public FotoDTO cambiarEstado(Long id, String nuevoEstado) {
        Foto foto = em.find(Foto.class, id.intValue());
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
     * Verifica si el usuario tiene rol de administrador.
     */
    public boolean esAdmin(String username) {
        Usuario usuario = usuarioService.buscarPorUsernameEntity(username);
        if (usuario == null) return false;
        String rol = usuario.getRol();
        return "ADMIN".equals(rol) || "SUPER_ADMIN".equals(rol);
    }

    // ============================================
    // DESCARGA
    // ============================================

    /**
     * Obtiene el archivo original para descarga.
     */
    public File obtenerArchivoOriginal(Long id, String username) {
        Foto foto = em.find(Foto.class, id.intValue());
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
    public void incrementarVisitas(Long id) {
        Foto foto = em.find(Foto.class, id.intValue());
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
