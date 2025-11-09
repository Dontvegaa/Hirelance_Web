package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "perfil_estudiante")
public class PerfilEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer idPerfil;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String carrera;

    @Column(name = "anio_carrera")
    private Integer anioCarrera;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "portafolio_url")
    private String portafolioUrl;

    @Column(name = "foto_perfil")
    private String fotoPerfil; // (Guardaremos la ruta/nombre del archivo)

    // Faltan las relaciones con Universidad y Habilidades,
    // pero las omitimos por ahora para simplificar el registro inicial.
}