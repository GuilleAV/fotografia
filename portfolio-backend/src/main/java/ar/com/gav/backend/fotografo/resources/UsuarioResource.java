package ar.com.gav.backend.fotografo.resources;

import ar.com.gav.backend.fotografo.dto.PerfilPublicoDTO;
import ar.com.gav.backend.fotografo.dto.UsuarioCreateDTO;
import ar.com.gav.backend.fotografo.dto.UsuarioDTO;
import ar.com.gav.backend.fotografo.security.PasswordUtil;
import ar.com.gav.backend.fotografo.security.Secured;
import ar.com.gav.backend.fotografo.service.UsuarioService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private static final Logger LOG = Logger.getLogger(UsuarioResource.class.getName());

    @Inject
    private UsuarioService usuarioService;

    // PÚBLICO: Perfil principal del fotógrafo
    @GET
    @Path("/publico/perfil")
    public Response obtenerPerfilPublico() {
        LOG.info("=== PERFIL PUBLICO ===");
        try {
            PerfilPublicoDTO perfil = usuarioService.obtenerPerfilPublicoPrincipal();
            if (perfil == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No hay fotógrafo público configurado").build();
            }
            return Response.ok(perfil).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error getting public profile", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Actualizar perfil público principal
    @PUT
    @Path("/admin/perfil-publico")
    @Secured
    public Response actualizarPerfilPublico(PerfilPublicoDTO dto) {
        LOG.info("=== ACTUALIZAR PERFIL PUBLICO ===");
        try {
            PerfilPublicoDTO actualizado = usuarioService.actualizarPerfilPublicoPrincipal(dto);
            return Response.ok(actualizado).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error updating public profile", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Listar todos los usuarios
    @GET
    @Secured
    public Response listarTodos() {
        LOG.info("=== LISTAR USUARIOS ===");
        try {
            List<UsuarioDTO> usuarios = usuarioService.listarDto();
            LOG.info("Found " + usuarios.size() + " users");
            return Response.ok(usuarios).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error listing users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Crear usuario
    @POST
    @Secured
    public Response crear(UsuarioCreateDTO dto) {
        LOG.info("=== CREAR USUARIO ===");
        LOG.info("Username: " + (dto != null ? dto.getUsername() : "null"));
        LOG.info("Email: " + (dto != null ? dto.getEmail() : "null"));
        LOG.info("Rol: " + (dto != null ? dto.getRol() : "null"));

        try {
            if (dto == null) {
                LOG.warning("Create user request body is null");
                return Response.status(Response.Status.BAD_REQUEST).entity("Cuerpo de la petición vacío").build();
            }
            if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                LOG.warning("Password is empty for user: " + dto.getUsername());
                return Response.status(Response.Status.BAD_REQUEST).entity("La contraseña es obligatoria").build();
            }
            String hashedPassword = PasswordUtil.hashPassword(dto.getPassword());
            usuarioService.crear(dto, hashedPassword);
            LOG.info("User CREATED: " + dto.getUsername());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Usuario creado exitosamente");
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error creating user: " + (dto != null ? dto.getUsername() : "unknown"), e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Actualizar usuario
    @PUT
    @Path("/{id}")
    @Secured
    public Response actualizar(@PathParam("id") Integer id, UsuarioDTO dto) {
        LOG.info("=== ACTUALIZAR USUARIO === ID: " + id);
        try {
            dto.setIdUsuario(id);
            usuarioService.actualizar(dto);
            LOG.info("User UPDATED: " + id);
            return Response.ok(dto).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error updating user: " + id, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Eliminar usuario
    @DELETE
    @Path("/{id}")
    @Secured
    public Response eliminar(@PathParam("id") Integer id) {
        LOG.info("=== ELIMINAR USUARIO === ID: " + id);
        try {
            usuarioService.eliminar(id);
            LOG.info("User DELETED: " + id);
            return Response.noContent().build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error deleting user: " + id, e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
