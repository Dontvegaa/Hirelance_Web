package com.example.Hirelance.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "id_usuario")
    private Usuario usuario;

    private LocalDateTime fechaExpiracion;

    // Constructores
    public PasswordResetToken() {}

    public PasswordResetToken(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        // El token expira en 24 horas
        this.fechaExpiracion = LocalDateTime.now().plusHours(24);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }
}