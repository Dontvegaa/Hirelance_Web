package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "valoraciones")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valoracion")
    private Integer idValoracion;

    // Usuario que escribe la reseña
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_emisor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario emisor;

    // Usuario que recibe la reseña
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receptor")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario receptor;

    // Proyecto sobre el que se opina
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Proyecto proyecto;

    @Column(name = "calificacion")
    private Integer calificacion; // (1-5)

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        fecha = LocalDateTime.now();
    }
}