package ar.com.gav.backend.fotografo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class JwtUtil {

    // Clave secreta, idealmente sacada de la tabla configuracion
    private static final String SECRET_KEY = "PortfolioFotografo2024SecretKeyJWTBackendJavaEE8GlassFish5MySQLAngularSeguridadMaximaProduccion";

    private static final long EXPIRACION_MS = 1000 * 60 * 60 * 24; // 24 horas

    public static String generarToken(String username) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + EXPIRACION_MS);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static boolean validarToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String obtenerUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}
