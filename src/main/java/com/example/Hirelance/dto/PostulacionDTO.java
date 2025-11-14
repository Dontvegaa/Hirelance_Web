package com.example.Hirelance.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PostulacionDTO {

    @NotEmpty(message = "La propuesta no puede estar vacía.")
    @Size(min = 10, message = "La propuesta debe tener al menos 10 caracteres.")
    private String propuesta;

    // Quita la validación @NotNull si quieres que este campo sea opcional
    @NotNull(message = "Debes ingresar un monto.")
    @Positive(message = "El monto ofertado debe ser positivo.")
    private Double montoOfertado;

    // Getters y Setters
    public String getPropuesta() {
        return propuesta;
    }

    public void setPropuesta(String propuesta) {
        this.propuesta = propuesta;
    }

    public Double getMontoOfertado() {
        return montoOfertado;
    }

    public void setMontoOfertado(Double montoOfertado) {
        this.montoOfertado = montoOfertado;
    }
}