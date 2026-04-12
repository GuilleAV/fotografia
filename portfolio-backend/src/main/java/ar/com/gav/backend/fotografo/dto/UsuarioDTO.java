package ar.com.gav.backend.fotografo.dto;

import ar.com.gav.backend.fotografo.entity.Usuario;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para Usuario
 * Se usa para transferir datos del usuario sin exponer información sensible
 * NO incluye password por seguridad
 */
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idUsuario;
    private String username;
    private String email;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private String fotoPerfil;
    private String socialYoutube;
    private String socialInstagram;
    private String socialThreads;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaSesion;
    private Integer cantidadFotos;

    // Constructores
    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer idUsuario, String username, String email, String nombre, String apellido, String rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.nombreCompleto = nombre + " " + apellido;
    }

    public UsuarioDTO(Usuario usuario) {
        this.idUsuario = usuario.getId();
        this.username = usuario.getNombreUsuario();
        this.email = usuario.getEmail();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.rol = usuario.getRol();
        this.nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
        this.fotoPerfil = usuario.getFotoPerfil();
        this.socialYoutube = usuario.getSocialYoutube();
        this.socialInstagram = usuario.getSocialInstagram();
        this.socialThreads = usuario.getSocialThreads();
    }


    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getSocialYoutube() {
        return socialYoutube;
    }

    public void setSocialYoutube(String socialYoutube) {
        this.socialYoutube = socialYoutube;
    }

    public String getSocialInstagram() {
        return socialInstagram;
    }

    public void setSocialInstagram(String socialInstagram) {
        this.socialInstagram = socialInstagram;
    }

    public String getSocialThreads() {
        return socialThreads;
    }

    public void setSocialThreads(String socialThreads) {
        this.socialThreads = socialThreads;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getUltimaSesion() {
        return ultimaSesion;
    }

    public void setUltimaSesion(LocalDateTime ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public Integer getCantidadFotos() {
        return cantidadFotos;
    }

    public void setCantidadFotos(Integer cantidadFotos) {
        this.cantidadFotos = cantidadFotos;
    }
}
