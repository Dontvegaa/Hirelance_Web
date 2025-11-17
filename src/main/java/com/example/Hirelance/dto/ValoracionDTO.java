package com.example.Hirelance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ValoracionDTO {

    @NotNull(message = "Debes seleccionar una calificación.")
    @Min(value = 1, message = "La calificación debe ser al menos 1.")
    @Max(value = 5, message = "La calificación no puede ser mayor a 5.")
    private Integer calificacion;

    @NotEmpty(message = "El comentario no puede estar vacío.")
    private String comentario;

    // Getters y Setters
    public Integer getCalificacion() {
        return calificacion;
    }
    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}