package ar.com.gav.backend.fotografo.resources;

import ar.com.gav.backend.fotografo.dto.*;
import ar.com.gav.backend.fotografo.security.JwtUtil;
import ar.com.gav.backend.fotografo.security.Secured;
import ar.com.gav.backend.fotografo.service.FotoService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/fotos")
@Produces(MediaType.APPLICATION_JSON)
public class FotoResource {

    private static final Logger LOG = Logger.getLogger(FotoResource.class.getName());

    @Inject
    private FotoService fotoService;

    @Inject
    private JwtUtil jwtUtil;

    @Context
    private HttpServletRequest httpRequest;

    // ============================================
    // ENDPOINTS PÚBLICOS
    // ============================================

    @GET
    public Response listarPublicas() {
        LOG.info("=== LISTAR FOTOS PUBLICAS ===");
        try {
            List<FotoDTO> fotos = fotoService.listarAprobadas();
            LOG.info("Found " + fotos.size() + " public photos");
            return Response.ok(fotos).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listing public photos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse("Error al listar fotos")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response obtener(@PathParam("id") Long id) {
        LOG.info("=== OBTENER FOTO === ID: " + id);
        try {
            FotoDTO dto = fotoService.buscarPorId(id);
            if (dto == null) {
                LOG.warning("Photo NOT FOUND: " + id);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorResponse("Foto no encontrada")).build();
            }
            if (!"APROBADA".equals(dto.getEstado())) {
                LOG.warning("Photo not available - state: " + dto.getEstado());
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(errorResponse("Foto no disponible")).build();
            }
            fotoService.incrementarVisitas(id);
            LOG.info("Photo retrieved: " + dto.getTitulo());
            return Response.ok(dto).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error getting photo: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse("Error al obtener foto")).build();
        }
    }

    @GET
    @Path("/categoria/{idCategoria}")
    public Response listarPorCategoria(@PathParam("idCategoria") Integer idCategoria) {
        LOG.info("=== LISTAR POR CATEGORIA === ID: " + idCategoria);
        try {
            List<FotoDTO> fotos = fotoService.listarPorCategoriaAprobadas(idCategoria);
            LOG.info("Found " + fotos.size() + " photos in category " + idCategoria);
            return Response.ok(fotos).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listing photos by category: " + idCategoria, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse("Error al listar fotos")).build();
        }
    }

    // ============================================
    // ENDPOINTS AUTENTICADOS
    // ============================================

    @GET
    @Path("/mis-fotos")
    @Secured
    public Response listarMisFotos(@HeaderParam("Authorization") String authHeader) {
        try {
            String username = obtenerUsername(authHeader);
            LOG.info("=== MIS FOTOS === User: " + username);
            List<FotoDTO> fotos = fotoService.listarPorUsuario(username);
            LOG.info("Found " + fotos.size() + " photos for user: " + username);
            return Response.ok(fotos).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listing user photos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse("Error al listar fotos")).build();
        }
    }

    /**
     * Upload usando Servlet 3.0 API (Nativo de Java EE 8)
     */
    @POST
    @Path("/upload")
    @Secured
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFoto(@HeaderParam("Authorization") String authHeader) {
        String username = "unknown";
        try {
            username = obtenerUsername(authHeader);
            LOG.info("=== UPLOAD FOTO === User: " + username);

            // Validar que sea multipart
            String contentType = httpRequest.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
                LOG.warning("Request is not multipart/form-data");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("El request debe ser multipart/form-data")).build();
            }

            // Obtener las partes del request
            Collection<Part> parts = httpRequest.getParts();

            Part archivoPart = null;
            String titulo = null;
            String descripcion = null;
            Integer idCategoria = null;

            for (Part part : parts) {
                if ("archivo".equals(part.getName())) {
                    archivoPart = part;
                } else if ("titulo".equals(part.getName())) {
                    titulo = getPartValue(part);
                } else if ("descripcion".equals(part.getName())) {
                    descripcion = getPartValue(part);
                } else if ("idCategoria".equals(part.getName())) {
                    String val = getPartValue(part);
                    if (val != null) idCategoria = Integer.parseInt(val);
                }
            }

            // Validaciones
            if (archivoPart == null) {
                LOG.warning("No file part in upload request");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("El archivo es obligatorio")).build();
            }

            String nombreArchivo = getFileName(archivoPart);
            LOG.info("File: " + nombreArchivo + ", Size: " + archivoPart.getSize() + " bytes");

            if (nombreArchivo == null || !ar.com.gav.backend.fotografo.service.ImageProcessor.isSupportedImage(nombreArchivo)) {
                LOG.warning("Unsupported file format: " + nombreArchivo);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("Formato no soportado. Use: jpg, jpeg, png, webp")).build();
            }

            if (archivoPart.getSize() > 10 * 1024 * 1024) {
                LOG.warning("File too large: " + archivoPart.getSize() + " bytes");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("El archivo no puede superar 10MB")).build();
            }

            if (titulo == null || titulo.trim().isEmpty()) {
                LOG.warning("Title is empty");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("El título es obligatorio")).build();
            }

            if (idCategoria == null) {
                LOG.warning("Category is null");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("La categoría es obligatoria")).build();
            }

            LOG.info("Processing upload - Title: " + titulo + ", Category: " + idCategoria);

            // Procesar imagen y guardar
            try (InputStream archivoStream = archivoPart.getInputStream()) {
                FileUploadResponseDTO response = fotoService.subirFoto(
                        archivoStream, nombreArchivo, titulo, descripcion, idCategoria, username);
                LOG.info("Photo UPLOADED successfully: " + response.getNombreArchivo());
                return Response.status(Response.Status.CREATED).entity(response).build();
            }

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "UPLOAD FAILED for user: " + username, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Error al subir la foto: " + e.getMessage())).build();
        }
    }

    // Helpers para Servlet API
    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1)
                        .substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }

    private String getPartValue(Part part) {
        try {
            Scanner scanner = new Scanner(part.getInputStream());
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @PUT
    @Path("/{id}")
    @Secured
    public Response actualizarFoto(
            @PathParam("id") Long id,
            FotoUpdateDTO dto,
            @HeaderParam("Authorization") String authHeader) {

        try {
            String username = obtenerUsername(authHeader);
            LOG.info("=== ACTUALIZAR FOTO === ID: " + id + ", User: " + username);
            FotoDTO actualizado = fotoService.actualizarFoto(id, dto, username);
            LOG.info("Photo UPDATED: " + id);
            return Response.ok(actualizado).build();
        } catch (SecurityException e) {
            LOG.warning("FORBIDDEN - Update photo " + id + ": " + e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("No tienes permiso para editar esta foto")).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error updating photo: " + id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse("Error al actualizar: " + e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Secured
    public Response eliminarFoto(
            @PathParam("id") Long id,
            @HeaderParam("Authorization") String authHeader) {

        try {
            String username = obtenerUsername(authHeader);
            LOG.info("=== ELIMINAR FOTO === ID: " + id + ", User: " + username);
            fotoService.eliminarFoto(id, username);
            LOG.info("Photo DELETED: " + id);
            return Response.noContent().build();
        } catch (SecurityException e) {
            LOG.warning("FORBIDDEN - Delete photo " + id + ": " + e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("No tienes permiso para eliminar esta foto")).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error deleting photo: " + id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse("Error al eliminar: " + e.getMessage())).build();
        }
    }

    // ============================================
    // ENDPOINTS DE MODERACIÓN
    // ============================================

    @GET
    @Path("/admin/todas")
    @Secured
    public Response listarTodasAdmin(@HeaderParam("Authorization") String authHeader) {
        try {
            validarRolAdmin(authHeader);
            LOG.info("=== ADMIN: LISTAR TODAS ===");
            List<FotoDTO> fotos = fotoService.listarDto();
            LOG.info("Found " + fotos.size() + " total photos");
            return Response.ok(fotos).build();
        } catch (SecurityException e) {
            LOG.warning("FORBIDDEN - Admin list all: " + e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity(errorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listing all photos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse("Error interno")).build();
        }
    }

    @GET
    @Path("/admin/pendientes")
    @Secured
    public Response listarPendientes(@HeaderParam("Authorization") String authHeader) {
        try {
            validarRolAdmin(authHeader);
            LOG.info("=== ADMIN: LISTAR PENDIENTES ===");
            List<FotoDTO> fotos = fotoService.listarPorEstado("PENDIENTE");
            LOG.info("Found " + fotos.size() + " pending photos");
            return Response.ok(fotos).build();
        } catch (SecurityException e) {
            LOG.warning("FORBIDDEN - Admin list pending: " + e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity(errorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listing pending photos", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse("Error interno")).build();
        }
    }

    @PATCH
    @Path("/{id}/estado")
    @Secured
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cambiarEstado(
            @PathParam("id") Long id,
            FotoEstadoDTO dto,
            @HeaderParam("Authorization") String authHeader) {

        try {
            validarRolAdmin(authHeader);
            LOG.info("=== ADMIN: CAMBIAR ESTADO === ID: " + id + ", New state: " + dto.getEstado());

            String nuevoEstado = dto.getEstado();
            if (nuevoEstado == null || (!nuevoEstado.equals("APROBADA") && !nuevoEstado.equals("RECHAZADA"))) {
                LOG.warning("Invalid state: " + nuevoEstado);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorResponse("Estado inválido. Use: APROBADA o RECHAZADA")).build();
            }

            FotoDTO foto = fotoService.cambiarEstado(id, nuevoEstado);
            LOG.info("Photo state CHANGED: " + id + " -> " + nuevoEstado);
            return Response.ok(foto).build();
        } catch (SecurityException e) {
            LOG.warning("FORBIDDEN - Change state: " + e.getMessage());
            return Response.status(Response.Status.FORBIDDEN).entity(errorResponse(e.getMessage())).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error changing photo state: " + id, e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse("Error al cambiar estado: " + e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}/download")
    @Secured
    public Response descargarOriginal(
            @PathParam("id") Long id,
            @HeaderParam("Authorization") String authHeader) {

        try {
            String username = obtenerUsername(authHeader);
            LOG.info("=== DOWNLOAD PHOTO === ID: " + id + ", User: " + username);
            File archivo = fotoService.obtenerArchivoOriginal(id, username);

            if (archivo == null || !archivo.exists()) {
                LOG.warning("File NOT FOUND for photo: " + id);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorResponse("Archivo no encontrado")).build();
            }

            LOG.info("File downloaded: " + archivo.getName() + " (" + archivo.length() + " bytes)");
            return Response.ok(archivo)
                    .header("Content-Disposition", "attachment; filename=\"" + archivo.getName() + "\"")
                    .header("Content-Length", archivo.length())
                    .build();

        } catch (SecurityException e) {
            LOG.warning("FORBIDDEN - Download photo " + id + ": " + e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(errorResponse("No tienes permiso para descargar esta foto")).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error downloading photo: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse("Error al descargar: " + e.getMessage())).build();
        }
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    private String obtenerUsername(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Token no proporcionado");
        }
        String token = authHeader.substring("Bearer ".length());
        return jwtUtil.obtenerUsername(token);
    }

    private void validarRolAdmin(String authHeader) {
        String username = obtenerUsername(authHeader);
        if (!fotoService.esAdmin(username)) {
            throw new SecurityException("Se requiere rol de administrador. User: " + username);
        }
    }

    private Map<String, String> errorResponse(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
