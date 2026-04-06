package ar.com.gav.backend.fotografo.dto;


import java.io.Serializable;

/**
 * DTO para respuesta de carga de archivos
 * Devuelve información del archivo subido
 */
public class FileUploadResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreArchivo;
    private String nombreOriginal;
    private String urlCompleta;
    private Long tamanioBytes;
    private Integer tamanioKb;
    private String mimeType;
    private Integer anchoPx;
    private Integer altoPx;
    private String dimensiones;
    private Boolean exitoso;
    private String mensaje;

    // Campos para respuesta de upload con procesamiento
    private Integer idFoto;
    private String titulo;
    private String rutaOriginal;
    private String rutaThumbnail;
    private String rutaWeb;
    private String estado;

    // Constructores
    public FileUploadResponseDTO() {
    }

    public FileUploadResponseDTO(String nombreArchivo, String urlCompleta, Long tamanioBytes) {
        this.nombreArchivo = nombreArchivo;
        this.urlCompleta = urlCompleta;
        this.tamanioBytes = tamanioBytes;
        this.tamanioKb = (int) (tamanioBytes / 1024);
        this.exitoso = true;
        this.mensaje = "Archivo subido exitosamente";
    }

    // Getters y Setters
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getUrlCompleta() {
        return urlCompleta;
    }

    public void setUrlCompleta(String urlCompleta) {
        this.urlCompleta = urlCompleta;
    }

    public Long getTamanioBytes() {
        return tamanioBytes;
    }

    public void setTamanioBytes(Long tamanioBytes) {
        this.tamanioBytes = tamanioBytes;
    }

    public Integer getTamanioKb() {
        return tamanioKb;
    }

    public void setTamanioKb(Integer tamanioKb) {
        this.tamanioKb = tamanioKb;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Integer getAnchoPx() {
        return anchoPx;
    }

    public void setAnchoPx(Integer anchoPx) {
        this.anchoPx = anchoPx;
    }

    public Integer getAltoPx() {
        return altoPx;
    }

    public void setAltoPx(Integer altoPx) {
        this.altoPx = altoPx;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public Boolean getExitoso() {
        return exitoso;
    }

    public void setExitoso(Boolean exitoso) {
        this.exitoso = exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getRutaOriginal() {
        return rutaOriginal;
    }

    public void setRutaOriginal(String rutaOriginal) {
        this.rutaOriginal = rutaOriginal;
    }

    public String getRutaThumbnail() {
        return rutaThumbnail;
    }

    public void setRutaThumbnail(String rutaThumbnail) {
        this.rutaThumbnail = rutaThumbnail;
    }

    public String getRutaWeb() {
        return rutaWeb;
    }

    public void setRutaWeb(String rutaWeb) {
        this.rutaWeb = rutaWeb;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}