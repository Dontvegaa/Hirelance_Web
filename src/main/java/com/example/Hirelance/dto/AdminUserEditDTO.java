package com.example.Hirelance.dto;

import com.example.Hirelance.models.Usuario;
import lombok.Data;

@Data
public class AdminUserEditDTO {

    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String correo;
    private Usuario.TipoUsuario tipo;
    private Usuario.EstadoUsuario estado;
}