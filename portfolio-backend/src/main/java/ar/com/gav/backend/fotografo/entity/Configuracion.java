package ar.com.gav.backend.fotografo.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Configuracion implements Serializable {

    private static final long serialVersionUID = 1L;

    // ============================================
    // CONSTANTES DE TIPOS
    // ============================================

    public static final String TIPO_STRING = "STRING";
    public static final String TIPO_NUMBER = "NUMBER";
    public static final String TIPO_BOOLEAN = "BOOLEAN";
    public static final String TIPO_JSON = "JSON";

    // ============================================
    // ATRIBUTOS
    // ============================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer idConfig;


    @Column(name = "clave", nullable = false, unique = true, length = 100)
    private String clave;


    @Column(name = "valor", nullable = false, columnDefinition = "TEXT")
    private String valor;


    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo; // STRING, NUMBER, BOOLEAN, JSON

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ============================================
    // CONSTRUCTORES
    // ============================================

    public Configuracion() {
    }

    public Configuracion(String clave, String valor, String tipo) {
        this.clave = clave;
        this.valor = valor;
        this.tipo = tipo;
    }

    public Configuracion(String clave, String valor, String tipo, String descripcion) {
        this(clave, valor, tipo);
        this.descripcion = descripcion;
    }

    public Integer getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Integer idConfig) {
        this.idConfig = idConfig;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuracion that = (Configuracion) o;
        return Objects.equals(idConfig, that.idConfig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idConfig);
    }

    @Override
    public String toString() {
        return "Configuracion{" +
                "idConfig=" + idConfig +
                ", clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}
