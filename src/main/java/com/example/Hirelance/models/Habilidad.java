package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "habilidad")
public class Habilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habilidad")
    private Integer idHabilidad;

    private String titulo;
    private String descripcion;

    // Esta línea es la que buscaba el campo "habilidades" en Usuario.java
    @ManyToMany(mappedBy = "habilidades", fetch = FetchType.LAZY)
    private Set<Usuario> usuarios;
}