package com.example.Hirelance.service;

import com.example.Hirelance.dto.RegisterDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UsuarioService {
    void registrarEstudiante(RegisterDTO registerDTO, MultipartFile fotoPerfil) throws Exception;
    void registrarContratista(RegisterDTO registerDTO, MultipartFile logoEmpresa) throws Exception;
}