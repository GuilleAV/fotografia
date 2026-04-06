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
import java.util.Date;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
public class AuthService {

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
        Usuario usuario = usuarioDAO.buscarPorUsername(request.getUsername());
        if (usuario == null || !usuario.getActivo()) {
            throw new Exception("Usuario no encontrado o inactivo");
        }
        if (!PasswordUtil.verificarPassword(request.getPassword(), usuario.getPassword())) {
            throw new Exception("Credenciales inválidas");
        }
        String token = jwtUtil.generarToken(usuario.getNombreUsuario());
        // Guardamos la sesión en base de datos
        SesionActiva sesion = new SesionActiva();
        sesion.setUsuario(usuario);
        sesion.setToken(token);
        sesion.setIpAddress(ip);
        sesion.setUserAgent(userAgent);
        sesion.setFechaInicio(LocalDateTime.now());
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        sesion.setActiva(true);
        sesionActivaDAO.crear(sesion);
        // Creamos y devolvemos el LoginResponseDTO
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setExpiresIn(24L * 60 * 60);  // 24 horas en segundos
        response.setExpiresAt(LocalDateTime.now().plusHours(24));
        response.setUsuario(new UsuarioDTO(usuario));  // Convierte el Usuario a UsuarioDTO
        response.setMensaje("Login exitoso");
        return response;
    }


    public void logout(String token) {
        SesionActiva sesion = sesionActivaDAO.buscarPorToken(token);
        if (sesion != null) {
            sesion.setActiva(false);
            sesionActivaDAO.actualizar(sesion);
        }
    }


    public void solicitarRecuperacion(String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) return;
        RecuperacionPassword rec = new RecuperacionPassword();
        rec.setUsuario(usuario);
        rec.setToken(UUID.randomUUID().toString());
        rec.setExpiracion(LocalDateTime.now().plusHours(1));
        rec.setUsado(false);
        recuperacionDAO.crear(rec);
        // Enviar email real de recuperación
        emailService.enviarRecuperacionPassword(email, rec.getToken());
    }
}
