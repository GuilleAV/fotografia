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

@Provider
@Priority(Priorities.AUTHENTICATION)
@Secured
public class JwtFilter implements ContainerRequestFilter {

    @Inject
    private JwtUtil jwtUtil;

    @Inject
    private SesionActivaDAO sesionActivaDAO;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(requestContext, "Token no proporcionado");
            return;
        }

        String token = authHeader.substring("Bearer ".length());

        if (!jwtUtil.validarToken(token)) {
            abort(requestContext, "Token inválido o expirado");
            return;
        }

        SesionActiva sesion = sesionActivaDAO.buscarPorToken(token);
        if (sesion == null || !sesion.getActiva()) {
            abort(requestContext, "Sesión no válida");
        }
    }

    private void abort(ContainerRequestContext requestContext, String mensaje) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"error\": \"" + mensaje + "\"}")
                .build());
    }
}
