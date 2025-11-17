package com.example.Hirelance.dto;

public class MensajeDTO {
    private Integer receptorId; // A quién le enviamos
    private String contenido;   // Qué le enviamos

    // Getters y Setters
    public Integer getReceptorId() { return receptorId; }
    public void setReceptorId(Integer receptorId) { this.receptorId = receptorId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}