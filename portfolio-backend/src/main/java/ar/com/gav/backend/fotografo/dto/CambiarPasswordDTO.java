package ar.com.gav.backend.fotografo.dto;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO para cambiar contraseña
 * Requiere contraseña actual y nueva
 */
public class CambiarPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "La contraseña actual es obligatoria")
    private String passwordActual;

    @NotNull(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String passwordNueva;

    @NotNull(message = "La confirmación de contraseña es obligatoria")
    private String passwordConfirmacion;

    // Constructores
    public CambiarPasswordDTO() {
    }

    // Getters y Setters
    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }

    public String getPasswordConfirmacion() {
        return passwordConfirmacion;
    }

    public void setPasswordConfirmacion(String passwordConfirmacion) {
        this.passwordConfirmacion = passwordConfirmacion;
    }
}