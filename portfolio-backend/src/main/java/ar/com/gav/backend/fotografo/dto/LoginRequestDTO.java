package ar.com.gav.backend.fotografo.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * DTO para petición de Login
 * Recibe credenciales del usuario
 */
public class LoginRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;

    @NotNull(message = "La contraseña es obligatoria")
    @Size(min = 3, message = "La contraseña es obligatoria")
    private String password;

    // Constructores
    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}