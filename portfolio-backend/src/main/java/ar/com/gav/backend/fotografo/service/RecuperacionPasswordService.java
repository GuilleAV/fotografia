package ar.com.gav.backend.fotografo.service;


import ar.com.gav.backend.fotografo.dao.SesionActivaDAO;
import ar.com.gav.backend.fotografo.dao.UsuarioDAO;
import ar.com.gav.backend.fotografo.dto.RecuperacionPasswordDAO;
import ar.com.gav.backend.fotografo.entity.RecuperacionPassword;
import ar.com.gav.backend.fotografo.entity.SesionActiva;
import ar.com.gav.backend.fotografo.entity.Usuario;
import ar.com.gav.backend.fotografo.security.PasswordUtil;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class RecuperacionPasswordService {

    @Inject
    private UsuarioDAO usuarioDAO;

    @Inject
    private RecuperacionPasswordDAO recuperacionDAO;

    @Inject
    private SesionActivaDAO sesionActivaDAO;

    @Inject
    private EmailService emailService;

    public void solicitarRecuperacion(String email) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            return; // No revelar información
        }

        RecuperacionPassword rec = new RecuperacionPassword();
        rec.setUsuario(usuario);
        rec.setToken(UUID.randomUUID().toString());
        rec.setExpiracion(LocalDateTime.now().plusHours(1));
        rec.setUsado(false);

        recuperacionDAO.crear(rec);

        // Enviar email real de recuperación
        emailService.enviarRecuperacionPassword(email, rec.getToken());
    }

    public void resetPassword(String token, String nuevaPassword) throws Exception {
        RecuperacionPassword rec = recuperacionDAO.buscarPorToken(token);

        if (rec == null || rec.isUsado() || rec.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new Exception("Token inválido o expirado");
        }

        Usuario usuario = rec.getUsuario();
        usuario.setPassword(PasswordUtil.hashPassword(nuevaPassword));
        usuarioDAO.actualizar(usuario);

        rec.setUsado(true);
        recuperacionDAO.actualizar(rec);

        // Invalida sesiones anteriores
        List<SesionActiva> sesiones = sesionActivaDAO.buscarPorUsuario(usuario);
        for (SesionActiva s : sesiones) {
            s.setActiva(false);
            sesionActivaDAO.actualizar(s);
        }
    }
}
