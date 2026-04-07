package ar.com.gav.backend.fotografo.config;

import javax.ws.rs.core.Application;

/**
 * Configuración principal de JAX-RS
 *
 * NOTA: @ApplicationPath fue removido porque Jersey se registra
 * explícitamente en web.xml con soporte para multipart/form-data.
 *
 * Todas las rutas comienzan con /api
 * Ejemplo: http://localhost:8080/portfolio-backend/api/categorias
 *
 * @author Adrián Morales
 * @version 1.0
 */
public class JaxRsConfiguration extends Application {
    // JAX-RS escanea automáticamente todas las clases con @Path
    // El multipart se maneja con Servlet 3.0 API (Part)
    // Configurado en web.xml con <multipart-config>
}