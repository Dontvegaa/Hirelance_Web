package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "universidad") //
public class Universidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_universidad")
    private Integer idUniversidad;

    private String nombre;
    private String logo;

    // Esta línea es la que busca el campo "universidades" en Usuario.java
    @ManyToMany(mappedBy = "universidades", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios;
}