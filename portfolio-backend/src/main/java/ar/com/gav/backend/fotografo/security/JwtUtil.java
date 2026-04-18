package ar.com.gav.backend.fotografo.security;

import ar.com.gav.backend.fotografo.config.AppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class JwtUtil {

    private static final Logger LOG = Logger.getLogger(JwtUtil.class.getName());

    private static final String INSECURE_DEFAULT_SECRET = "CHANGE_ME_IN_PRODUCTION_WITH_A_LONG_RANDOM_SECRET";

    private static final String SECRET_KEY = AppConfig.getJwtSecret();

    private static final long EXPIRACION_MS = AppConfig.getJwtExpirationMs();

    static {
        if (INSECURE_DEFAULT_SECRET.equals(SECRET_KEY)) {
            LOG.severe("JWT usa secret por defecto. Configurá APP_JWT_SECRET antes de producción.");
        }
    }

    public static String generarToken(String username) {
        LOG.info("=== JWT GENERAR TOKEN === User: " + username);
        try {
            Date ahora = new Date();
            Date expiracion = new Date(ahora.getTime() + EXPIRACION_MS);

            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(ahora)
                    .setExpiration(expiracion)
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            LOG.info("Token generated successfully for: " + username);
            return token;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "FAILED to generate token for: " + username, e);
            throw e;
        }
    }

    public static boolean validarToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            LOG.fine("Token validation OK");
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            LOG.warning("Token EXPIRED");
            return false;
        } catch (io.jsonwebtoken.SignatureException e) {
            LOG.warning("Token SIGNATURE INVALID");
            return false;
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Token validation FAILED: " + e.getMessage());
            return false;
        }
    }

    public String obtenerUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to extract username from token", e);
            throw new SecurityException("Token inválido");
        }
    }

}
