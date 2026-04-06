package ar.com.gav.backend.fotografo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


public class RecuperacionEmailRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    @NotNull(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;
    // Constructores
    public RecuperacionEmailRequestDTO() {
    }
    public RecuperacionEmailRequestDTO(String email) {
        this.email = email;
    }
    // Getters y Setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}