package ar.com.gav.backend.fotografo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


public class ResetPasswordRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "El token es obligatorio")
    private String token;
    @NotNull(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    // Constructores
    public ResetPasswordRequestDTO() {
    }
    public ResetPasswordRequestDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }
    // Getters y Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
    }
}