package ar.com.gav.backend.fotografo.config;

import javax.enterprise.context.ApplicationScoped;

/**
 * Configuración centralizada para JWT
 * Valores que se usan en toda la aplicación
 */
@ApplicationScoped
public class JwtConfig {

    // Clave secreta para firmar tokens JWT
    // En producción, esto debería estar en una variable de entorno
    private static final String SECRET_KEY = "MI_CLAVE_SUPER_SECRETA_2024_PORTFOLIO_FOTOGRAFO";

    // Tiempo de expiración del token (24 horas)
    private static final long EXPIRATION_TIME = 86400000; // 24 * 60 * 60 * 1000 ms

    // Prefijo del token en el header
    private static final String TOKEN_PREFIX = "Bearer ";

    // Nombre del header donde va el token
    private static final String HEADER_STRING = "Authorization";

    // Issuer (quién emite el token)
    private static final String ISSUER = "portfolio-fotografo-api";

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public String getTokenPrefix() {
        return TOKEN_PREFIX;
    }

    public String getHeaderString() {
        return HEADER_STRING;
    }

    public String getIssuer() {
        return ISSUER;
    }
}
