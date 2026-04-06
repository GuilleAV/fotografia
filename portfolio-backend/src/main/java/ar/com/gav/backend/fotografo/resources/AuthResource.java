package ar.com.gav.backend.fotografo.resources;


import ar.com.gav.backend.fotografo.dto.*;
import ar.com.gav.backend.fotografo.service.AuthService;
import ar.com.gav.backend.fotografo.service.RecuperacionPasswordService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private AuthService authService;

    @Inject
    private RecuperacionPasswordService recuperacionPasswordService;

    @Context
    private HttpServletRequest request;

    /**
     * Endpoint de login. Valida credenciales y devuelve un JWT.
     */
    @POST
    @Path("/login")
    public Response login(LoginRequestDTO loginDTO) {
        try {
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            // Llama a AuthService y obtiene LoginResponseDTO
            LoginResponseDTO responseDTO = authService.login(loginDTO, ip, userAgent);

            return Response.ok(responseDTO).build();  // Devuelve directamente el DTO como JSON
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error en la autenticación: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }

    // 🔹 Solicitud de recuperación
    @POST
    @Path("/recuperar")
    public Response solicitarRecuperacion(RecuperacionEmailRequestDTO request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email es requerido");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        recuperacionPasswordService.solicitarRecuperacion(request.getEmail());
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Si el email existe, se envió un enlace de recuperación.");
        return Response.ok(respuesta).build();
    }



    // 🔹 Reset de contraseña
    @POST
    @Path("/reset")
    public Response resetPassword(ResetPasswordRequestDTO  request) {
        try {
            recuperacionPasswordService.resetPassword(request.getToken(), request.getPassword());
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Contraseña restablecida correctamente.");
            return Response.ok(respuesta).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al restablecer la contraseña");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    /**
     * Endpoint para cerrar sesión. Invalida el token en la base de datos.
     */
    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token no proporcionado");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        String token = authHeader.substring("Bearer ".length());
        authService.logout(token);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("message", "Sesión cerrada correctamente");
        return Response.ok(respuesta).build();
    }
}
