package com.example.Hirelance.controllers;

import org.springframework.web.bind.annotation.RequestParam;

import com.example.Hirelance.models.*;
import com.example.Hirelance.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.Hirelance.dto.ProjectDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid; // (Usaremos @Valid para validación futura)

import java.util.*;

import org.springframework.web.bind.annotation.PathVariable; // ¡NUEVO!

import org.springframework.http.HttpStatus; // ¡NUEVO!
import org.springframework.web.server.ResponseStatusException; // ¡NUEVO!

import com.example.Hirelance.models.Postulacion;
import com.example.Hirelance.models.Postulacion.EstadoPostulacion; // ¡Importante!

import com.example.Hirelance.dto.ProfileDTO; // ¡NUEVO!

import org.springframework.web.multipart.MultipartFile; // ¡NUEVO!
import org.springframework.web.bind.annotation.RequestParam;
import com.example.Hirelance.models.Habilidad;
import com.example.Hirelance.dto.PasswordChangeDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Hirelance.config.CustomUserDetails;

import com.example.Hirelance.models.Proyecto.EstadoProyecto;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/client") // Todas las URLs de este controlador empezarán con /client
@PreAuthorize("hasAuthority('contratista')") // ¡IMPORTANTE! Solo permite acceso a usuarios con rol 'contratista'
public class ClientController {

    // Inyectamos los repositorios que necesitaremos
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private PerfilContratistaRepository perfilContratistaRepository;

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HabilidadRepository habilidadRepository;

    private Map<String, List<String>> getDepartamentosMunicipios() {
        Map<String, List<String>> data = new java.util.TreeMap<>();
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
     * Muestra el dashboard principal del contratista.
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        // 1. Añadir el nombre de usuario al modelo para un saludo
        model.addAttribute("nombreUsuario", usuario.getNombre());



        // 2. Obtener la lista completa de proyectos y postulaciones
        List<Proyecto> todosMisProyectos = proyectoRepository
                .findByContratistaIdUsuarioOrderByFechaCreacionDesc(usuario.getIdUsuario());

        List<Postulacion> todasMisPostulaciones = postulacionRepository
                .findAllByContratistaId(usuario.getIdUsuario());

        // 3. Calcular Estadísticas
        long totalProyectos = todosMisProyectos.size();
        long totalPostulaciones = todasMisPostulaciones.size();
        long postulacionesPendientes = todasMisPostulaciones.stream()
                .filter(p -> p.getEstado() == Postulacion.EstadoPostulacion.pendiente)
                .count();

        // 4. Obtener listas "recientes" (ej: los últimos 5)
        List<Proyecto> proyectosRecientes = todosMisProyectos.stream()
                .limit(5)
                .collect(Collectors.toList());

        List<Postulacion> postulacionesRecientes = todasMisPostulaciones.stream()
                .limit(5)
                .collect(Collectors.toList());

        // 5. Añadir todo al modelo
        model.addAttribute("totalProyectos", totalProyectos);
        model.addAttribute("totalPostulaciones", totalPostulaciones);
        model.addAttribute("postulacionesPendientes", postulacionesPendientes);
        model.addAttribute("proyectosRecientes", proyectosRecientes);
        model.addAttribute("postulacionesRecientes", postulacionesRecientes);

        return "client/dashboard"; // Apunta a dashboard.html
    }
    @GetMapping("/post-project")
    public String showPostProjectForm(Model model) {

        // 1. Cargar las categorías para el menú desplegable
        List<Categoria> categorias = categoriaRepository.findAll();

        // 2. Añadir las categorías y un DTO vacío al modelo
        model.addAttribute("categorias", categorias);
        model.addAttribute("projectDTO", new ProjectDTO()); // DTO que usará el formulario

        // 3. Devolver la ruta de la plantilla
        return "client/post-project"; // Apunta a client/post-project.html
    }
    /**
     * Procesa el envío del formulario para crear un nuevo proyecto.
     */
    @PostMapping("/post-project")
    public String processPostProjectForm(
            @Valid @ModelAttribute("projectDTO") ProjectDTO projectDTO,
            BindingResult bindingResult, // Para manejar errores de validación
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. Verificar si hay errores de validación
        if (bindingResult.hasErrors()) {
            // Si hay errores, recargar las categorías y volver a mostrar el formulario
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "client/post-project";
        }

        try {
            // 2. Crear una nueva entidad Proyecto
            Proyecto nuevoProyecto = new Proyecto();
            nuevoProyecto.setTitulo(projectDTO.getTitulo());
            nuevoProyecto.setDescripcion(projectDTO.getDescripcion());
            nuevoProyecto.setPresupuesto(projectDTO.getPresupuesto());

            // 3. Asignar al contratista logueado
            // ¡IMPORTANTE! Asigna el usuario de la sesión al proyecto
            Usuario usuario = userDetails.getUsuario();
            nuevoProyecto.setContratista(usuario);

            // 4. Asignar la categoría
            // Buscamos la categoría por el ID que vino del formulario
            Categoria categoria = categoriaRepository.findById(projectDTO.getIdCategoria())
                    .orElseThrow(() -> new Exception("Categoría no encontrada"));
            nuevoProyecto.setCategoria(categoria); //

            // 5. Convertir LocalDate a LocalDateTime (el PrePersist se encargará del resto)
            //
            if (projectDTO.getFechaLimite() != null) {
                nuevoProyecto.setFechaLimite(projectDTO.getFechaLimite().atStartOfDay());
            }

            // 6. Guardar el proyecto
            // El @PrePersist en Proyecto.java
            // se encargará de 'fechaCreacion' y 'estado'
            proyectoRepository.save(nuevoProyecto);

            // 7. Redirigir con mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage", "¡Tu proyecto ha sido publicado exitosamente!");

            // Redirigimos a la página "Mis Proyectos" (que crearemos después)
            return "redirect:/client/my-projects";

        } catch (Exception e) {
            // 8. Manejar errores
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error al publicar el proyecto: " + e.getMessage());
            model.addAttribute("categorias", categoriaRepository.findAll());
            return "client/post-project";
        }
    }

    /**
     * Muestra la lista de proyectos publicados por el contratista logueado.
     * ¡ACTUALIZADO con filtros de búsqueda, estado y presupuesto!
     */
    @GetMapping("/my-projects")
    public String showMyProjects(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false, defaultValue = "all") String estado,
            // --- NUEVOS PARÁMETROS ---
            @RequestParam(required = false) Double minBudget,
            @RequestParam(required = false) Double maxBudget,
            // --- FIN DE NUEVOS PARÁMETROS ---
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Usuario usuario = userDetails.getUsuario();
        List<Proyecto> misProyectos;

        // Convertir el string 'estado' al tipo Enum
        Proyecto.EstadoProyecto estadoEnum = null;
        if (!estado.equals("all")) {
            try {
                estadoEnum = Proyecto.EstadoProyecto.valueOf(estado);
            } catch (IllegalArgumentException e) {
                // Manejar estado inválido, por defecto 'all'
                estado = "all";
            }
        }

        // Usar el nuevo método del repositorio (actualizado)
        misProyectos = proyectoRepository.findByContratistaIdAndFilters(
                usuario.getIdUsuario(),
                busqueda,
                estado,
                estadoEnum,
                minBudget, // <-- Pasar nuevo parámetro
                maxBudget  // <-- Pasar nuevo parámetro
        );

        // Enviar los filtros de vuelta a la vista para rellenar el formulario
        model.addAttribute("proyectos", misProyectos);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estado", estado);
        model.addAttribute("minBudget", minBudget); // <-- Pasar nuevo parámetro
        model.addAttribute("maxBudget", maxBudget); // <-- Pasar nuevo parámetro

        return "client/my-projects";
    }
    /**
     * Muestra el formulario para EDITAR un proyecto existente.
     * URL: /client/project/{id}/edit
     */
    @GetMapping("/project/{id}/edit")
    public String showEditProjectForm(
            @PathVariable("id") Integer proyectoId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Verificar propiedad y obtener proyecto
            Proyecto proyecto = checkProjectOwnership(proyectoId, userDetails.getUsuario());

            // 2. Solo se pueden editar proyectos 'publicados'
            if (proyecto.getEstado() != EstadoProyecto.publicado) {
                redirectAttributes.addFlashAttribute("errorMessage", "No puedes editar un proyecto que ya está en progreso o finalizado.");
                return "redirect:/client/my-projects";
            }

            // 3. Poblar el DTO con los datos del proyecto
            ProjectDTO projectDTO = new ProjectDTO();
            // ¡LÍNEA ELIMINADA! No necesitamos setear el ID en el DTO.
            projectDTO.setTitulo(proyecto.getTitulo());
            projectDTO.setDescripcion(proyecto.getDescripcion());
            projectDTO.setPresupuesto(proyecto.getPresupuesto());
            projectDTO.setFechaLimite(proyecto.getFechaLimite() != null ? proyecto.getFechaLimite().toLocalDate() : null);
            projectDTO.setIdCategoria(proyecto.getCategoria().getIdCategoria());

            // 4. Cargar categorías y enviar todo a la vista
            model.addAttribute("projectDTO", projectDTO);
            model.addAttribute("categorias", categoriaRepository.findAll());
            model.addAttribute("pageTitle", "Editar Proyecto");
            model.addAttribute("formAction", "/client/project/" + proyectoId + "/edit");

            return "client/edit-project"; // Apunta a la NUEVA plantilla

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/client/my-projects";
        }
    }

    /**
     * Procesa el formulario de EDICIÓN de un proyecto.
     * URL: /client/project/{id}/edit
     */
    @PostMapping("/project/{id}/edit")
    @Transactional
    public String processEditProjectForm(
            @PathVariable("id") Integer proyectoId,
            @Valid @ModelAttribute("projectDTO") ProjectDTO projectDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Verificar propiedad y obtener proyecto
            Proyecto proyectoAEditar = checkProjectOwnership(proyectoId, userDetails.getUsuario());

            // 2. Solo se pueden editar proyectos 'publicados'
            if (proyectoAEditar.getEstado() != EstadoProyecto.publicado) {
                redirectAttributes.addFlashAttribute("errorMessage", "Este proyecto ya no se puede editar.");
                return "redirect:/client/my-projects";
            }

            // 3. Validar errores del formulario
            if (bindingResult.hasErrors()) {
                model.addAttribute("categorias", categoriaRepository.findAll());
                model.addAttribute("pageTitle", "Editar Proyecto");
                model.addAttribute("formAction", "/client/project/" + proyectoId + "/edit");
                return "client/edit-project";
            }

            // 4. Actualizar la entidad con los datos del DTO
            proyectoAEditar.setTitulo(projectDTO.getTitulo());
            proyectoAEditar.setDescripcion(projectDTO.getDescripcion());
            proyectoAEditar.setPresupuesto(projectDTO.getPresupuesto());

            if (projectDTO.getFechaLimite() != null) {
                proyectoAEditar.setFechaLimite(projectDTO.getFechaLimite().atStartOfDay());
            } else {
                proyectoAEditar.setFechaLimite(null);
            }

            Categoria categoria = categoriaRepository.findById(projectDTO.getIdCategoria())
                    .orElseThrow(() -> new Exception("Categoría no encontrada"));
            proyectoAEditar.setCategoria(categoria);

            // 5. Guardar cambios
            proyectoRepository.save(proyectoAEditar);

            redirectAttributes.addFlashAttribute("successMessage", "Proyecto actualizado con éxito.");
            return "redirect:/client/my-projects";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el proyecto: " + e.getMessage());
            return "redirect:/client/my-projects";
        }
    }

    /**
     * ELIMINA un proyecto.
     * URL: /client/project/{id}/delete
     */
    @PostMapping("/project/{id}/delete")
    @Transactional
    public String deleteProject(
            @PathVariable("id") Integer proyectoId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Verificar propiedad y obtener proyecto
            Proyecto proyectoAEliminar = checkProjectOwnership(proyectoId, userDetails.getUsuario());

            // 2. Solo se pueden eliminar proyectos 'publicados'
            if (proyectoAEliminar.getEstado() != EstadoProyecto.publicado) {
                redirectAttributes.addFlashAttribute("errorMessage", "No puedes eliminar un proyecto que está en progreso o finalizado.");
                return "redirect:/client/my-projects";
            }

            // 3. Eliminar el proyecto
            // (La BD debería borrar las postulaciones en cascada)
            proyectoRepository.delete(proyectoAEliminar);

            redirectAttributes.addFlashAttribute("successMessage", "Proyecto eliminado con éxito.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el proyecto: " + e.getMessage());
        }

        return "redirect:/client/my-projects";
    }

    /**
     * Método de utilidad para verificar si un proyecto pertenece al
     * contratista logueado. Lanza una excepción si no es así.
     */
    private Proyecto checkProjectOwnership(Integer proyectoId, Usuario contratista) throws Exception {
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        if (!proyecto.getContratista().getIdUsuario().equals(contratista.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: Este proyecto no te pertenece.");
        }
        return proyecto;
    }

    /**
     * Muestra las postulaciones para un proyecto específico.
     */
    @GetMapping("/project/{id}/applications")
    public String showProjectApplications(
            @PathVariable("id") Integer proyectoId, // Obtiene el ID del proyecto desde la URL
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        // 1. Verificar que el proyecto existe y pertenece al contratista
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));
        Usuario usuario = userDetails.getUsuario();

        if (!proyecto.getContratista().getIdUsuario().equals(usuario.getIdUsuario())) {
            // ¡Seguridad! No dejar que un contratista vea las postulaciones de otro.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
        }

        // 2. Buscar las postulaciones usando el nuevo método del repositorio
        List<Postulacion> postulaciones = postulacionRepository
                .findAllByProyectoIdWithEstudiante(proyectoId);

        // 3. Pasar el proyecto y la lista de postulaciones a la vista
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("postulaciones", postulaciones);

        return "client/project-applications"; // Apunta a client/project-applications.html
    }

    /**
     * ACEPTA una postulación.
     */
    @PostMapping("/application/{id}/accept")
    public String acceptApplication(
            @PathVariable("id") Integer postulacionId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Encontrar la postulación
            Postulacion postulacion = postulacionRepository.findById(postulacionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulación no encontrada"));

            // 2. Validar que el contratista sea el dueño del proyecto
            Usuario usuario = userDetails.getUsuario();
            if (!postulacion.getProyecto().getContratista().getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
            }

            // 3. Cambiar el estado y guardar
            postulacion.setEstado(EstadoPostulacion.aceptada); //
            postulacionRepository.save(postulacion);

            // (Opcional: podrías cambiar el estado del proyecto a "EN_PROGRESO" aquí)
            // Proyecto proyecto = postulacion.getProyecto();
            // proyecto.setEstado(Proyecto.EstadoProyecto.en_progreso);
            // proyectoRepository.save(proyecto);

            redirectAttributes.addFlashAttribute("successMessage", "¡Postulación aceptada exitosamente!");

            // 4. Redirigir de vuelta a la lista
            return "redirect:/client/project/" + postulacion.getProyecto().getIdProyecto() + "/applications";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/client/my-projects"; // Redirigir a mis proyectos si algo sale muy mal
        }
    }

    /**
     * RECHAZA una postulación.
     */
    @PostMapping("/application/{id}/reject")
    public String rejectApplication(
            @PathVariable("id") Integer postulacionId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Encontrar la postulación
            Postulacion postulacion = postulacionRepository.findById(postulacionId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulación no encontrada"));

            // 2. Validar que el contratista sea el dueño del proyecto
            Usuario usuario = userDetails.getUsuario();
            if (!postulacion.getProyecto().getContratista().getIdUsuario().equals(usuario.getIdUsuario())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
            }

            // 3. Cambiar el estado y guardar
            postulacion.setEstado(EstadoPostulacion.rechazada); //
            postulacionRepository.save(postulacion);

            redirectAttributes.addFlashAttribute("successMessage", "Postulación rechazada correctamente.");

            // 4. Redirigir de vuelta a la lista
            return "redirect:/client/project/" + postulacion.getProyecto().getIdProyecto() + "/applications";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/client/my-projects";
        }
    }
    /**
     * Muestra la página de VISUALIZACIÓN del perfil del contratista.
     */
    @GetMapping("/profile")
    public String showViewProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        PerfilContratista perfil = usuario.getPerfilContratista();

        // Cargar la ubicación (departamento y ciudad)
        Ubicacion ubicacion = ubicacionRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream().findFirst().orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", perfil);
        model.addAttribute("ubicacion", ubicacion);

        // Cargar la imagen de logo (si existe)
        if (perfil != null && perfil.getLogoEmpresa() != null) {
            String base64Logo = Base64.getEncoder().encodeToString(perfil.getLogoEmpresa());
            model.addAttribute("currentLogo", base64Logo);
        }

        return "client/profile"; // Apunta a la NUEVA profile.html
    }
    /**
     * Muestra el formulario para editar el perfil del contratista.
     */
    @GetMapping("/edit-profile") // <-- RUTA CAMBIADA
    public String showEditProfileForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        // SOLUCIÓN: "Desenvolver" el usuario desde CustomUserDetails
        Usuario usuario = userDetails.getUsuario();

        // 1. Cargar datos del usuario, perfil y ubicación
        PerfilContratista perfil = usuario.getPerfilContratista(); // <-- Esta línea ahora es segura

        // Buscamos la primera ubicación (un usuario podría tener varias, pero aquí asumimos 1)
        Ubicacion ubicacion = ubicacionRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream().findFirst().orElse(new Ubicacion()); // Crea una nueva si no existe

        // 2. Poblar el DTO con los datos existentes
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setNombre(usuario.getNombre());
        profileDTO.setApellido(usuario.getApellido());
        profileDTO.setTelefono(usuario.getTelefono());
        profileDTO.setDui(usuario.getDui());

        if (perfil != null) {
            profileDTO.setEmpresa(perfil.getEmpresa());
            profileDTO.setDescripcion(perfil.getDescripcion());
            profileDTO.setSitioWeb(perfil.getSitioWeb());
        }

        profileDTO.setDepartamento(ubicacion.getDepartamento());
        profileDTO.setCiudad(ubicacion.getCiudad());

        // 3. Preparar datos para los dropdowns de ubicación
        Map<String, List<String>> locationData = getDepartamentosMunicipios();
        model.addAttribute("locationData", locationData);
        model.addAttribute("departamentos", locationData.keySet());

        // 4. Añadir el DTO al modelo
        model.addAttribute("profileDTO", profileDTO);

        // 5. (Para mostrar la imagen/logo actual)
        if (perfil != null && perfil.getLogoEmpresa() != null) {
            String base64Logo = Base64.getEncoder().encodeToString(perfil.getLogoEmpresa());
            model.addAttribute("currentLogo", base64Logo);
        }

        return "client/edit-profile"; // <-- PLANTILLA CAMBIADA
    }
    /**
     * Procesa la actualización del perfil del contratista.
     */
    @PostMapping("/edit-profile") // <-- Esta es la URL del controlador
    @Transactional
    public String processEditProfileForm(
            @Valid @ModelAttribute("profileDTO") ProfileDTO profileDTO,
            BindingResult bindingResult,
            @RequestParam("logoEmpresa") MultipartFile logoFile, // Captura el archivo del logo
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Recargar al usuario desde la BD para asegurar que esté "gestionado" por JPA
            Usuario usuario = userDetails.getUsuario();
            Usuario usuarioActual = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            // 2. Cargar el perfil (debe existir desde el registro)
            PerfilContratista perfil = usuarioActual.getPerfilContratista();
            if (perfil == null) {
                // Esto no debería pasar si el registro funciona, pero es una buena validación
                perfil = new PerfilContratista();
                perfil.setUsuario(usuarioActual);
            }

            // 3. Cargar o crear la ubicación
            Ubicacion ubicacion = ubicacionRepository.findByUsuarioIdUsuario(usuarioActual.getIdUsuario())
                    .stream().findFirst().orElse(new Ubicacion());
            ubicacion.setUsuario(usuarioActual); // Asegurar la relación

            // 4. Validar errores del formulario
            if (bindingResult.hasErrors()) {
                // Si hay errores, recargar datos para los dropdowns y mostrar errores
                Map<String, List<String>> locationData = getDepartamentosMunicipios();
                model.addAttribute("locationData", locationData);
                model.addAttribute("departamentos", locationData.keySet());
                // (No olvides re-cargar la imagen actual si la tenías)
                return "edit-profile";
            }

            // 5. Actualizar los datos del Usuario
            usuarioActual.setNombre(profileDTO.getNombre());
            usuarioActual.setApellido(profileDTO.getApellido());
            usuarioActual.setTelefono(profileDTO.getTelefono());
            usuarioActual.setDui(profileDTO.getDui());

            // 6. Actualizar los datos de PerfilContratista
            perfil.setEmpresa(profileDTO.getEmpresa());
            perfil.setDescripcion(profileDTO.getDescripcion());
            perfil.setSitioWeb(profileDTO.getSitioWeb());

            // 7. Actualizar el Logo (SOLO si se subió uno nuevo)
            // (Usamos la lógica de byte[] de la Opción 2)
            if (logoFile != null && !logoFile.isEmpty()) {
                perfil.setLogoEmpresa(logoFile.getBytes());
            }

            // 8. Actualizar los datos de Ubicacion
            perfil.setUbicacion(profileDTO.getDepartamento()); // Actualiza el campo de filtro rápido
            ubicacion.setDepartamento(profileDTO.getDepartamento());
            ubicacion.setCiudad(profileDTO.getCiudad());

            // 9. Guardar TODAS las entidades modificadas
            usuarioRepository.save(usuarioActual);
            perfilContratistaRepository.save(perfil);
            ubicacionRepository.save(ubicacion);

            // 10. Redirigir con mensaje de éxito
            redirectAttributes.addFlashAttribute("successMessage", "¡Perfil actualizado exitosamente!");
            return "redirect:/client/edit-profile";

        } catch (Exception e) {
            // 11. Manejar errores
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el perfil: " + e.getMessage());
            return "redirect:/client/edit-profile";
        }
    }

    /**
     * Muestra el formulario de ajustes (cambiar contraseña).
     */
    @GetMapping("/change-password")
    public String showSettingsForm(Model model) {

        // Añadimos el DTO vacío para el formulario
        model.addAttribute("passwordDTO", new PasswordChangeDTO());

        return "client/change-password";
    }

    /**
     * Procesa el formulario de cambio de contraseña.
     */
    @PostMapping("/change-password")
    public String processChangePassword(
            @Valid @ModelAttribute("passwordDTO") PasswordChangeDTO passwordDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        // 1. Validaciones básicas del formulario
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error en los datos del formulario.");
            return "redirect:/client/change-password";
        }

        // 2. Validar que la nueva contraseña y la confirmación coincidan
        if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "La nueva contraseña y la confirmación no coinciden.");
            return "redirect:/client/change-password";
        }

        // 3. Validar que la contraseña antigua sea correcta
        Usuario usuario = userDetails.getUsuario();
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), usuario.getContrasena())) {
            redirectAttributes.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
            return "redirect:/client/change-password";
        }

        // 4. (Opcional) Validar seguridad de la nueva contraseña (longitud, etc.)
        if (passwordDTO.getNewPassword().length() < 8) {
            redirectAttributes.addFlashAttribute("errorMessage", "La nueva contraseña debe tener al menos 8 caracteres.");
            return "redirect:/client/change-password";
        }

        try {
            // 5. Todo es correcto. Guardar la nueva contraseña.
            Usuario usuarioActual = usuarioRepository.findById(usuario.getIdUsuario())
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            usuarioActual.setContrasena(passwordEncoder.encode(passwordDTO.getNewPassword()));
            usuarioRepository.save(usuarioActual);

            redirectAttributes.addFlashAttribute("successMessage", "¡Contraseña actualizada exitosamente!");
            return "redirect:/client/settings";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar la contraseña: " + e.getMessage());
            return "redirect:/client/settings";
        }
    }

    /**
     * Muestra la página "Hub" de Ajustes.
     */
    @GetMapping("/settings")
    public String showSettingsHub() {
        return "client/settings"; // Apunta al NUEVO archivo settings.html
    }

    /**
     * Muestra la página de chat/mensajería.
     * (Por ahora, es una página estática).
     */
    @GetMapping("/chat")
    public String showChat(Model model) {
        // En un futuro, aquí cargarías la lista de contactos y mensajes
        // model.addAttribute("listaDeConversaciones", ...);

        return "client/chat"; // Apunta a client/chat.html
    }
    /**
     * Muestra la página para buscar perfiles de estudiantes (freelancers).
     */
    @GetMapping("/search-freelancer")
    public String showSearchFreelancer(
            @RequestParam(required = false) String habilidad,
            @RequestParam(required = false) String busqueda,
            Model model) {

        // 1. Cargar Habilidades para los filtros
        List<Habilidad> habilidades = habilidadRepository.findAll();
        model.addAttribute("habilidades", habilidades);
        model.addAttribute("habilidadSeleccionada", habilidad != null ? habilidad : "all");
        model.addAttribute("busqueda", busqueda);

        // 2. Obtener la lista de estudiantes (misma lógica que en explore)
        List<Usuario> estudiantes;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            estudiantes = usuarioRepository.findEstudiantesWithProfileBySearch(busqueda);
        } else if (habilidad != null && !habilidad.equals("all")) {
            estudiantes = usuarioRepository.findEstudiantesByHabilidad(habilidad);
        } else {
            estudiantes = usuarioRepository.findAllEstudiantesWithProfile();
        }

        // 3. Convertir imágenes a Base64 (misma lógica que en explore)
        List<Map<String, Object>> estudiantesVM = new ArrayList<>();
        for (Usuario u : estudiantes) {
            Map<String, Object> vm = new HashMap<>();
            vm.put("usuario", u);
            String base64Img = null;
            if (u.getPerfilEstudiante() != null && u.getPerfilEstudiante().getFotoPerfil() != null) {
                base64Img = Base64.getEncoder().encodeToString(u.getPerfilEstudiante().getFotoPerfil());
            }
            vm.put("base64Image", base64Img);
            estudiantesVM.add(vm);
        }

        // 4. Añadir la lista procesada al modelo
        model.addAttribute("estudiantes", estudiantesVM);

        return "client/search-freelancer"; // Apunta a client/search-freelancer.html
    }

    /**
     * MUESTRA el perfil interno de un estudiante al contratista.
     * URL: /client/student-profile/{id}
     */
    @GetMapping("/student-profile/{id}")
    public String showStudentProfileInternal(
            @PathVariable("id") Integer idEstudiante,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. Buscar al estudiante con su perfil completo
        //    (Usamos el método 'findByIdWithFullProfile' que ya debes tener en tu UsuarioRepository)
        Usuario estudiante = usuarioRepository.findByIdWithFullProfile(idEstudiante)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // 2. Extraer los datos
        PerfilEstudiante perfil = estudiante.getPerfilEstudiante();
        Set<Habilidad> habilidades = estudiante.getHabilidades();
        Set<Universidad> universidades = estudiante.getUniversidades();

        // 3. Preparar la foto de perfil
        String base64Photo = null;
        if (perfil != null && perfil.getFotoPerfil() != null) {
            base64Photo = Base64.getEncoder().encodeToString(perfil.getFotoPerfil());
        }

        // 4. Enviar todo al modelo
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("perfil", perfil);
        model.addAttribute("habilidades", habilidades);
        model.addAttribute("universidades", universidades);
        model.addAttribute("base64Photo", base64Photo);

        // 5. Apuntar a la nueva plantilla HTML que crearemos
        return "client/student-profile";
    }
}