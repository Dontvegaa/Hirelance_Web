package com.example.Hirelance.dto;

// Usamos Lombok para reducir el código (getters/setters)
// Si no usas Lombok, genera los getters y setters manualmente.
import lombok.Data;

@Data
public class RegisterDTO {

    // --- Campos Comunes ---
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String telefono;
    private String dui;

    // --- Campos de Estudiante ---
    private String universidad;
    private String carrera;
    private Integer anioCarrera;
    private String habilidades; // (Ej: "Java, Python, Figma")
    private String descripcion; // (Descripción personal)
    private String portafolioUrl;

    // --- Campos de Contratista ---
    private String empresa;
    private String ubicacion;
    private String sitioWeb;
    // Nota: El campo 'descripcion' se reutiliza para la empresa
}