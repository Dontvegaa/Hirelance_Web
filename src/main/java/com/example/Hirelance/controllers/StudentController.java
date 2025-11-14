package com.example.Hirelance.controllers;

import com.example.Hirelance.config.CustomUserDetails;
import com.example.Hirelance.models.Usuario;
import com.example.Hirelance.repository.PostulacionRepository;
import com.example.Hirelance.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.Hirelance.models.Postulacion; // ¡NUEVO!
import com.example.Hirelance.models.PerfilEstudiante;
import com.example.Hirelance.models.Habilidad;
import com.example.Hirelance.models.Universidad;
import java.util.Set;
import java.util.Base64;
import com.example.Hirelance.dto.StudentProfileDTO; // ¡NUEVO!
import com.example.Hirelance.repository.HabilidadRepository; // ¡NUEVO!
import com.example.Hirelance.repository.UniversidadRepository; // ¡NUEVO!
import com.example.Hirelance.repository.PerfilEstudianteRepository; // ¡NUEVO!
import java.util.stream.Collectors; // ¡NUEVO!
import java.util.List; // ¡NUEVO!
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.HashSet; // ¡NUEVO!
import com.example.Hirelance.dto.PasswordChangeDTO;
import org.springframework.security.crypto.password.PasswordEncoder; // ¡NUEVO!

@Controller
@RequestMapping("/student") // Todas las URLs de este controlador empezarán con /student
@PreAuthorize("hasAuthority('estudiante')") // ¡Seguridad! Solo para estudiantes
public class StudentController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private PerfilEstudianteRepository perfilEstudianteRepository;

    @Autowired
    private HabilidadRepository habilidadRepository;

    @Autowired
    private UniversidadRepository universidadRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * Muestra el dashboard principal del estudiante con estadísticas.
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("nombreUsuario", usuario.getNombre());

        // --- INICIO DE LA LÓGICA DEL DASHBOARD ---

        // 1. Buscar todas las postulaciones del estudiante
        List<Postulacion> misPostulaciones = postulacionRepository
                .findByEstudianteIdUsuario(usuario.getIdUsuario()); //

        // 2. Calcular Estadísticas
        long totalPostulaciones = misPostulaciones.size();

        long totalAceptadas = misPostulaciones.stream()
                .filter(p -> p.getEstado() == Postulacion.EstadoPostulacion.aceptada) //
                .count();

        long totalPendientes = misPostulaciones.stream()
                .filter(p -> p.getEstado() == Postulacion.EstadoPostulacion.pendiente) //
                .count();

        // 3. Añadir las estadísticas al modelo
        model.addAttribute("totalPostulaciones", totalPostulaciones);
        model.addAttribute("totalAceptadas", totalAceptadas);
        model.addAttribute("totalPendientes", totalPendientes);

        // (Más adelante añadiremos una lista de postulaciones recientes aquí)

        // --- FIN DE LA LÓGICA DEL DASHBOARD ---

        return "student/dashboard";
    }

    /**
     * Muestra la lista completa de postulaciones del estudiante.
     */
    @GetMapping("/my-applications")
    public String showMyApplications(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();

        // 1. Buscar todas las postulaciones usando el nuevo método
        List<Postulacion> misPostulaciones = postulacionRepository
                .findAllByEstudianteIdWithProyecto(usuario.getIdUsuario());

        // 2. Añadir la lista al modelo
        model.addAttribute("postulaciones", misPostulaciones);

        return "student/my-applications"; // Apunta a student/my-applications.html
    }

    /**
     * Muestra la página de VISUALIZACIÓN del perfil del estudiante.
     */
    @GetMapping("/profile")
    public String showProfileView(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        // SOLUCIÓN: Usar el ID del principal para hacer UN solo query
        // que traiga (FETCH) todas las colecciones LAZY.
        Usuario usuario = usuarioRepository.findByIdWithFullProfile(userDetails.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userDetails.getUsuario().getIdUsuario()));

        // Ahora estas colecciones ya están cargadas
        PerfilEstudiante perfil = usuario.getPerfilEstudiante();
        Set<Habilidad> habilidades = usuario.getHabilidades();
        Set<Universidad> universidades = usuario.getUniversidades();

        // (Ya no necesitamos .size() porque el JOIN FETCH las inicializó)

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        model.addAttribute("habilidades", habilidades);
        model.addAttribute("universidades", universidades);

        // Convertir la foto de perfil a Base64
        if (perfil != null && perfil.getFotoPerfil() != null) {
            String base64Photo = Base64.getEncoder().encodeToString(perfil.getFotoPerfil());
            model.addAttribute("currentPhoto", base64Photo);
        }

        return "student/profile"; // Apunta a student/profile.html
    }

    /**
     * Muestra la página "Hub" de Ajustes del estudiante.
     */
    @GetMapping("/settings")
    public String showSettingsHub() {
        return "student/settings"; // Apunta a student/settings.html
    }

    /**
     * Muestra el formulario para EDITAR el perfil del estudiante.
     * (Versión actualizada con multi-select de universidades)
     */
    @GetMapping("/edit-profile")
    public String showEditProfileForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        Usuario usuario = usuarioRepository.findByIdWithFullProfile(userDetails.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilEstudiante perfil = usuario.getPerfilEstudiante();
        StudentProfileDTO profileDTO = new StudentProfileDTO();

        // 1. Poblar campos de Usuario y Perfil (igual que antes)
        profileDTO.setNombre(usuario.getNombre());
        profileDTO.setApellido(usuario.getApellido());
        profileDTO.setTelefono(usuario.getTelefono());
        profileDTO.setDui(usuario.getDui());

        if (perfil != null) {
            profileDTO.setCarrera(perfil.getCarrera());
            profileDTO.setAnioCarrera(perfil.getAnioCarrera());
            profileDTO.setDescripcion(perfil.getDescripcion());
            profileDTO.setPortafolioUrl(perfil.getPortafolioUrl());
        }

        // 2. Poblar Habilidades como String (igual que antes)
        String skillsString = usuario.getHabilidades().stream()
                .map(Habilidad::getTitulo)
                .collect(Collectors.joining(", "));
        profileDTO.setHabilidades(skillsString);

        // --- INICIO DE LA SOLUCIÓN ---
        // 3. Cargar TODAS las universidades para el dropdown
        List<Universidad> allUniversidades = universidadRepository.findAll();
        model.addAttribute("allUniversidades", allUniversidades);

        // 4. Poblar el DTO con los IDs de las universidades actuales del estudiante
        List<Integer> currentUniIds = usuario.getUniversidades().stream()
                .map(Universidad::getIdUniversidad)
                .collect(Collectors.toList());
        profileDTO.setUniversidades(currentUniIds);
        // --- FIN DE LA SOLUCIÓN ---

        model.addAttribute("profileDTO", profileDTO);

        // 5. Cargar foto actual (igual que antes)
        if (perfil != null && perfil.getFotoPerfil() != null) {
            String base64Photo = Base64.getEncoder().encodeToString(perfil.getFotoPerfil());
            model.addAttribute("currentPhoto", base64Photo);
        }

        return "student/edit-profile";
    }

    /**
     * Procesa el formulario de EDICIÓN del perfil del estudiante.
     * (Versión actualizada con multi-select de universidades)
     */
    @PostMapping("/edit-profile")
    @Transactional
    public String processEditProfileForm(
            @Valid @ModelAttribute("profileDTO") StudentProfileDTO profileDTO,
            BindingResult bindingResult,
            @RequestParam("fotoPerfil") MultipartFile fotoPerfil,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        Usuario usuarioBD = usuarioRepository.findByIdWithFullProfile(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PerfilEstudiante perfil = usuarioBD.getPerfilEstudiante();
        if (perfil == null) {
            perfil = new PerfilEstudiante();
            perfil.setUsuario(usuarioBD);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("profileDTO", profileDTO);
            // ¡Importante! Recargar la lista de universidades si hay un error
            model.addAttribute("allUniversidades", universidadRepository.findAll());
            return "student/edit-profile";
        }

        try {
            // 1. Actualizar Usuario y Perfil (igual que antes)
            usuarioBD.setNombre(profileDTO.getNombre());
            usuarioBD.setApellido(profileDTO.getApellido());
            usuarioBD.setTelefono(profileDTO.getTelefono());
            usuarioBD.setDui(profileDTO.getDui());

            perfil.setCarrera(profileDTO.getCarrera());
            perfil.setAnioCarrera(profileDTO.getAnioCarrera());
            perfil.setDescripcion(profileDTO.getDescripcion());
            perfil.setPortafolioUrl(profileDTO.getPortafolioUrl());

            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                perfil.setFotoPerfil(fotoPerfil.getBytes());
            }

            // 2. Actualizar Habilidades (igual que antes)
            usuarioBD.getHabilidades().clear();
            if (profileDTO.getHabilidades() != null && !profileDTO.getHabilidades().isEmpty()) {
                String[] nombresHabilidades = profileDTO.getHabilidades().split(",");
                for (String nombreHab : nombresHabilidades) {
                    String nombreTrimmed = nombreHab.trim();
                    if (nombreTrimmed.isEmpty()) continue;

                    Habilidad hab = habilidadRepository.findByTitulo(nombreTrimmed)
                            .orElseGet(() -> {
                                Habilidad newHab = new Habilidad();
                                newHab.setTitulo(nombreTrimmed);
                                return habilidadRepository.save(newHab);
                            });
                    usuarioBD.getHabilidades().add(hab);
                }
            }

            // --- INICIO DE LA SOLUCIÓN ---
            // 3. Actualizar Universidades (lógica de ManyToMany con IDs)
            usuarioBD.getUniversidades().clear(); // Limpiar las antiguas
            if (profileDTO.getUniversidades() != null && !profileDTO.getUniversidades().isEmpty()) {
                // Buscar todas las universidades por sus IDs
                List<Universidad> selectedUniversidades = universidadRepository.findAllById(profileDTO.getUniversidades());
                // Asignar el nuevo Set
                usuarioBD.setUniversidades(new HashSet<>(selectedUniversidades));
            }
            // --- FIN DE LA SOLUCIÓN ---

            // 4. Guardar (igual que antes)
            perfilEstudianteRepository.save(perfil);
            usuarioRepository.save(usuarioBD);

            redirectAttributes.addFlashAttribute("successMessage", "¡Perfil actualizado exitosamente!");
            return "redirect:/student/profile";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
            // Recargar datos para el formulario si hay un error
            model.addAttribute("profileDTO", profileDTO);
            model.addAttribute("allUniversidades", universidadRepository.findAll());
            return "student/edit-profile";
        }
    }
    /**
     * Muestra el formulario de cambiar contraseña para el estudiante.
     */
    @GetMapping("/change-password")
    public String showStudentChangePasswordForm(Model model) {
        model.addAttribute("passwordDTO", new PasswordChangeDTO());
        return "student/change-password";
    }
    /**
     * Procesa el formulario de cambio de contraseña del estudiante.
     */
    @PostMapping("/change-password")
    public String processStudentChangePassword(
            @Valid @ModelAttribute("passwordDTO") PasswordChangeDTO passwordDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        // 1. Validaciones
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error en los datos del formulario.");
            return "redirect:/student/change-password";
        }

        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "La nueva contraseña y la confirmación no coinciden.");
            return "redirect:/student/change-password";
        }

        Usuario usuario = userDetails.getUsuario();
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), usuario.getContrasena())) {
            redirectAttributes.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
            return "redirect:/student/change-password";
        }

        if (passwordDTO.getNewPassword().length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "redirect:/student/change-password";
        }

        try {
            // 2. Guardar la nueva contraseña
            // Volvemos a cargar al usuario para asegurarnos de que está adjunto
            Usuario usuarioBD = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            usuarioBD.setContrasena(passwordEncoder.encode(passwordDTO.getNewPassword()));
            usuarioRepository.save(usuarioBD);

            redirectAttributes.addFlashAttribute("successMessage", "¡Contraseña actualizada exitosamente!");
            return "redirect:/student/change-password";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la contraseña: " + e.getMessage());
            return "redirect:/student/change-password";
        }
    }

    /**
     * Muestra la página de chat/mensajería del estudiante.
     * (Por ahora, es una página estática).
     */
    @GetMapping("/chat")
    public String showChat(Model model) {
        // En un futuro, aquí cargarías la lista de contactos
        // model.addAttribute("listaDeConversaciones", ...);

        return "student/chat"; // Apunta a student/chat.html
    }
}