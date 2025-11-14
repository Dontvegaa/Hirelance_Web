package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "postulaciones")
public class Postulacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_postulacion")
    private Integer idPostulacion;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    private Double presupuestoPropuesto;

    @Enumerated(EnumType.STRING)
    private EstadoPostulacion estado;

    @Column(name = "fecha_postulacion")
    private LocalDateTime fechaPostulacion;

    // Relación con el estudiante que se postula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante")
    private Usuario estudiante;

    // Relación con el proyecto al que se postula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    @PrePersist
    public void prePersist() {
        fechaPostulacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoPostulacion.pendiente;
        }
    }

    public enum EstadoPostulacion {
        pendiente,  // Cambia a minúscula para coincidir con la BD
        aceptada,   // Cambia a minúscula
        rechazada,  // Cambia a minúscula
        cancelada
    }
}