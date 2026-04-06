package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para respuestas exitosas genéricas
 * Formato estándar para operaciones exitosas
 */
public class SuccessResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String mensaje;
    private Object data;
    private LocalDateTime timestamp;

    // Constructores
    public SuccessResponseDTO() {
        this.success = true;
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponseDTO(String mensaje) {
        this();
        this.mensaje = mensaje;
    }

    public SuccessResponseDTO(String mensaje, Object data) {
        this(mensaje);
        this.data = data;
    }

    // Getters y Setters
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}