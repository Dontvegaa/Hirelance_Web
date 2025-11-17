package com.example.Hirelance.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContrato;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Usuario estudiante;

    @ManyToOne
    @JoinColumn(name = "id_contratista", nullable = false)
    private Usuario contratista;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private BigDecimal totalPago; // Usamos BigDecimal para dinero

    @Enumerated(EnumType.STRING)
    private EstadoContrato estado;

    public enum EstadoContrato {
        activo, completado, cancelado
    }

    @PrePersist
    public void prePersist() {
        this.fechaInicio = LocalDate.now();
        this.estado = EstadoContrato.activo;
    }

    // Getters y Setters
    public Integer getIdContrato() { return idContrato; }
    public void setIdContrato(Integer idContrato) { this.idContrato = idContrato; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    public Usuario getEstudiante() { return estudiante; }
    public void setEstudiante(Usuario estudiante) { this.estudiante = estudiante; }
    public Usuario getContratista() { return contratista; }
    public void setContratista(Usuario contratista) { this.contratista = contratista; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public BigDecimal getTotalPago() { return totalPago; }
    public void setTotalPago(BigDecimal totalPago) { this.totalPago = totalPago; }
    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }
}