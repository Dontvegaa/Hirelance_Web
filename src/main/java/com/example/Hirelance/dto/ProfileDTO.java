package com.example.Hirelance.dto;

import lombok.Data;

@Data
public class ProfileDTO {

    // --- Campos de Usuario ---
    private String nombre;
    private String apellido;
    private String telefono;
    private String dui;

    // --- Campos de PerfilContratista ---
    private String empresa;
    private String descripcion;
    private String sitioWeb;

    // --- Campos de Ubicacion ---
    // Usaremos esto para los dropdowns
    private String departamento;
    private String ciudad;
}