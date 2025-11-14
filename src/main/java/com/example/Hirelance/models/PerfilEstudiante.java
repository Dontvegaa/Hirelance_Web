package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "perfil_estudiante")
public class PerfilEstudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer idPerfil;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    private String carrera;

    @Column(name = "anio_carrera")
    private Integer anioCarrera;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "portafolio_url")
    private String portafolioUrl;

    @Lob // Indica que es un "Large Object"
    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] fotoPerfil; // (Guardaremos los bytes)

    // Faltan las relaciones con Universidad y Habilidades,
    // pero las omitimos por ahora para simplificar el registro inicial.
}