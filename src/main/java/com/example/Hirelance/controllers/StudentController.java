package com.example.Hirelance.controllers;

import com.example.Hirelance.config.CustomUserDetails;
import com.example.Hirelance.dto.PostulacionDTO;
import com.example.Hirelance.models.*;
import com.example.Hirelance.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import com.example.Hirelance.dto.StudentProfileDTO; // ¡NUEVO!

import java.util.stream.Collectors; // ¡NUEVO!

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.example.Hirelance.dto.PasswordChangeDTO;
import org.springframework.security.crypto.password.PasswordEncoder; // ¡NUEVO!

import java.util.stream.Collectors; // (Esta ya debería estar)

import java.time.LocalDateTime;

import java.util.Optional;
import com.example.Hirelance.models.Postulacion.EstadoPostulacion;

import com.example.Hirelance.dto.ReporteDTO;
import com.example.Hirelance.models.Reporte;
import com.example.Hirelance.models.Valoracion;

import java.util.Base64; // Asegúrate de que esta importación exista

import com.example.Hirelance.models.Proyecto.EstadoProyecto;
import com.example.Hirelance.models.Valoracion;
import com.example.Hirelance.dto.ValoracionDTO;


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

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ReporteRepository reporteRepository; // ¡Inyecta esto!

    @Autowired
    private ValoracionRepository valoracionRepository;
    @Autowired
    private com.example.Hirelance.service.NotificacionService notificacionService;

    @Autowired
    private com.example.Hirelance.services.PdfService pdfService; // Inyectar servicio



    private Map<String, List<String>> getDepartamentosMunicipios() {
        Map<String, List<String>> data = new TreeMap<>();
        data.put("Ahuachapán", List.of("Ahuachapán", "Apaneca", "Atiquizaya", "Concepción de Ataco", "El Refugio", "Guaymango", "Jujutla", "San Francisco Menéndez", "San Lorenzo", "San Pedro Puxtla", "Tacuba", "Turín"));
        data.put("Cabañas", List.of("Cinquera", "Dolores", "Guacotecti", "Ilobasco", "Jutiapa", "San Isidro", "Sensuntepeque", "Tejutepeque", "Victoria"));
        data.put("Chalatenango", List.of("Agua Caliente", "Arcatao", "Azacualpa", "Chalatenango", "Citalá", "Comalapa", "Concepción Quezaltepeque", "Dulce Nombre de María", "El Carrizal", "El Paraíso", "La Laguna", "La Palma", "La Reina", "Las Vueltas", "Nombre de Jesús", "Nueva Concepción", "Nueva Trinidad", "Ojos de Agua", "Potonico", "San Antonio de la Cruz", "San Antonio Los Ranchos", "San Fernando", "San Francisco Lempa", "San Francisco Morazán", "San Ignacio", "San Isidro Labrador", "San José Cancasque", "San José Las Flores", "San Luis del Carmen", "San Miguel de Mercedes", "San Rafael", "Santa Rita", "Tejutla"));
        data.put("Cuscatlán", List.of("Candelaria", "Cojutepeque", "El Carmen", "El Rosario", "Monte San Juan", "Oratorio de Concepción", "San Bartolomé Perulapía", "San Cristóbal", "San José Guayabal", "San Pedro Perulapán", "San Rafael Cedros", "San Ramón", "Santa Cruz Analquito", "Santa Cruz Michapa", "Suchitoto", "Tenancingo"));
        data.put("La Libertad", List.of("Antiguo Cuscatlán", "Chiltiupán", "Ciudad Arce", "Colón", "Comasagua", "Huizúcar", "Jayaque", "Jicalapa", "La Libertad", "Santa Tecla", "Nuevo Cuscatlán", "Quezaltepeque", "Sacacoyo", "San José Villanueva", "San Juan Opico", "San Matías", "San Pablo Tacachico", "Talnique", "Tamanique", "Teotepeque", "Tepecoyo", "Zaragoza"));
        data.put("La Paz", List.of("Cuyultitán", "El Rosario", "Jerusalén", "Mercedes La Ceiba", "Olocuilta", "Paraíso de Osorio", "San Antonio Masahuat", "San Emigdio", "San Francisco Chinameca", "San Juan Nonualco", "San Juan Talpa", "San Juan Tepezontes", "San Luis La Herradura", "San Luis Talpa", "San Miguel Tepezontes", "San Pedro Masahuat", "San Pedro Nonualco", "San Rafael Obrajuelo", "Santa María Ostuma", "Santiago Nonualco", "Tapalhuaca", "Zacatecoluca"));
        data.put("La Unión", List.of("Anamorós", "Bolívar", "Concepción de Oriente", "Conchagua", "El Carmen", "El Sauce", "Intipucá", "La Unión", "Lislique", "Meanguera del Golfo", "Nueva Esparta", "Pasaquina", "Polorós", "San Alejo", "San José", "Santa Rosa de Lima", "Yayantique", "Yucuaiquín"));
        data.put("Morazán", List.of("Arambala", "Cacaopera", "Chilanga", "Corinto", "Delicias de Concepción", "El Divisadero", "El Rosario", "Gualococti", "Guatajiagua", "Joateca", "Jocoaitique", "Jocoro", "Lolotiquillo", "Meanguera", "Osicala", "Perquín", "San Carlos", "San Fernando", "San Francisco Gotera", "San Isidro", "San Simón", "Sensembra", "Sociedad", "Torola", "Yamabal", "Yoloaiquín"));
        data.put("San Miguel", List.of("Carolina", "Chapeltique", "Chinameca", "Chirilagua", "Ciudad Barrios", "Comacarán", "El Tránsito", "Lolotique", "Moncagua", "Nueva Guadalupe", "Nuevo Edén de San Juan", "Quelepa", "San Antonio", "San Gerardo", "San Jorge", "San Luis de la Reina", "San Miguel", "San Rafael Oriente", "Sesori", "Uluazapa"));
        data.put("San Salvador", List.of("Aguilares", "Apopa", "Ayutuxtepeque", "Cuscatancingo", "Delgado", "El Paisnal", "Guazapa", "Ilopango", "Mejicanos", "Nejapa", "Panchimalco", "Rosario de Mora", "San Marcos", "San Martín", "San Salvador", "Santiago Texacuangos", "Santo Tomás", "Soyapango", "Tonacatepeque"));
        data.put("San Vicente", List.of("Apastepeque", "Guadalupe", "San Cayetano Istepeque", "San Esteban Catarina", "San Ildefonso", "San Lorenzo", "San Sebastián", "San Vicente", "Santa Clara", "Santo Domingo", "Tecoluca", "Tepetitán", "Verapaz"));
        data.put("Santa Ana", List.of("Candelaria de la Frontera", "Chalchuapa", "Coatepeque", "El Congo", "El Porvenir", "Masahuat", "Metapán", "San Antonio Pajonal", "San Sebastián Salitrillo", "Santa Ana", "Santa Rosa Guachipilín", "Santiago de la Frontera", "Texistepeque"));
        data.put("Sonsonate", List.of("Acajutla", "Armenia", "Caluco", "Cuisnahuat", "Izalco", "Juayúa", "Nahuizalco", "Nahulingo", "Salcoatitán", "San Antonio del Monte", "San Julián", "Santa Catarina Masahuat", "Santa Isabel Ishuatán", "Santo Domingo de Guzmán", "Sonsonate", "Sonzacate"));
        data.put("Usulután", List.of("Alegría", "Berlín", "California", "Concepción Batres", "El Triunfo", "Ereguayquín", "Estanzuelas", "Jiquilisco", "Jucuapa", "Jucuarán", "Mercedes Umaña", "Nueva Granada", "Ozatlán", "Puerto El Triunfo", "San Agustín", "San Buenaventura", "San Dionisio", "San Francisco Javier", "San Jorge", "Santa Elena", "Santa María", "Santiago de María", "Tecapán", "Usulután"));
        return data;
    }
    /**
     * Muestra el dashboard principal del estudiante con estadísticas Y feed.
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("nombreUsuario", usuario.getNombre());

        // --- INICIO DE LA LÓGICA DEL DASHBOARD ---

        // 1. Buscar todas las postulaciones del estudiante
        // ¡ACTUALIZADO! Usamos el método que trae el Proyecto para el feed
        List<Postulacion> misPostulaciones = postulacionRepository
                .findAllByEstudianteIdWithProyecto(usuario.getIdUsuario()); //

        // 2. Calcular Estadísticas (igual que antes)
        long totalPostulaciones = misPostulaciones.size();

        long totalAceptadas = misPostulaciones.stream()
                .filter(p -> p.getEstado() == Postulacion.EstadoPostulacion.aceptada)
                .count();

        long totalPendientes = misPostulaciones.stream()
                .filter(p -> p.getEstado() == Postulacion.EstadoPostulacion.pendiente)
                .count();

        model.addAttribute("totalPostulaciones", totalPostulaciones);
        model.addAttribute("totalAceptadas", totalAceptadas);
        model.addAttribute("totalPendientes", totalPendientes);

        // --- ¡NUEVO! FEED DE ACTIVIDAD ---
        // 3. Obtener las 5 postulaciones más recientes (ya están ordenadas por fecha)
        List<Postulacion> postulacionesRecientes = misPostulaciones.stream()
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("postulacionesRecientes", postulacionesRecientes);
        // --- FIN DE LA NUEVA LÓGICA ---

        return "student/dashboard";
    }

    /**
     * Muestra la lista completa de postulaciones del estudiante.
     * ¡ACTUALIZADO con filtros de búsqueda y estado!
     */
    @GetMapping("/my-applications")
    public String showMyApplications(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false, defaultValue = "all") String estado,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        List<Postulacion> misPostulaciones;

        // Convertir el string 'estado' al tipo Enum
        Postulacion.EstadoPostulacion estadoEnum = null;
        if (!estado.equals("all")) {
            try {
                estadoEnum = Postulacion.EstadoPostulacion.valueOf(estado);
            } catch (IllegalArgumentException e) {
                // Manejar estado inválido, por defecto 'all'
                estado = "all";
            }
        }

        // Usar el nuevo método del repositorio
        misPostulaciones = postulacionRepository.findByEstudianteAndFilters(
                usuario.getIdUsuario(),
                busqueda,
                estado,
                estadoEnum
        );



        // Enviar los filtros de vuelta a la vista para rellenar el formulario
        model.addAttribute("postulaciones", misPostulaciones);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estado", estado);

        return "student/my-applications";
    }
    /**
     * MUESTRA el formulario para editar una postulación.
     * URL: /student/application/{id}/edit
     */
    @GetMapping("/application/{id}/edit")
    public String showEditApplicationForm(
            @PathVariable("id") Integer idPostulacion,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario estudiante = userDetails.getUsuario();

        // 1. Buscar la postulación y asegurarse de que le pertenece al estudiante
        Optional<Postulacion> optPostulacion = postulacionRepository
                .findByIdPostulacionAndEstudianteIdUsuario(idPostulacion, estudiante.getIdUsuario());

        if (optPostulacion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Postulación no encontrada.");
            return "redirect:/student/my-applications";
        }

        Postulacion postulacion = optPostulacion.get();

        // 2. Solo se puede editar si está 'pendiente'
        if (postulacion.getEstado() != EstadoPostulacion.pendiente) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes editar una postulación que ya ha sido " + postulacion.getEstado().name() + ".");
            return "redirect:/student/my-applications";
        }

        PostulacionDTO dto = new PostulacionDTO();

        // 3. Crear el DTO y poblarlo con los datos actuales
        dto.setPropuesta(postulacion.getPropuesta());
        dto.setMontoOfertado(postulacion.getMontoOfertado());
        dto.setTiempoEstimado(postulacion.getTiempoEstimado()); // Usando 'getPresupuestoPropuesto' de tu Postulacion.java

        // 4. Enviar datos a la vista
        model.addAttribute("postulacionDTO", dto);
        model.addAttribute("postulacion", postulacion);
        model.addAttribute("proyecto", postulacion.getProyecto());

        return "student/edit-application"; // Apunta a la NUEVA plantilla
    }

    /**
     * PROCESA el formulario de edición de una postulación.
     * URL: /student/application/{id}/edit
     */
    @PostMapping("/application/{id}/edit")
    @Transactional
    public String processEditApplicationForm(
            @PathVariable("id") Integer idPostulacion,
            @Valid @ModelAttribute("postulacionDTO") PostulacionDTO postulacionDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        Usuario estudiante = userDetails.getUsuario();

        // 1. Buscar la postulación y verificar permisos
        Optional<Postulacion> optPostulacion = postulacionRepository
                .findByIdPostulacionAndEstudianteIdUsuario(idPostulacion, estudiante.getIdUsuario());

        if (optPostulacion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Postulación no encontrada.");
            return "redirect:/student/my-applications";
        }

        Postulacion postulacion = optPostulacion.get();

        // 2. Verificar estado 'pendiente'
        if (postulacion.getEstado() != EstadoPostulacion.pendiente) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes editar esta postulación.");
            return "redirect:/student/my-applications";
        }

        // 3. Validar errores del formulario
        if (bindingResult.hasErrors()) {
            // Si hay errores, recargar la página de edición
            model.addAttribute("postulacionDTO", postulacionDTO);
            model.addAttribute("postulacion", postulacion);
            model.addAttribute("proyecto", postulacion.getProyecto());
            return "student/edit-application";
        }

        // 4. Guardar los cambios
        try {
            postulacion.setPropuesta(postulacionDTO.getPropuesta());
            postulacion.setMontoOfertado(postulacionDTO.getMontoOfertado());
            postulacion.setTiempoEstimado(postulacionDTO.getTiempoEstimado());// Actualizar la fecha

            postulacionRepository.save(postulacion);

            redirectAttributes.addFlashAttribute("successMessage", "¡Postulación actualizada con éxito!");
            return "redirect:/student/my-applications";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la postulación: " + e.getMessage());
            return "redirect:/student/application/" + idPostulacion + "/edit";
        }
    }
    /**
     * ELIMINA (Retira) una postulación.
     * URL: /student/application/{id}/delete
     */
    @PostMapping("/application/{id}/delete")
    @Transactional
    public String deleteApplication(
            @PathVariable("id") Integer idPostulacion,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario estudiante = userDetails.getUsuario();

        // 1. Buscar la postulación y verificar permisos
        Optional<Postulacion> optPostulacion = postulacionRepository
                .findByIdPostulacionAndEstudianteIdUsuario(idPostulacion, estudiante.getIdUsuario());

        if (optPostulacion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Postulación no encontrada.");
            return "redirect:/student/my-applications";
        }

        Postulacion postulacion = optPostulacion.get();

        // 2. Solo se puede borrar si está 'pendiente'
        if (postulacion.getEstado() != EstadoPostulacion.pendiente) {
            redirectAttributes.addFlashAttribute("errorMessage", "No puedes retirar una postulación que ya no está pendiente.");
            return "redirect:/student/my-applications";
        }

        // 3. Borrar la postulación
        try {
            postulacionRepository.delete(postulacion);
            redirectAttributes.addFlashAttribute("successMessage", "Postulación retirada con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al retirar la postulación.");
        }

        return "redirect:/student/my-applications";
    }

    /**
     * MUESTRA los detalles de una postulación específica.
     * URL: /student/application/{id}/details
     */
    @GetMapping("/application/{id}/details")
    public String showApplicationDetails(
            @PathVariable("id") Integer idPostulacion,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Usuario estudiante = userDetails.getUsuario();

        // 1. Buscar la postulación
        Optional<Postulacion> optPostulacion = postulacionRepository
                .findByIdPostulacionAndEstudianteIdUsuario(idPostulacion, estudiante.getIdUsuario());

        if (optPostulacion.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Postulación no encontrada.");
            return "redirect:/student/my-applications";
        }
        Postulacion postulacion = optPostulacion.get();

        // --- ¡NUEVA LÓGICA PARA VALORACIÓN! ---
        boolean haValorado = false;
        boolean puedeValorar = false;

        // Solo se puede valorar si el proyecto está 'finalizado' y la postulación fue 'aceptada'
        if (postulacion.getProyecto().getEstado() == EstadoProyecto.finalizado &&
                postulacion.getEstado() == EstadoPostulacion.aceptada) {

            puedeValorar = true;

            // Comprobar si el estudiante (emisor) ya valoró al contratista (receptor)
            haValorado = valoracionRepository
                    .existsByEmisorIdUsuarioAndReceptorIdUsuarioAndProyectoIdProyecto(
                            estudiante.getIdUsuario(),
                            postulacion.getProyecto().getContratista().getIdUsuario(),
                            postulacion.getProyecto().getIdProyecto()
                    );
        }
        // --- FIN NUEVA LÓGICA ---

        // 2. Enviar datos a la vista
        model.addAttribute("postulacion", postulacion);
        model.addAttribute("proyecto", postulacion.getProyecto()); // Por si acaso

        // Datos para la valoración
        model.addAttribute("valoracionDTO", new ValoracionDTO());
        model.addAttribute("puedeValorar", puedeValorar);
        model.addAttribute("haValorado", haValorado);

        return "student/application-details";
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


    /**
     * Muestra la página INTERNA para buscar proyectos.
     */
    @GetMapping("/search-projects")
    public String searchProjects(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda,
            Model model) {

        // Cargar categorías para el filtro
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriaSeleccionada", categoria != null ? categoria : "all");
        model.addAttribute("busqueda", busqueda);

        // Lógica de filtrado (usando los métodos del repositorio que ya existen)
        List<Proyecto> proyectos;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            proyectos = proyectoRepository.buscarPorTituloODescripcion(busqueda);
        } else if (categoria != null && !categoria.equals("all")) {
            // Asumimos que la corrección de @ManyToOne está aplicada
            proyectos = proyectoRepository.buscarPorCategoria(categoria);
        } else {
            proyectos = proyectoRepository.findAllWithContratista();
        }

        model.addAttribute("proyectos", proyectos);

        return "student/search-projects"; // Apunta a la nueva vista
    }

    /**
     * Muestra la página INTERNA para buscar contratistas.
     */
    @GetMapping("/search-contractors")
    public String searchContractors(
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String busqueda,
            Model model) {

        // Cargar ubicaciones (departamentos) para el filtro
        Map<String, List<String>> locationData = getDepartamentosMunicipios();
        model.addAttribute("departamentos", locationData.keySet());
        model.addAttribute("ubicacionSeleccionada", ubicacion != null ? ubicacion : "all");
        model.addAttribute("busqueda", busqueda);

        // Lógica de filtrado
        List<Usuario> contratistas;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            contratistas = usuarioRepository.findContratistasWithProfileBySearch(busqueda);
        } else if (ubicacion != null && !ubicacion.equals("all")) {
            contratistas = usuarioRepository.findContratistasByUbicacion(ubicacion);
        } else {
            contratistas = usuarioRepository.findAllContratistasWithProfile();
        }

        // Convertir imágenes a Base64 (igual que en PublicController)
        List<Map<String, Object>> contratistasVM = new ArrayList<>();
        for (Usuario u : contratistas) {
            Map<String, Object> vm = new HashMap<>();
            vm.put("contratista", u);
            String base64Img = null;
            if (u.getPerfilContratista() != null && u.getPerfilContratista().getLogoEmpresa() != null) {
                base64Img = Base64.getEncoder().encodeToString(u.getPerfilContratista().getLogoEmpresa());
            }
            vm.put("base64Image", base64Img);
            contratistasVM.add(vm);
        }

        model.addAttribute("contratistas", contratistasVM);

        return "student/search-contractors"; // Apunta a la nueva vista
    }
    /**
     * MUESTRA la página de detalles del proyecto y el formulario para postularse.
     * ¡ACTUALIZADO para manejar la postulación existente y el formulario de valoración!
     */
    @GetMapping("/project/{id}/apply")
    public String showApplyToProjectForm(
            @PathVariable("id") Integer idProyecto, // <-- Integer
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Usuario estudiante = userDetails.getUsuario();

        // 1. Buscar el proyecto
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 2. ¡CAMBIO! Buscar la postulación existente en lugar de solo comprobar si existe
        Optional<Postulacion> optPostulacion = postulacionRepository
                .findByEstudianteIdUsuarioAndProyectoIdProyecto(estudiante.getIdUsuario(), idProyecto);

        boolean yaPostulado = optPostulacion.isPresent();
        Postulacion postulacion = optPostulacion.orElse(null); // Pasa la postulación si existe
        boolean haValorado = false;

        // 3. Comprobar si el estudiante ya ha valorado este proyecto
// 3. Comprobar si el estudiante ya ha valorado este proyecto
        if (yaPostulado && proyecto.getEstado() == EstadoProyecto.finalizado) {
            haValorado = valoracionRepository
                    .existsByEmisorIdUsuarioAndReceptorIdUsuarioAndProyectoIdProyecto(
                            estudiante.getIdUsuario(), // Emisor (estudiante)
                            proyecto.getContratista().getIdUsuario(), // Receptor (contratista)
                            idProyecto // Proyecto
                    );
        }

        // 4. Obtener el contratista (dueño del proyecto)
        Usuario contratista = proyecto.getContratista();

        // 5. Preparar el logo del contratista
        String base64Logo = null;
        if (contratista != null && contratista.getPerfilContratista() != null && contratista.getPerfilContratista().getLogoEmpresa() != null) {
            base64Logo = Base64.getEncoder().encodeToString(contratista.getPerfilContratista().getLogoEmpresa());
        }

        // 6. Enviar todos los datos a la vista
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("postulacionDTO", new PostulacionDTO()); // Para el formulario de *postularse*
        model.addAttribute("yaPostulado", yaPostulado);
        model.addAttribute("postulacion", postulacion); // ¡NUEVO! Pasa la postulación (o null)
        model.addAttribute("contratista", contratista);
        model.addAttribute("base64Logo", base64Logo);
        model.addAttribute("valoracionDTO", new ValoracionDTO()); // ¡NUEVO! Para el formulario de *valoración*
        model.addAttribute("haValorado", haValorado); // ¡NUEVO! Para saber si mostrar el form o no
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("yaPostulado", yaPostulado);
        model.addAttribute("postulacion", postulacion);
        model.addAttribute("contratista", contratista);
        model.addAttribute("base64Logo", base64Logo);
        model.addAttribute("valoracionDTO", new ValoracionDTO());
        model.addAttribute("haValorado", haValorado);

        return "student/apply-to-project"; // Apunta a la vista de detalles
    }

// ===============================================
// ¡NUEVO MÉTODO GET PARA MOSTRAR EL FORMULARIO!
// ===============================================
/**
 * MUESTRA el formulario de postulación dedicado.
 */
@GetMapping("/project/{id}/application")
public String showApplicationForm(
        @PathVariable("id") Integer idProyecto,
        Model model,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        RedirectAttributes redirectAttributes) {

    Usuario estudiante = userDetails.getUsuario();
    Proyecto proyecto = proyectoRepository.findById(idProyecto)
            .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

    // Verificar si ya se postuló
    boolean yaPostulado = postulacionRepository
            .existsByEstudianteIdUsuarioAndProyectoIdProyecto(estudiante.getIdUsuario(), idProyecto);

    if (yaPostulado) {
        redirectAttributes.addFlashAttribute("errorMessage", "¡Ya te has postulado a este proyecto!");
        return "redirect:/student/project/" + idProyecto + "/apply";
    }

    // Verificar si el proyecto sigue 'publicado'
    if (proyecto.getEstado() != EstadoProyecto.publicado) {
        redirectAttributes.addFlashAttribute("errorMessage", "Este proyecto ya no acepta postulaciones.");
        return "redirect:/student/project/" + idProyecto + "/apply";
    }

    model.addAttribute("proyecto", proyecto);
    model.addAttribute("postulacionDTO", new PostulacionDTO()); // DTO para el nuevo formulario

    return "student/application-form"; // Apunta a la NUEVA plantilla de formulario
}


    /**
     * PROCESA el formulario de postulación del estudiante.
     * ¡ACTUALIZADO para usar los nombres de campo correctos de la DB!
     */
    @PostMapping("/project/{id}/apply")
    @Transactional
    public String processApplyToProject(
            @PathVariable("id") Integer idProyecto, // <-- Integer
            @Valid @ModelAttribute("postulacionDTO") PostulacionDTO postulacionDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        Usuario estudiante = userDetails.getUsuario();
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 1. Validar duplicados
        boolean yaPostulado = postulacionRepository
                .existsByEstudianteIdUsuarioAndProyectoIdProyecto(estudiante.getIdUsuario(), idProyecto);

        if (yaPostulado) {
            redirectAttributes.addFlashAttribute("errorMessage", "¡Ya te has postulado a este proyecto!");
            return "redirect:/student/project/" + idProyecto + "/apply";
        }

        // 2. Validar errores del formulario (DTO)
        if (bindingResult.hasErrors()) {
            // Si hay errores, debemos recargar la página del formulario
            model.addAttribute("proyecto", proyecto);
            // El 'postulacionDTO' con errores se añade automáticamente
            return "student/application-form";
        }

        // 3. Si todo está bien, creamos la postulación
        try {
            Postulacion nuevaPostulacion = new Postulacion();

            // Usamos los nuevos nombres (que ahora sí existen en Postulacion.java)
            nuevaPostulacion.setPropuesta(postulacionDTO.getPropuesta());
            nuevaPostulacion.setMontoOfertado(postulacionDTO.getMontoOfertado());
            nuevaPostulacion.setTiempoEstimado(postulacionDTO.getTiempoEstimado());

            // CORRECCIÓN: Usar LocalDateTime (no Date)
            nuevaPostulacion.setFechaPostulacion(java.time.LocalDateTime.now());

            nuevaPostulacion.setEstado(Postulacion.EstadoPostulacion.pendiente);
            nuevaPostulacion.setEstudiante(userDetails.getUsuario());
            nuevaPostulacion.setProyecto(proyecto);

            postulacionRepository.save(nuevaPostulacion);

            // --- NOTIFICAR AL CONTRATISTA ---
            String mensajeNotif = "Nueva postulación de " + estudiante.getNombre() + " en: " + proyecto.getTitulo();

// URL: Lleva al contratista a ver el detalle de ESTA postulación
            String urlDestino = "/client/application/" + nuevaPostulacion.getIdPostulacion() + "/details";

            notificacionService.crearNotificacion(
                    proyecto.getContratista(),
                    mensajeNotif,
                    Notificacion.TipoNotificacion.proyecto,
                    urlDestino // <--- Pasamos la URL
            );

            redirectAttributes.addFlashAttribute("successMessage", "¡Postulación enviada con éxito!");
            return "redirect:/student/my-applications"; // Enviar a "Mis Postulaciones"

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la postulación.");
            return "redirect:/student/project/" + idProyecto + "/application";
        }
    }

    // ===============================================
    // ¡NUEVO MÉTODO PARA PROCESAR LA VALORACIÓN!
    // ===============================================
    /**
     * Procesa el envío del formulario de valoración.
     */
    @PostMapping("/project/{id}/review")
    @Transactional
    public String processReviewForm(
            @PathVariable("id") Integer idProyecto,
            @Valid @ModelAttribute("valoracionDTO") ValoracionDTO valoracionDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes,
            Model model) {

        Usuario estudiante = userDetails.getUsuario();
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 1. Validar errores del formulario
        if (bindingResult.hasErrors()) {
            // Si hay errores, debemos recargar la página completa
            // (Reutilizamos la lógica del GET)
            Postulacion postulacion = postulacionRepository
                    .findByEstudianteIdUsuarioAndProyectoIdProyecto(estudiante.getIdUsuario(), idProyecto)
                    .orElse(null);

            Usuario contratista = proyecto.getContratista();
            String base64Logo = null;
            if (contratista != null && contratista.getPerfilContratista() != null && contratista.getPerfilContratista().getLogoEmpresa() != null) {
                base64Logo = Base64.getEncoder().encodeToString(contratista.getPerfilContratista().getLogoEmpresa());
            }

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("postulacionDTO", new PostulacionDTO()); // Form de aplicar
            model.addAttribute("yaPostulado", true);
            model.addAttribute("postulacion", postulacion);
            model.addAttribute("contratista", contratista);
            model.addAttribute("base64Logo", base64Logo);
            model.addAttribute("haValorado", false);
            // El 'valoracionDTO' con errores se añade automáticamente por Spring

            return "student/apply-to-project"; // Devuelve la vista con los errores
        }

        try {
            // 2. Verificar que no haya valorado ya
            boolean haValorado = valoracionRepository
                    .existsByEmisorIdUsuarioAndReceptorIdUsuarioAndProyectoIdProyecto(
                            estudiante.getIdUsuario(), // Emisor (estudiante)
                            proyecto.getContratista().getIdUsuario(), // Receptor (contratista)
                            idProyecto // Proyecto
                    );

            if (haValorado) {
                redirectAttributes.addFlashAttribute("errorMessage", "Ya has enviado una valoración para este proyecto.");
                return "redirect:/student/project/" + idProyecto + "/apply";
            }

            // 3. Crear y guardar la valoración
            Valoracion valoracion = new Valoracion();
            valoracion.setEmisor(estudiante); // El estudiante es el emisor
            valoracion.setReceptor(proyecto.getContratista()); // El contratista es el receptor
            valoracion.setProyecto(proyecto);
            valoracion.setCalificacion(valoracionDTO.getCalificacion());
            valoracion.setComentario(valoracionDTO.getComentario());

            valoracionRepository.save(valoracion);

            // --- NUEVO: NOTIFICAR AL CONTRATISTA ---
            String mensaje = "¡Has recibido una nueva valoración de 5 estrellas de " + estudiante.getNombre() + "!";
            // Opcional: Personalizar mensaje según estrellas:
            // String mensaje = "¡" + estudiante.getNombre() + " te ha calificado con " + valoracionDTO.getCalificacion() + " estrellas!";

            String urlDestino = "/client/profile"; // O "/client/my-projects"

            notificacionService.crearNotificacion(
                    proyecto.getContratista(), // Destinatario
                    mensaje,
                    Notificacion.TipoNotificacion.valoracion, // Asegúrate de tener este Enum o usa 'sistema'
                    urlDestino
            );

            redirectAttributes.addFlashAttribute("successMessage", "¡Gracias por tu valoración!");
            return "redirect:/student/project/" + idProyecto + "/apply";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la valoración: " + e.getMessage());
            return "redirect:/student/project/" + idProyecto + "/apply";
        }
    }

    /**
     * MUESTRA el perfil interno de un contratista al estudiante.
     * URL: /student/contractor-profile/{id}
     * ¡ACTUALIZADO para incluir Valoraciones!
     */
    @GetMapping("/contractor-profile/{id}")
    public String showContractorProfileInternal(
            @PathVariable("id") Integer idContratista,
            Model model) {

        // 1. Buscar al contratista
        Usuario contratista = usuarioRepository.findById(idContratista)
                .orElseThrow(() -> new RuntimeException("Contratista no encontrado"));

        // 2. Buscar el perfil (asumiendo que tiene uno)
        PerfilContratista perfil = contratista.getPerfilContratista();

        // 3. Buscar los proyectos de ese contratista
        List<Proyecto> proyectos = proyectoRepository.findByContratistaIdUsuarioOrderByFechaCreacionDesc(idContratista);

        // 4. Preparar el logo
        String base64Logo = null;
        if (perfil != null && perfil.getLogoEmpresa() != null) {
            base64Logo = Base64.getEncoder().encodeToString(perfil.getLogoEmpresa());
        }

        // --- ¡NUEVO PASO 5! ---
        // 5. Buscar las valoraciones (reviews)
        List<Valoracion> valoraciones = valoracionRepository.findByReceptorIdWithEmisorAndProyecto(idContratista);


        // 6. Enviar todo al modelo
        model.addAttribute("contratista", contratista);
        model.addAttribute("perfil", perfil);
        model.addAttribute("proyectos", proyectos);
        model.addAttribute("base64Logo", base64Logo);
        model.addAttribute("valoraciones", valoraciones); // <-- ¡AÑADE ESTO!

        // 7. Apuntar a la plantilla HTML
        return "student/contractor-profile";
    }

    /**
     * Muestra el formulario para enviar un reporte.
     */
    @GetMapping("/report")
    public String showReportForm(Model model) {
        model.addAttribute("reporteDTO", new ReporteDTO());
        return "student/report"; // Apunta a la nueva vista
    }
    /**
     * Procesa el envío del reporte.
     */
    @PostMapping("/report")
    @Transactional
    public String processReportForm(
            @Valid @ModelAttribute("reporteDTO") ReporteDTO reporteDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "student/report";
        }

        try {
            Usuario estudiante = userDetails.getUsuario();

            Reporte nuevoReporte = new Reporte();
            nuevoReporte.setUsuario(estudiante);
            nuevoReporte.setDescripcion(reporteDTO.getDescripcion());

            // Convertir el string del DTO al Enum del Modelo
            // Asumiendo que Reporte.java tiene un enum TipoReporte
            // Si da error, asegúrate de que los valores coinciden (bug, fraude, soporte, otro)
            nuevoReporte.setTipo(Reporte.TipoReporte.valueOf(reporteDTO.getTipo()));

            nuevoReporte.setEstado(Reporte.EstadoReporte.pendiente);
            nuevoReporte.setFechaReporte(java.time.LocalDateTime.now());

            reporteRepository.save(nuevoReporte);

            redirectAttributes.addFlashAttribute("successMessage", "¡Tu reporte ha sido enviado. Gracias por tu feedback!");
            return "redirect:/student/dashboard"; // O redirigir a donde prefieras

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar el reporte: " + e.getMessage());
            return "redirect:/student/report";
        }
    }

    /**
     * Muestra la lista de notificaciones del estudiante.
     */
    @GetMapping("/notifications")
    public String showNotifications(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();

        // Obtener notificaciones (usando el servicio que ya creamos)
        List<Notificacion> lista = notificacionService.obtenerMisNotificaciones(usuario.getIdUsuario());

        model.addAttribute("notificaciones", lista);

        // (Opcional) Marcar como leídas al entrar
        // lista.forEach(n -> notificacionService.marcarComoLeida(n.getIdNotificacion()));

        return "student/notifications"; // Apunta al nuevo HTML
    }
    @Autowired
    private ContratoRepository contratoRepository; // Inyectar repositorio

    @GetMapping("/contracts")
    public String showMyContracts(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario estudiante = userDetails.getUsuario();
        List<Contrato> contratos = contratoRepository.findByEstudianteIdUsuarioOrderByFechaInicioDesc(estudiante.getIdUsuario());
        model.addAttribute("contratos", contratos);
        return "student/my-contracts";
    }

    @GetMapping("/contract/{id}/details")
    public String viewContractDetails(@PathVariable("id") Integer contratoId,
                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                      Model model) {

        // 1. Buscar el contrato
        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        // 2. Seguridad: Verificar que el contrato pertenezca al estudiante logueado
        if(!contrato.getEstudiante().getIdUsuario().equals(userDetails.getUsuario().getIdUsuario())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver este contrato");
        }

        model.addAttribute("contrato", contrato);
        return "student/contract-details"; // Apunta al nuevo HTML
    }

    @GetMapping("/contract/{id}/pdf")
    public void downloadContractPdf(@PathVariable("id") Integer contratoId,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    HttpServletResponse response) throws IOException {

        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Seguridad
        if (!contrato.getEstudiante().getIdUsuario().equals(userDetails.getUsuario().getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        pdfService.exportarContratoPdf(response, contrato);
    }

}