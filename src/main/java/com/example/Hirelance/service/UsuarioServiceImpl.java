package com.example.Hirelance.service;

import com.example.Hirelance.dto.RegisterDTO;
import com.example.Hirelance.models.*; // Importa todos los modelos
import com.example.Hirelance.repository.*; // Importa todos los repos
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilEstudianteRepository perfilEstudianteRepository;

    @Autowired
    private PerfilContratistaRepository perfilContratistaRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HabilidadRepository habilidadRepository;

    // ¡¡Inyecta el nuevo repositorio!!
    @Autowired
    private UniversidadRepository universidadRepository;

    // ¡NUEVO! Inyectar el repositorio de Ubicacion
    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Override
    @Transactional
    public void registrarEstudiante(RegisterDTO registerDTO, MultipartFile fotoPerfil) throws Exception {

        if (usuarioRepository.findByCorreo(registerDTO.getCorreo()).isPresent()) {
            throw new Exception("El correo electrónico ya está en uso.");
        }


        Usuario usuario = new Usuario();
        usuario.setNombre(registerDTO.getNombre());
        usuario.setApellido(registerDTO.getApellido());
        usuario.setCorreo(registerDTO.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(registerDTO.getContrasena()));
        usuario.setDui(registerDTO.getDui());
        usuario.setTelefono(registerDTO.getTelefono());
        usuario.setTipo(Usuario.TipoUsuario.estudiante);
        usuario.setEstado(Usuario.EstadoUsuario.activo);

        // --- Procesar Habilidades (Como antes) ---
        if (registerDTO.getHabilidades() != null && !registerDTO.getHabilidades().isEmpty()) {
            String[] habilidadesArray = registerDTO.getHabilidades().split(",");
            for (String nombreHabilidad : habilidadesArray) {
                Optional<Habilidad> habOpt = habilidadRepository.findByTitulo(nombreHabilidad.trim());
                if (habOpt.isPresent()) {
                    usuario.getHabilidades().add(habOpt.get());
                }
            }
        }

        // ==========================================================
        // ¡¡NUEVA LÓGICA PARA PROCESAR UNIVERSIDAD!!
        // ==========================================================
        if (registerDTO.getUniversidad() != null && !registerDTO.getUniversidad().isEmpty()) {
            String nombreUniversidad = registerDTO.getUniversidad().trim();

            // Busca la universidad. Si no existe, crea una nueva.
            Universidad uni = universidadRepository.findByNombre(nombreUniversidad)
                    .orElseGet(() -> {
                        Universidad nuevaUni = new Universidad();
                        nuevaUni.setNombre(nombreUniversidad);
                        return universidadRepository.save(nuevaUni);
                    });

            // Añade la universidad (existente o nueva) al set del usuario
            usuario.getUniversidades().add(uni);
        }
        // ==========================================================

        // Guardamos el usuario CON sus relaciones
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Creamos el perfil (como antes)
        PerfilEstudiante perfil = new PerfilEstudiante();
        perfil.setUsuario(usuarioGuardado);
        perfil.setCarrera(registerDTO.getCarrera());
        perfil.setAnioCarrera(registerDTO.getAnioCarrera());
        perfil.setDescripcion(registerDTO.getDescripcion());
        perfil.setPortafolioUrl(registerDTO.getPortafolioUrl());
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            perfil.setFotoPerfil(fotoPerfil.getBytes());
        }

        perfilEstudianteRepository.save(perfil);
    }

    @Override
    @Transactional
    public void registrarContratista(RegisterDTO registerDTO, MultipartFile logoEmpresa) throws Exception {

        if (usuarioRepository.findByCorreo(registerDTO.getCorreo()).isPresent()) {
            throw new Exception("El correo electrónico ya está en uso.");
        }


        Usuario usuario = new Usuario();
        usuario.setNombre(registerDTO.getNombre());
        usuario.setApellido(registerDTO.getApellido());
        usuario.setCorreo(registerDTO.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(registerDTO.getContrasena()));
        usuario.setDui(registerDTO.getDui());
        usuario.setTelefono(registerDTO.getTelefono());
        usuario.setTipo(Usuario.TipoUsuario.contratista);
        usuario.setEstado(Usuario.EstadoUsuario.activo);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // --- INICIO DE LA SOLUCIÓN ---

        String ciudad = null;
        String departamento = null;

        // 1. Separar la cadena "Ciudad, Departamento"
        if (registerDTO.getUbicacion() != null && !registerDTO.getUbicacion().isEmpty()) {
            String[] partes = registerDTO.getUbicacion().split(", ");
            if (partes.length == 2) {
                ciudad = partes[0];
                departamento = partes[1];
            } else {
                // Si algo falla, al menos guarda el departamento (para filtros)
                departamento = registerDTO.getUbicacion();
            }
        }

        // 2. Guardar en PerfilContratista (solo el departamento)
        PerfilContratista perfil = new PerfilContratista();
        perfil.setUsuario(usuarioGuardado);
        perfil.setEmpresa(registerDTO.getEmpresa());
        perfil.setUbicacion(departamento); // Guardamos solo el departamento para los filtros
        perfil.setDescripcion(registerDTO.getDescripcion());
        perfil.setSitioWeb(registerDTO.getSitioWeb());
        if (logoEmpresa != null && !logoEmpresa.isEmpty()) {
            perfil.setLogoEmpresa(logoEmpresa.getBytes());
        }

        perfilContratistaRepository.save(perfil);

        // 3. Guardar en la tabla Ubicaciones (el detalle)
        // (Asegúrate de haber inyectado UbicacionRepository como te indiqué antes)
        Ubicacion ubicacionDetallada = new Ubicacion();
        ubicacionDetallada.setUsuario(usuarioGuardado);
        ubicacionDetallada.setDepartamento(departamento);
        ubicacionDetallada.setCiudad(ciudad);

        ubicacionRepository.save(ubicacionDetallada);
        // --- FIN DE LA SOLUCIÓN ---
    }
}