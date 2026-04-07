package ar.com.gav.backend.fotografo.security;

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

    // Clave secreta, idealmente sacada de la tabla configuracion
    private static final String SECRET_KEY = "PortfolioFotografo2024SecretKeyJWTBackendJavaEE8GlassFish5MySQLAngularSeguridadMaximaProduccion";

    private static final long EXPIRACION_MS = 1000 * 60 * 60 * 24; // 24 horas

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
