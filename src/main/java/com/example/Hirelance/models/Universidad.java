package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@Entity
@Table(name = "universidad")
public class Universidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_universidad")
    private Integer idUniversidad;

    @Column(nullable = false, unique = true)
    private String nombre;

    // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
    @Lob // Le dice a JPA que es un "Large Object"
    @Column(name = "logo", columnDefinition = "MEDIUMBLOB")
    private byte[] logo; // Debe ser byte[] para coincidir con MEDIUMBLOB
    // --- FIN DE LA CORRECCIÓN ---

    // Relación inversa con Usuarios (Estudiantes)
    @ManyToMany(mappedBy = "universidades", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Usuario> usuarios;
}