package com.example.Hirelance.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idNotificacion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario; // A quién le llega la notificación

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    private boolean leido = false;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    // Constructor vacío y PrePersist
    public Notificacion() {}

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }

    // Enums
    public enum TipoNotificacion {
        mensaje, proyecto, valoracion, sistema
    }

    @Column(name = "enlace")
    private String enlace;

    // Getters y Setters obligatorios
    public String getEnlace() { return enlace; }
    public void setEnlace(String enlace) { this.enlace = enlace; }

    public Integer getIdNotificacion() { return idNotificacion; }
    public void setIdNotificacion(Integer idNotificacion) { this.idNotificacion = idNotificacion; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public TipoNotificacion getTipo() { return tipo; }
    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}