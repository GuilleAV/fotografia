package ar.com.gav.backend.fotografo.config;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Filtro CORS para permitir peticiones desde Angular
 *
 * CORS (Cross-Origin Resource Sharing) es necesario porque:
 * - Backend: http://localhost:8080
 * - Frontend: http://localhost:4200
 *
 * Sin este filtro, el navegador bloqueará las peticiones
 *
 * @author Guillermo Vallejos
 * @version 1.0
 */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        // Permitir peticiones desde estos orígenes
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin",
                "http://localhost:4200" // Angular en desarrollo
        );

        // En producción, usar el dominio real:
        // responseContext.getHeaders().add("Access-Control-Allow-Origin", "https://mi-portfolio.com");

        // Métodos HTTP permitidos
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD"
        );

        // Headers permitidos en las peticiones
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With, Accept, Origin"
        );

        // Permitir envío de credenciales (cookies, tokens)
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials",
                "true"
        );

        // Cachear respuesta preflight (OPTIONS) por 1 hora
        responseContext.getHeaders().add(
                "Access-Control-Max-Age",
                "3600"
        );

        // Headers que el cliente puede leer en la respuesta
        responseContext.getHeaders().add(
                "Access-Control-Expose-Headers",
                "Authorization, Content-Type"
        );
    }
}
