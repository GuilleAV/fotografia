package ar.com.gav.backend.fotografo.security;


import ar.com.gav.backend.fotografo.dao.SesionActivaDAO;
import ar.com.gav.backend.fotografo.entity.SesionActiva;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
@Secured
public class JwtFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(JwtFilter.class.getName());

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private SesionActivaDAO sesionActivaDAO;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();
        String authHeader = requestContext.getHeaderString("Authorization");

        LOG.info("=== JWT FILTER === [" + method + " " + path + "]");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOG.warning("[" + method + " " + path + "] No Authorization header or invalid format");
            abort(requestContext, "Token no proporcionado");
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        LOG.fine("Token received: " + token.substring(0, Math.min(20, token.length())) + "...");

        if (!jwtUtil.validarToken(token)) {
            LOG.warning("[" + method + " " + path + "] Token validation FAILED");
            abort(requestContext, "Token inválido o expirado");
            return;
        }
        LOG.fine("Token validation OK");

        SesionActiva sesion = sesionActivaDAO.buscarPorToken(token);
        if (sesion == null) {
            LOG.warning("[" + method + " " + path + "] Session NOT FOUND in DB for token");
            abort(requestContext, "Sesión no válida");
            return;
        }
        if (!sesion.getActiva()) {
            LOG.warning("[" + method + " " + path + "] Session is INACTIVE for user: " + sesion.getUsuario().getNombreUsuario());
            abort(requestContext, "Sesión no válida");
            return;
        }

        LOG.fine("[" + method + " " + path + "] Auth OK - User: " + sesion.getUsuario().getNombreUsuario());
    }

    private void abort(ContainerRequestContext requestContext, String mensaje) {
        LOG.warning("ABORTING request: " + mensaje);
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + mensaje + "\"}")
                .build());
    }
}
