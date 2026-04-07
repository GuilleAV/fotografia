package ar.com.gav.backend.fotografo.service;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.UUID;

/**
 * Servicio para procesamiento de imágenes.
 * Genera thumbnails y versiones web a partir de la imagen original.
 * 
 * Valida magic numbers para seguridad, captura dimensiones reales,
 * y genera versiones optimizadas (WebP para web/thumb).
 */
@Stateless
public class ImageProcessor {

    // Dimensiones máximas
    private static final int THUMBNAIL_MAX_SIZE = 800;
    private static final int WEB_MAX_SIZE = 1920;

    // Calidad de compresión JPEG (0.0 - 1.0)
    private static final float THUMBNAIL_QUALITY = 0.80f;
    private static final float WEB_QUALITY = 0.85f;

    // Magic numbers para validación de formato
    private static final byte[] JPG_MAGIC = {(byte) 0xFF, (byte) 0xD8};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] WEBP_MAGIC_RIFF = {'R', 'I', 'F', 'F'};
    private static final byte[] WEBP_MAGIC_WEBP = {'W', 'E', 'B', 'P'};

    /**
     * Resultado del procesamiento de imagen con todas las métricas.
     */
    public static class ImageResult {
        public final String originalName;
        public final String thumbnailName;
        public final String webName;
        public final int originalWidth;
        public final int originalHeight;
        public final long originalSizeBytes;
        public final long thumbnailSizeBytes;
        public final long webSizeBytes;

        public ImageResult(String originalName, String thumbnailName, String webName,
                          int originalWidth, int originalHeight,
                          long originalSizeBytes, long thumbnailSizeBytes, long webSizeBytes) {
            this.originalName = originalName;
            this.thumbnailName = thumbnailName;
            this.webName = webName;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
            this.originalSizeBytes = originalSizeBytes;
            this.thumbnailSizeBytes = thumbnailSizeBytes;
            this.webSizeBytes = webSizeBytes;
        }
    }

    /**
     * Procesa una imagen y genera las versiones thumbnail y web.
     *
     * @param inputStream Stream de la imagen original
     * @param originalFilename Nombre original del archivo
     * @param uploadDir Directorio donde se guardarán las versiones
     * @return ImageResult con nombres de archivos y dimensiones
     */
    public ImageResult processImage(InputStream inputStream, String originalFilename, String uploadDir) throws IOException {
        // Envolver en BufferedInputStream para soportar mark/reset
        // (Part.getInputStream() no lo soporta nativamente)
        BufferedInputStream bufferedStream = new BufferedInputStream(inputStream);
        bufferedStream.mark(12);

        // Validar magic numbers ANTES de procesar
        validateImageFormat(bufferedStream, originalFilename);

        // Resetear stream después de validar
        bufferedStream.reset();

        // Leer la imagen original
        BufferedImage originalImage = ImageIO.read(bufferedStream);
        if (originalImage == null) {
            throw new IOException("No se pudo leer la imagen. Formato no soportado o archivo corrupto.");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // Generar nombre base único
        String baseName = generateUniqueName(originalFilename);
        String extension = getExtension(originalFilename);

        // Crear directorio si no existe
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 1. Guardar imagen original (sin compresión, formato original)
        String originalPath = uploadDir + File.separator + baseName + "_original." + extension;
        saveImage(originalImage, originalPath, extension, 1.0f);
        long originalSize = new File(originalPath).length();

        // 2. Generar y guardar thumbnail (WebP si disponible, sino JPEG)
        String thumbnailPath = uploadDir + File.separator + baseName + "_thumb.webp";
        BufferedImage thumbnail = resizeImage(originalImage, THUMBNAIL_MAX_SIZE);
        String thumbnailName = saveAsWebp(thumbnail, thumbnailPath, THUMBNAIL_QUALITY);
        long thumbnailSize = new File(uploadDir + File.separator + thumbnailName).length();

        // 3. Generar y guardar versión web (WebP si disponible, sino JPEG)
        String webPath = uploadDir + File.separator + baseName + "_web.webp";
        BufferedImage webImage = resizeImage(originalImage, WEB_MAX_SIZE);
        String webName = saveAsWebp(webImage, webPath, WEB_QUALITY);
        long webSize = new File(uploadDir + File.separator + webName).length();

        return new ImageResult(
                baseName + "_original." + extension,
                thumbnailName,
                webName,
                originalWidth,
                originalHeight,
                originalSize,
                thumbnailSize,
                webSize
        );
    }

    /**
     * Valida el formato de la imagen leyendo los magic bytes.
     * Previene ataques de extensión falsa (ej: .exe renombrado a .jpg).
     */
    private void validateImageFormat(InputStream inputStream, String filename) throws IOException {
        String ext = getExtension(filename).toLowerCase();

        // Extensiones soportadas
        if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("webp")) {
            throw new IOException("Formato no soportado: " + ext);
        }

        // Leer los primeros bytes para validar magic number
        byte[] header = new byte[12];
        int bytesRead = inputStream.read(header);
        if (bytesRead < 4) {
            throw new IOException("Archivo demasiado pequeño para ser una imagen válida");
        }

        boolean valid = false;

        if (ext.equals("jpg") || ext.equals("jpeg")) {
            // JPEG: FF D8 FF
            valid = bytesRead >= 3 && header[0] == JPG_MAGIC[0] && header[1] == JPG_MAGIC[1] && header[2] == (byte) 0xFF;
        } else if (ext.equals("png")) {
            // PNG: 89 50 4E 47
            valid = header[0] == PNG_MAGIC[0] && header[1] == PNG_MAGIC[1]
                    && header[2] == PNG_MAGIC[2] && header[3] == PNG_MAGIC[3];
        } else if (ext.equals("webp")) {
            // WebP: RIFF....WEBP
            valid = header[0] == WEBP_MAGIC_RIFF[0] && header[1] == WEBP_MAGIC_RIFF[1]
                    && header[2] == WEBP_MAGIC_RIFF[2] && header[3] == WEBP_MAGIC_RIFF[3]
                    && header[8] == WEBP_MAGIC_WEBP[0] && header[9] == WEBP_MAGIC_WEBP[1]
                    && header[10] == WEBP_MAGIC_WEBP[2] && header[11] == WEBP_MAGIC_WEBP[3];
        }

        if (!valid) {
            throw new IOException("El contenido del archivo no coincide con la extensión ." + ext
                    + ". Posible archivo corrupto o malicioso.");
        }
    }

    /**
     * Redimensiona una imagen manteniendo el aspect ratio.
     * Usa Graphics2D con hints de alta calidad.
     */
    private BufferedImage resizeImage(BufferedImage original, int maxSize) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Si la imagen ya es más pequeña que maxSize, no redimensionar
        if (originalWidth <= maxSize && originalHeight <= maxSize) {
            return original;
        }

        // Calcular nuevas dimensiones manteniendo aspect ratio
        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            newWidth = maxSize;
            newHeight = (int) ((double) originalHeight / originalWidth * maxSize);
        } else {
            newHeight = maxSize;
            newWidth = (int) ((double) originalWidth / originalHeight * maxSize);
        }

        // Crear imagen redimensionada con tipo compatible para mejor performance
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();

        // Mejorar calidad del redimensionado
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        // Rellenar fondo blanco (para imágenes con transparencia)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, newWidth, newHeight);
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resized;
    }

    /**
     * Guarda una imagen en disco con la calidad especificada.
     */
    private void saveImage(BufferedImage image, String path, String extension, float quality) throws IOException {
        File outputFile = new File(path);

        // Para JPEG, aplicar calidad
        if (extension.equals("jpg") || extension.equals("jpeg")) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);

                try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                    writer.setOutput(ios);
                    writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                    writer.dispose();
                }
                return;
            }
        }

        // Fallback: escritura directa
        ImageIO.write(image, extension, outputFile);
    }

    /**
     * Guarda una imagen en formato WebP con calidad optimizada.
     * Requiere TwelveMonkeys imageio-webp en el classpath.
     * Fallback a JPEG si no está disponible.
     * 
     * @return nombre real del archivo guardado (puede ser .jpg si fallback)
     */
    private String saveAsWebp(BufferedImage image, String path, float quality) throws IOException {
        // Intentar escribir como WebP
        Iterator<ImageWriter> webpWriters = ImageIO.getImageWritersByFormatName("webp");
        if (webpWriters.hasNext()) {
            ImageWriter writer = webpWriters.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            File outputFile = new File(path);
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
                writer.setOutput(ios);
                writer.write(null, new javax.imageio.IIOImage(image, null, null), param);
                writer.dispose();
            }
            return new File(path).getName();
        }

        // Fallback: guardar como JPEG
        String jpegPath = path.replace(".webp", ".jpg");
        saveImage(image, jpegPath, "jpg", quality);
        return new File(jpegPath).getName();
    }

    /**
     * Genera un nombre único para el archivo.
     */
    private String generateUniqueName(String originalFilename) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid;
    }

    /**
     * Obtiene la extensión del archivo.
     */
    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * Valida que la extensión sea una imagen soportada.
     */
    public static boolean isSupportedImage(String filename) {
        if (filename == null) return false;
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("webp");
    }
}
