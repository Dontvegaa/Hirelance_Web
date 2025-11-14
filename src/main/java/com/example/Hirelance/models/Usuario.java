package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String correo;  // ✅ Mantén este

    private String contrasena;  // ✅ Mantén este

    @Column(name = "dui", length = 10)
    private String dui;

    @Column(name = "telefono", length = 14)
    private String telefono;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;

    @Enumerated(EnumType.STRING)
    private EstadoUsuario estado;

    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP")
    private LocalDateTime fechaRegistro;

    // Relación uno a uno con PerfilEstudiante - EVITA RECURSIÓN
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PerfilEstudiante perfilEstudiante;

    // Relación uno a uno con PerfilContratista - EVITA RECURSIÓN
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PerfilContratista perfilContratista;

    // Relación con Ubicaciones (NUEVO)
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Ubicacion> ubicaciones = new HashSet<>();


    // --- Relación con Habilidad (La que ya tenías) ---
    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "habilidad_estudiante", // Nombre de tu tabla intermedia
            joinColumns = { @JoinColumn(name = "id_usuario") }, // FK a esta entidad (Usuario)
            inverseJoinColumns = { @JoinColumn(name = "id_habilidad") } // FK a la otra entidad (Habilidad)
    )
    @ToString.Exclude // <-- AÑADIR
    @EqualsAndHashCode.Exclude // <-- AÑADIR
    private Set<Habilidad> habilidades = new HashSet<>();

    // ==========================================================
    // ¡¡AQUÍ ESTÁ LA SECCIÓN NUEVA QUE FALTA!!
    // ==========================================================
    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "estudiante_universidad", // Nombre de tu tabla intermedia
            joinColumns = { @JoinColumn(name = "id_usuario") }, // FK a esta entidad (Usuario)
            inverseJoinColumns = { @JoinColumn(name = "id_universidad") } // FK a la otra entidad (Universidad)
    )
    @ToString.Exclude // <-- AÑADIR
    @EqualsAndHashCode.Exclude // <-- AÑADIR
    private Set<Universidad> universidades = new HashSet<>();
    // ==========================================================
    // FIN DE LA SECCIÓN
    // ==========================================================



    @PrePersist
    public void prePersist() {
        fechaRegistro = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoUsuario.activo;
        }
    }

    // --- Enums anidados para 'tipo' y 'estado' ---
    public enum TipoUsuario {
        estudiante, contratista, admin
    }

    public enum EstadoUsuario {
        activo, inactivo, baneado
    }
}