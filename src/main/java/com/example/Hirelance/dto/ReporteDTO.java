package com.example.Hirelance.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ReporteDTO {

    @NotNull(message = "Debes seleccionar un tipo de reporte.")
    private String tipo; // bug, fraude, soporte, otro

    @NotEmpty(message = "La descripción no puede estar vacía.")
    private String descripcion;

    // Getters y Setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}