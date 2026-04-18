package ar.com.gav.backend.fotografo.config;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

/**
 * Filtro CORS configurable para entornos local y producción.
 * Los orígenes permitidos se leen de APP_CORS_ALLOWED_ORIGINS.
 *
 * @author Guillermo Vallejos
 * @version 1.0
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    private static final List<String> ALLOWED_ORIGINS = AppConfig.getCorsAllowedOrigins();

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        String requestOrigin = requestContext.getHeaderString("Origin");

        if (requestOrigin != null && ALLOWED_ORIGINS.contains(requestOrigin)) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", requestOrigin);
            responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
            responseContext.getHeaders().putSingle("Vary", "Origin");
        }

        // Métodos HTTP permitidos
        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD"
        );

        // Headers permitidos en las peticiones
        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Accept, Origin"
        );

        // Cachear respuesta preflight (OPTIONS) por 1 hora
        responseContext.getHeaders().putSingle(
                "Access-Control-Max-Age",
                "3600"
        );

        // Headers que el cliente puede leer en la respuesta
        // Incluir ETag y Cache-Control para optimización de imágenes
        responseContext.getHeaders().putSingle(
                "Access-Control-Expose-Headers",
                "Authorization, Content-Type, ETag, Cache-Control, Last-Modified, Content-Length"
        );
    }
}
