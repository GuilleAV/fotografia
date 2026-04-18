package ar.com.gav.backend.fotografo.config;

import javax.enterprise.context.ApplicationScoped;

/**
 * Configuración centralizada para JWT
 * Valores que se usan en toda la aplicación
 */
@ApplicationScoped
public class JwtConfig {

    // Prefijo del token en el header
    private static final String TOKEN_PREFIX = "Bearer ";

    // Nombre del header donde va el token
    private static final String HEADER_STRING = "Authorization";

    // Issuer (quién emite el token)
    private static final String ISSUER = "portfolio-fotografo-api";

    public String getSecretKey() {
        return AppConfig.getJwtSecret();
    }

    public long getExpirationTime() {
        return AppConfig.getJwtExpirationMs();
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
