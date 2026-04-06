package ar.com.gav.backend.fotografo.resources;

import ar.com.gav.backend.fotografo.dto.UsuarioDTO;
import ar.com.gav.backend.fotografo.security.PasswordUtil;
import ar.com.gav.backend.fotografo.security.Secured;
import ar.com.gav.backend.fotografo.service.UsuarioService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    private UsuarioService usuarioService;

    // ADMIN: Listar todos los usuarios
    @GET
    @Secured
    public Response listarTodos() {
        return Response.ok(usuarioService.listarDto()).build();
    }

    // ADMIN: Crear usuario
    @POST
    @Secured
    public Response crear(UsuarioDTO dto) {
        try {
            if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("La contraseña es obligatoria").build();
            }
            dto.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
            usuarioService.crear(dto);
            return Response.status(Response.Status.CREATED).entity(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Actualizar usuario
    @PUT
    @Path("/{id}")
    @Secured
    public Response actualizar(@PathParam("id") Integer id, UsuarioDTO dto) {
        try {
            dto.setIdUsuario(id);
            // Si envía contraseña nueva, la hasheamos. Si no, la dejamos null para no sobreescribir.
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                dto.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
            } else {
                dto.setPassword(null);
            }
            usuarioService.actualizar(dto);
            return Response.ok(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Eliminar usuario
    @DELETE
    @Path("/{id}")
    @Secured
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            usuarioService.eliminar(id.longValue());
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
