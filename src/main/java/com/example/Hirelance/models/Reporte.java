package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte")
    private Integer idReporte;

    // Relación con el usuario que reporta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoReporte tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoReporte estado;

    @Column(name = "fecha_reporte")
    private LocalDateTime fechaReporte;

    @PrePersist
    public void prePersist() {
        fechaReporte = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoReporte.pendiente;
        }
    }

    // Enums basados en tu hirelance_db.sql
    public enum TipoReporte {
        bug, fraude, soporte, otro
    }

    public enum EstadoReporte {
        pendiente, en_revision, resuelto
    }
}