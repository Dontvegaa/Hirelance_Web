package com.example.Hirelance.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class StudentProfileDTO {

    // --- Campos de Usuario ---
    private String nombre;
    private String apellido;
    private String telefono;
    private String dui;

    // --- Campos de PerfilEstudiante ---
    private String carrera;
    private Integer anioCarrera;
    private String descripcion;
    private String portafolioUrl;

    // --- Campos de Relaciones (como texto) ---
    private String habilidades; // "Java, Python, Figma"
    private List<Integer> universidades = new ArrayList<>(); // Guardará los IDs [1, 2]
}