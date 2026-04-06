package ar.com.gav.backend.fotografo.dto;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para respuestas de error
 * Formato estándar para errores de la API
 */
public class ErrorResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer status;
    private String error;
    private String mensaje;
    private String path;
    private LocalDateTime timestamp;
    private List<String> detalles = new ArrayList<>();

    // Constructores
    public ErrorResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(Integer status, String error, String mensaje) {
        this();
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
    }

    public ErrorResponseDTO(Integer status, String error, String mensaje, String path) {
        this(status, error, mensaje);
        this.path = path;
    }

    // Getters y Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<String> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(String detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
    }
}