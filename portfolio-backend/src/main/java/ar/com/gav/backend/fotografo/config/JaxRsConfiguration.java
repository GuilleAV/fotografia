package ar.com.gav.backend.fotografo.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configuración principal de JAX-RS
 * Define la ruta base para todos los endpoints REST
 *
 * Todas las rutas comenzarán con /api
 * Ejemplo: http://localhost:8080/portfolio-backend/api/categorias
 *
 * @author Adrián Morales
 * @version 1.0
 */
@ApplicationPath("api")
public class JaxRsConfiguration extends Application {
    // JAX-RS escanea automáticamente todas las clases con @Path
    // El multipart se maneja con Servlet 3.0 API (Part)
}