package ar.com.gav.backend.fotografo.service;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

/**
 * Servicio para procesamiento de imágenes.
 * Genera thumbnails y versiones web a partir de la imagen original.
 */
@Stateless
public class ImageProcessor {

    // Dimensiones máximas
    private static final int THUMBNAIL_MAX_SIZE = 800;
    private static final int WEB_MAX_SIZE = 1920;

    // Calidad de compresión JPEG (0.0 - 1.0)
    private static final float THUMBNAIL_QUALITY = 0.80f;
    private static final float WEB_QUALITY = 0.85f;

    /**
     * Procesa una imagen y genera las versiones thumbnail y web.
     *
     * @param inputStream Stream de la imagen original
     * @param originalFilename Nombre original del archivo
     * @param uploadDir Directorio donde se guardarán las versiones
     * @return Array de 3 strings: [rutaOriginal, rutaThumbnail, rutaWeb]
     */
    public String[] processImage(InputStream inputStream, String originalFilename, String uploadDir) throws IOException {
        // Leer la imagen original
        BufferedImage originalImage = ImageIO.read(inputStream);
        if (originalImage == null) {
            throw new IOException("No se pudo leer la imagen. Formato no soportado.");
        }

        // Generar nombre base único
        String baseName = generateUniqueName(originalFilename);
        String extension = getExtension(originalFilename);

        // Crear directorio si no existe
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Guardar imagen original
        String originalPath = uploadDir + File.separator + baseName + "_original." + extension;
        saveImage(originalImage, originalPath, extension, 1.0f);

        // Generar y guardar thumbnail
        String thumbnailPath = uploadDir + File.separator + baseName + "_thumb." + extension;
        BufferedImage thumbnail = resizeImage(originalImage, THUMBNAIL_MAX_SIZE);
        saveImage(thumbnail, thumbnailPath, extension, THUMBNAIL_QUALITY);

        // Generar y guardar versión web
        String webPath = uploadDir + File.separator + baseName + "_web." + extension;
        BufferedImage webImage = resizeImage(originalImage, WEB_MAX_SIZE);
        saveImage(webImage, webPath, extension, WEB_QUALITY);

        return new String[]{
                baseName + "_original." + extension,
                baseName + "_thumb." + extension,
                baseName + "_web." + extension
        };
    }

    /**
     * Redimensiona una imagen manteniendo el aspect ratio.
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

        // Crear imagen redimensionada
        BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g2d = resized.createGraphics();

        // Mejorar calidad del redimensionado
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resized;
    }

    /**
     * Guarda una imagen en disco con la calidad especificada.
     */
    private void saveImage(BufferedImage image, String path, String extension, float quality) throws IOException {
        File outputFile = new File(path);
        ImageIO.write(image, extension, outputFile);
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
