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

    // Coincidiendo con la BD: 'propuesta'
    @Column(name = "propuesta", columnDefinition = "TEXT")
    private String propuesta;

    // Coincidiendo con la BD: 'monto_ofertado'
    @Column(name = "monto_ofertado")
    private Double montoOfertado;

    // Coincidiendo con la BD: 'tiempo_estimado'
    @Column(name = "tiempo_estimado")
    private String tiempoEstimado;

    @Enumerated(EnumType.STRING)
    private EstadoPostulacion estado;

    @Column(name = "fecha_postulacion")
    private LocalDateTime fechaPostulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudiante")
    private Usuario estudiante;

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
        pendiente, aceptada, rechazada, cancelada
    }
}