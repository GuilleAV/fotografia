package ar.com.gav.backend.fotografo.service;

import ar.com.gav.backend.fotografo.dao.SesionActivaDAO;
import ar.com.gav.backend.fotografo.dao.UsuarioDAO;
import ar.com.gav.backend.fotografo.dto.LoginRequestDTO;
import ar.com.gav.backend.fotografo.dto.LoginResponseDTO;
import ar.com.gav.backend.fotografo.dto.RecuperacionPasswordDAO;
import ar.com.gav.backend.fotografo.dto.UsuarioDTO;
import ar.com.gav.backend.fotografo.entity.RecuperacionPassword;
import ar.com.gav.backend.fotografo.entity.SesionActiva;
import ar.com.gav.backend.fotografo.entity.Usuario;
import ar.com.gav.backend.fotografo.security.JwtUtil;
import ar.com.gav.backend.fotografo.security.PasswordUtil;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class AuthService {

    private static final Logger LOG = Logger.getLogger(AuthService.class.getName());

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private SesionActivaDAO sesionActivaDAO;

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private RecuperacionPasswordDAO recuperacionDAO;

    @Inject
    private EmailService emailService;

    public LoginResponseDTO login(LoginRequestDTO request, String ip, String userAgent) throws Exception {
        LOG.info("=== AUTH SERVICE LOGIN ===");
        LOG.info("Looking up user: " + request.getUsername());

        Usuario usuario = usuarioDAO.buscarPorUsername(request.getUsername());
        if (usuario == null) {
            LOG.warning("User NOT FOUND: " + request.getUsername());
            throw new Exception("Usuario no encontrado o inactivo");
        }
        if (!usuario.getActivo()) {
            LOG.warning("User INACTIVE: " + request.getUsername());
            throw new Exception("Usuario no encontrado o inactivo");
        }
        LOG.info("User found: " + usuario.getNombreUsuario() + " (rol: " + usuario.getRol() + ", activo: " + usuario.getActivo() + ")");

        boolean passwordOk = PasswordUtil.verificarPassword(request.getPassword(), usuario.getPassword());
        if (!passwordOk) {
            LOG.warning("INVALID PASSWORD for user: " + request.getUsername());
            throw new Exception("Credenciales inválidas");
        }
        LOG.info("Password verified OK");

        String token = jwtUtil.generarToken(usuario.getNombreUsuario());
        LOG.info("JWT generated for: " + usuario.getNombreUsuario());

        // Guardamos la sesión en base de datos
        SesionActiva sesion = new SesionActiva();
        sesion.setUsuario(usuario);
        sesion.setToken(token);
        sesion.setIpAddress(ip);
        sesion.setUserAgent(userAgent);
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        sesion.setActiva(true);

        LOG.info("Creating session in DB - IP: " + ip + ", Expires: " + sesion.getFechaExpiracion());
        try {
            sesionActivaDAO.crear(sesion);
            LOG.info("Session SAVED to DB successfully, id: " + sesion.getIdSesion());
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "FAILED to save session to DB", e);
            throw new Exception("Error al guardar la sesión: " + e.getMessage(), e);
        }

        // Creamos y devolvemos el LoginResponseDTO
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(24L * 60 * 60);
        response.setExpiresAt(LocalDateTime.now().plusHours(24));
        response.setUsuario(new UsuarioDTO(usuario));
        response.setMensaje("Login exitoso");

        LOG.info("Login complete for: " + usuario.getNombreUsuario());
        return response;
    }


    public void logout(String token) {
        LOG.info("=== LOGOUT ===");
        LOG.info("Invalidating token: " + token.substring(0, Math.min(20, token.length())) + "...");
        SesionActiva sesion = sesionActivaDAO.buscarPorToken(token);
        if (sesion != null) {
            sesion.setActiva(false);
            sesionActivaDAO.actualizar(sesion);
            LOG.info("Session invalidated for user: " + sesion.getUsuario().getNombreUsuario());
        } else {
            LOG.warning("Session NOT FOUND for token");
        }
    }


    public void solicitarRecuperacion(String email) {
        LOG.info("=== PASSWORD RECOVERY ===");
        LOG.info("Email: " + email);
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            LOG.info("Email not found: " + email + " (returning silently for security)");
            return;
        }
        RecuperacionPassword rec = new RecuperacionPassword();
        rec.setUsuario(usuario);
        rec.setToken(UUID.randomUUID().toString());
        rec.setExpiracion(LocalDateTime.now().plusHours(1));
        rec.setUsado(false);
        recuperacionDAO.crear(rec);
        LOG.info("Recovery token created for user: " + usuario.getNombreUsuario());

        // Enviar email real de recuperación
        try {
            emailService.enviarRecuperacionPassword(email, rec.getToken());
            LOG.info("Recovery email sent to: " + email);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to send recovery email to: " + email, e);
        }
    }
}
