package ar.com.gav.backend.fotografo.resources;

import ar.com.gav.backend.fotografo.dto.CategoriaDTO;
import ar.com.gav.backend.fotografo.entity.Categoria;
import ar.com.gav.backend.fotografo.mapper.CategoriaMapper;
import ar.com.gav.backend.fotografo.security.Secured;
import ar.com.gav.backend.fotografo.service.CategoriaService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/categorias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoriaResource {

    @Inject
    private CategoriaService categoriaService;

    // Público: Listar activas
    @GET
    @Path("/activas")
    public Response listarActivas() {
        List<CategoriaDTO> dtos = categoriaService.listarActivas().stream()
                .map(CategoriaMapper::toDTO)
                .collect(Collectors.toList());
        return Response.ok(dtos).build();
    }

    // ADMIN: Listar todas
    @GET
    @Secured
    public Response listarTodas() {
        return Response.ok(categoriaService.listarDto()).build();
    }

    // ADMIN: Crear
    @POST
    @Secured
    public Response crear(CategoriaDTO dto) {
        try {
            categoriaService.crear(dto);
            return Response.status(Response.Status.CREATED).entity(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Actualizar
    @PUT
    @Path("/{id}")
    @Secured
    public Response actualizar(@PathParam("id") Integer id, CategoriaDTO dto) {
        try {
            dto.setIdCategoria(id);
            categoriaService.actualizar(dto);
            return Response.ok(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // ADMIN: Eliminar
    @DELETE
    @Path("/{id}")
    @Secured
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            categoriaService.eliminar(id.longValue());
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
