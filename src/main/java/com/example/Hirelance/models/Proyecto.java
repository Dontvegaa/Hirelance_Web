package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer idProyecto;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Double presupuesto;

    @Enumerated(EnumType.STRING)
    private EstadoProyecto estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_limite")
    private LocalDateTime fechaLimite;

    // Relación con el contratista que publica el proyecto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contratista")
    private Usuario contratista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    // Relación con habilidades requeridas
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "proyecto_habilidad",
            joinColumns = @JoinColumn(name = "id_proyecto"),
            inverseJoinColumns = @JoinColumn(name = "id_habilidad")
    )
    private Set<Habilidad> habilidadesRequeridas = new HashSet<>();

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            // SOLUCIÓN: Usar el valor 'publicado' en minúscula
            estado = EstadoProyecto.publicado;
        }
    }

    // SOLUCIÓN: Cambiar los valores del enum a minúscula
    // para que coincidan con la BD
    public enum EstadoProyecto {
        publicado,
        en_progreso,
        finalizado,  // <-- SOLUCIÓN: Cambiado a 'finalizado'
        cancelado
    }
}