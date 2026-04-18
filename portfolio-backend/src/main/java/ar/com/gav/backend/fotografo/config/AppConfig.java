package ar.com.gav.backend.fotografo.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuración runtime para soportar local + producción.
 *
 * Prioridad de lectura:
 * 1) JVM System Properties (-DAPP_...)
 * 2) Variables de entorno (APP_...)
 * 3) app.local.properties (opcional, no versionado)
 * 4) app.properties (versionado)
 * 5) default en código
 */
public final class AppConfig {

    private static final Logger LOG = Logger.getLogger(AppConfig.class.getName());

    private static final String DEFAULT_PUBLIC_API_BASE_URL = "http://localhost:8080/portfolio-backend/api";
    private static final String DEFAULT_FRONTEND_BASE_URL = "http://localhost:4200";
    private static final String DEFAULT_UPLOAD_DIR = System.getProperty("user.home") + File.separator + "portfolio-uploads";
    private static final String DEFAULT_CORS_ALLOWED_ORIGINS =
            "http://localhost:4200,http://127.0.0.1:4200,https://sentirfotografico.com.ar,https://www.sentirfotografico.com.ar";
    private static final String DEFAULT_JWT_SECRET = "CHANGE_ME_IN_PRODUCTION_WITH_A_LONG_RANDOM_SECRET";
    private static final long DEFAULT_JWT_EXPIRATION_MS = 1000L * 60 * 60 * 24; // 24h

    private static final Properties FILE_PROPS = new Properties();

    static {
        FILE_PROPS.putAll(loadProps("app.properties"));
        FILE_PROPS.putAll(loadProps("app.local.properties"));
    }

    private AppConfig() {
    }

    private static Properties loadProps(String resourceName) {
        Properties props = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                return props;
            }
            props.load(input);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "No se pudo cargar " + resourceName, e);
        }
        return props;
    }

    public static String get(String key, String defaultValue) {
        String system = trimToNull(System.getProperty(key));
        if (system != null) return system;

        String env = trimToNull(System.getenv(key));
        if (env != null) return env;

        String fileProp = trimToNull(FILE_PROPS.getProperty(key));
        if (fileProp != null) return fileProp;

        return defaultValue;
    }

    public static String getOptional(String key) {
        return get(key, null);
    }

    public static long getLong(String key, long defaultValue) {
        String value = getOptional(key);
        if (value == null) return defaultValue;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            LOG.warning("Valor inválido para " + key + ": " + value + ". Se usa default: " + defaultValue);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getOptional(key);
        if (value == null) return defaultValue;
        return "true".equalsIgnoreCase(value) || "1".equals(value);
    }

    public static String getPublicApiBaseUrl() {
        return stripTrailingSlash(get("APP_PUBLIC_API_BASE_URL", DEFAULT_PUBLIC_API_BASE_URL));
    }

    public static String getFrontendBaseUrl() {
        return stripTrailingSlash(get("APP_FRONTEND_BASE_URL", DEFAULT_FRONTEND_BASE_URL));
    }

    public static String getUploadDir() {
        return get("APP_UPLOAD_DIR", DEFAULT_UPLOAD_DIR);
    }

    public static List<String> getCorsAllowedOrigins() {
        String originsCsv = get("APP_CORS_ALLOWED_ORIGINS", DEFAULT_CORS_ALLOWED_ORIGINS);
        List<String> result = new ArrayList<>();

        Arrays.stream(originsCsv.split(","))
                .map(AppConfig::trimToNull)
                .filter(v -> v != null && !result.contains(v))
                .forEach(result::add);

        return result;
    }

    public static String getJwtSecret() {
        return get("APP_JWT_SECRET", DEFAULT_JWT_SECRET);
    }

    public static long getJwtExpirationMs() {
        return getLong("APP_JWT_EXPIRATION_MS", DEFAULT_JWT_EXPIRATION_MS);
    }

    public static String stripTrailingSlash(String value) {
        if (value == null) return null;
        String out = value.trim();
        while (out.endsWith("/")) {
            out = out.substring(0, out.length() - 1);
        }
        return out;
    }

    private static String trimToNull(String value) {
        if (value == null) return null;
        String out = value.trim();
        return out.isEmpty() ? null : out;
    }
}
