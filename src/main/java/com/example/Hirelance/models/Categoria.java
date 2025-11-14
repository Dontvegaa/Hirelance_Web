package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String icono;

    // Método para obtener color basado en el nombre
    public String getColorClass() {
        if (nombre == null) return "bg-primary";

        String lowerName = nombre.toLowerCase();
        if (lowerName.contains("diseño") || lowerName.contains("ui") || lowerName.contains("ux")) {
            return "bg-purple-500";
        } else if (lowerName.contains("desarrollo") || lowerName.contains("web") || lowerName.contains("programación")) {
            return "bg-blue-500";
        } else if (lowerName.contains("marketing") || lowerName.contains("digital") || lowerName.contains("redes")) {
            return "bg-green-500";
        } else if (lowerName.contains("móvil") || lowerName.contains("mobile") || lowerName.contains("app")) {
            return "bg-indigo-500";
        } else if (lowerName.contains("escritura") || lowerName.contains("redacción") || lowerName.contains("contenido")) {
            return "bg-pink-500";
        } else if (lowerName.contains("data") || lowerName.contains("ciencia") || lowerName.contains("análisis")) {
            return "bg-teal-500";
        } else if (lowerName.contains("video") || lowerName.contains("animación") || lowerName.contains("motion")) {
            return "bg-red-500";
        } else if (lowerName.contains("audio") || lowerName.contains("música") || lowerName.contains("sonido")) {
            return "bg-yellow-500";
        } else {
            return "bg-primary"; // Color por defecto
        }
    }

    // Método para obtener icono si no está definido
    public String getIconoDefinido() {
        if (icono != null && !icono.trim().isEmpty()) {
            return icono;
        }

        if (nombre == null) return "fa-briefcase";

        String lowerName = nombre.toLowerCase();
        if (lowerName.contains("diseño") || lowerName.contains("ui") || lowerName.contains("ux")) {
            return "fa-palette";
        } else if (lowerName.contains("desarrollo") || lowerName.contains("web") || lowerName.contains("programación")) {
            return "fa-code";
        } else if (lowerName.contains("marketing") || lowerName.contains("digital") || lowerName.contains("redes")) {
            return "fa-bullhorn";
        } else if (lowerName.contains("móvil") || lowerName.contains("mobile") || lowerName.contains("app")) {
            return "fa-mobile-alt";
        } else if (lowerName.contains("escritura") || lowerName.contains("redacción") || lowerName.contains("contenido")) {
            return "fa-pen-fancy";
        } else if (lowerName.contains("data") || lowerName.contains("ciencia") || lowerName.contains("análisis")) {
            return "fa-chart-line";
        } else if (lowerName.contains("video") || lowerName.contains("animación") || lowerName.contains("motion")) {
            return "fa-video";
        } else if (lowerName.contains("audio") || lowerName.contains("música") || lowerName.contains("sonido")) {
            return "fa-music";
        } else {
            return "fa-briefcase"; // Icono por defecto
        }
    }
}