package com.example.Hirelance.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ProjectDTO {

    private String titulo;
    private String descripcion;
    private Double presupuesto;

    // Usamos LocalDate para el input de fecha,
    // ya que no necesitamos la hora exacta del formulario.
    private LocalDate fechaLimite;

    // Recibiremos el ID de la categoría desde el <select> del formulario
    private Integer idCategoria;

    // (Más adelante podríamos añadir habilidades aquí)
}