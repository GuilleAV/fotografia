package ar.com.gav.backend.fotografo.dto;

import java.io.Serializable;

/**
 * DTO público para branding del fotógrafo del sitio.
 */
public class PerfilPublicoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreMarca;
    private String nombreCompleto;
    private String fotoPerfil;
    private String emailContacto;
    private String socialYoutube;
    private String socialInstagram;
    private String socialThreads;

    public String getNombreMarca() {
        return nombreMarca;
    }

    public void setNombreMarca(String nombreMarca) {
        this.nombreMarca = nombreMarca;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
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
}
