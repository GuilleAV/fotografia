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
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private static final Logger LOG = Logger.getLogger(AuthResource.class.getName());

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
        String ip = request.getRemoteAddr();
        LOG.info("=== LOGIN REQUEST ===");
        LOG.info("Username: " + (loginDTO != null ? loginDTO.getUsername() : "null"));
        LOG.info("IP: " + ip);
        LOG.info("User-Agent: " + request.getHeader("User-Agent"));

        try {
            if (loginDTO == null) {
                LOG.warning("Login request body is null");
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Cuerpo de la petición vacío");
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            }

            if (loginDTO.getUsername() == null || loginDTO.getUsername().isEmpty()) {
                LOG.warning("Username is empty");
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Username es requerido");
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            }

            if (loginDTO.getPassword() == null || loginDTO.getPassword().isEmpty()) {
                LOG.warning("Password is empty for user: " + loginDTO.getUsername());
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Password es requerido");
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            }

            // Llama a AuthService y obtiene LoginResponseDTO
            LoginResponseDTO responseDTO = authService.login(loginDTO, ip, request.getHeader("User-Agent"));

            LOG.info("LOGIN SUCCESS for user: " + loginDTO.getUsername());
            return Response.ok(responseDTO).build();

        } catch (Exception e) {
            LOG.log(Level.SEVERE, "LOGIN FAILED for user: " + (loginDTO != null ? loginDTO.getUsername() : "unknown"), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error en la autenticación: " + e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }

    // 🔹 Solicitud de recuperación
    @POST
    @Path("/recuperar")
    public Response solicitarRecuperacion(RecuperacionEmailRequestDTO req) {
        LOG.info("=== PASSWORD RECOVERY REQUEST ===");
        LOG.info("Email: " + (req != null ? req.getEmail() : "null"));

        if (req == null || req.getEmail() == null || req.getEmail().isEmpty()) {
            LOG.warning("Recovery request missing email");
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email es requerido");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
            recuperacionPasswordService.solicitarRecuperacion(req.getEmail());
            LOG.info("Recovery email sent to: " + req.getEmail());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error sending recovery email to: " + req.getEmail(), e);
        }

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Si el email existe, se envió un enlace de recuperación.");
        return Response.ok(respuesta).build();
    }

    // 🔹 Reset de contraseña
    @POST
    @Path("/reset")
    public Response resetPassword(ResetPasswordRequestDTO req) {
        LOG.info("=== PASSWORD RESET REQUEST ===");
        LOG.info("Token present: " + (req != null && req.getToken() != null));

        try {
            if (req == null || req.getToken() == null || req.getPassword() == null) {
                LOG.warning("Reset request missing token or password");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token y password son requeridos");
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            }

            recuperacionPasswordService.resetPassword(req.getToken(), req.getPassword());
            LOG.info("Password reset successful for token: " + req.getToken().substring(0, Math.min(8, req.getToken().length())) + "...");
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Contraseña restablecida correctamente.");
            return Response.ok(respuesta).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Password reset failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al restablecer la contraseña: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
    }

    /**
     * Endpoint para cerrar sesión. Invalida el token en la base de datos.
     */
    @POST
    @Path("/logout")
    public Response logout(@HeaderParam("Authorization") String authHeader) {
        LOG.info("=== LOGOUT REQUEST ===");
        LOG.info("Auth header present: " + (authHeader != null));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.warning("Logout without valid token");
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token no proporcionado");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            authService.logout(token);
            LOG.info("Logout successful, session invalidated");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error during logout", e);
        }

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("message", "Sesión cerrada correctamente");
        return Response.ok(respuesta).build();
    }
}
