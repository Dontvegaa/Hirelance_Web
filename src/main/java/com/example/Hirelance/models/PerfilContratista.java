package com.example.Hirelance.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "perfil_contratista")
public class PerfilContratista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Integer idPerfil;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String empresa;
    private String ubicacion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "sitio_web")
    private String sitioWeb;

    @Column(name = "logo_empresa")
    private String logoEmpresa; // (Guardaremos la ruta/nombre del archivo)
}