package com.example.Hirelance.controllers;

import com.example.Hirelance.config.CustomUserDetails;
import com.example.Hirelance.dto.AdminUserEditDTO;
import com.example.Hirelance.models.*;
import com.example.Hirelance.repository.ProyectoRepository;
import com.example.Hirelance.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List; // (Esta ya debería estar)


import com.example.Hirelance.repository.UniversidadRepository; // ¡NUEVO!
import java.util.Base64; // ¡NUEVO!
import java.util.Map; // ¡NUEVO!
import java.util.HashMap; // ¡NUEVO!
import java.util.ArrayList; // ¡NUEVO!
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping; // ¡NUEVO!
import org.springframework.web.bind.annotation.ModelAttribute; // ¡NUEVO!
import org.springframework.web.bind.annotation.RequestParam; // ¡NUEVO!
import org.springframework.web.multipart.MultipartFile; // ¡NUEVO!
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // ¡NUEVO!

import com.example.Hirelance.repository.CategoriaRepository; // ¡NUEVO!

import com.example.Hirelance.repository.ReporteRepository; // ¡NUEVO!
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus; // ¡NUEVO!
import org.springframework.web.server.ResponseStatusException; // ¡NUEVO!
import com.example.Hirelance.repository.PostulacionRepository; // ¡LA LÍNEA QUE FALTA!
import com.example.Hirelance.dto.AdminUserEditDTO; // ¡NUEVO!
import jakarta.validation.Valid; // ¡NUEVO!
import org.springframework.validation.BindingResult; // ¡NUEVO!
import org.springframework.transaction.annotation.Transactional; // ¡NUEVO!

import com.example.Hirelance.models.Habilidad; // ¡NUEVO!
import com.example.Hirelance.repository.HabilidadRepository; // ¡NUEVO!

import java.time.LocalDate; // ¡NUEVO!

import com.example.Hirelance.models.Valoracion; // ¡NUEVO!
import com.example.Hirelance.repository.ValoracionRepository; // ¡NUEVO!

import com.example.Hirelance.models.Contrato; // ¡NUEVO!
import com.example.Hirelance.repository.ContratoRepository; // ¡NUEVO!
import java.math.BigDecimal; // ¡NUEVO!

import java.time.LocalDate; // ¡NUEVO!
import org.springframework.format.annotation.DateTimeFormat; // ¡NUEVO!

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator; // ¡NUEVO!

@Controller
@RequestMapping("/admin") // Todas las URLs de este controlador empezarán con /admin
@PreAuthorize("hasAuthority('admin')") // ¡Seguridad! Solo para administradores
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UniversidadRepository universidadRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private HabilidadRepository habilidadRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    /**
     * Muestra el dashboard principal del administrador.
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario admin = userDetails.getUsuario();
        model.addAttribute("nombreAdmin", admin.getNombre());

        // --- INICIO DE LA LÓGICA ACTUALIZADA ---

        // 1. Cargar estadísticas globales (KPIs)
        long totalUsuarios = usuarioRepository.count();
        long totalProyectos = proyectoRepository.count();
        long totalPostulaciones = postulacionRepository.count();
        long reportesPendientes = reporteRepository.countByEstado(Reporte.EstadoReporte.pendiente);

        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalProyectos", totalProyectos);
        model.addAttribute("totalPostulaciones", totalPostulaciones);
        model.addAttribute("reportesPendientes", reportesPendientes);

        // 2. Cargar Feeds de Actividad Reciente
        List<Usuario> usuariosRecientes = usuarioRepository.findTop5ByOrderByFechaRegistroDesc();
        List<Proyecto> proyectosRecientes = proyectoRepository.findTop5ByOrderByFechaCreacionDesc();
        List<Reporte> reportesRecientes = reporteRepository.findTop5ByOrderByFechaReporteDesc();

        model.addAttribute("usuariosRecientes", usuariosRecientes);
        model.addAttribute("proyectosRecientes", proyectosRecientes);
        model.addAttribute("reportesRecientes", reportesRecientes);

        // --- FIN DE LA LÓGICA ACTUALIZADA ---

        return "admin/dashboard"; // Apunta a admin/dashboard.html
    }
    /**
     * Muestra la página de gestión de usuarios (CON FILTROS).
     */
    @GetMapping("/manage-users")
    public String showManageUsers(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Model model) {

        // 1. Cargar todos los usuarios
        List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

        // 2. Aplicar filtros y búsqueda en Java
        List<Usuario> usuariosFiltrados = todosLosUsuarios.stream()
                .filter(u -> (busqueda == null || busqueda.isEmpty()) ||
                        u.getNombre().toLowerCase().contains(busqueda.toLowerCase()) ||
                        u.getApellido().toLowerCase().contains(busqueda.toLowerCase()) ||
                        u.getCorreo().toLowerCase().contains(busqueda.toLowerCase()))
                .filter(u -> (tipo == null || tipo.equals("all")) ||
                        u.getTipo().name().equalsIgnoreCase(tipo))
                .filter(u -> (estado == null || estado.equals("all")) ||
                        u.getEstado().name().equalsIgnoreCase(estado))
                .collect(Collectors.toList());

        // 3. Añadir todo al modelo
        model.addAttribute("usuarios", usuariosFiltrados);
        model.addAttribute("tiposUsuario", Usuario.TipoUsuario.values());
        model.addAttribute("estadosUsuario", Usuario.EstadoUsuario.values());
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("tipoSeleccionado", tipo != null ? tipo : "all");
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");

        return "admin/manage-users";
    }
    /**
     * CAMBIA EL ESTADO de un usuario a 'baneado'.
     */
    @PostMapping("/manage-users/{id}/suspend")
    public String suspendUser(
            @PathVariable("id") Integer userId,
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            RedirectAttributes redirectAttributes) {

        Usuario admin = adminDetails.getUsuario();

        // 1. Verificación de seguridad: Un admin no se puede banear a sí mismo
        if (admin.getIdUsuario().equals(userId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No puedes suspender tu propia cuenta.");
            return "redirect:/admin/manage-users";
        }

        try {
            Usuario userToSuspend = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            // 2. Cambiar el estado y guardar
            userToSuspend.setEstado(Usuario.EstadoUsuario.baneado); //
            usuarioRepository.save(userToSuspend);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario suspendido exitosamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/manage-users";
    }

    /**
     * CAMBIA EL ESTADO de un usuario a 'activo'.
     */
    @PostMapping("/manage-users/{id}/reactivate")
    public String reactivateUser(
            @PathVariable("id") Integer userId,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario userToReactivate = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            // 1. Cambiar el estado y guardar
            userToReactivate.setEstado(Usuario.EstadoUsuario.activo); //
            usuarioRepository.save(userToReactivate);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario reactivado exitosamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/manage-users";
    }
    /**
     * Muestra la vista de "Detalles" para un usuario específico (estudiante o contratista).
     */
    @GetMapping("/manage-users/{id}/view")
    public String viewUserDetails(@PathVariable("id") Integer userId, Model model) {

        // 1. Cargar el usuario básico primero
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        model.addAttribute("usuario", usuario); // Pasar el usuario básico

        // 2. Cargar datos de ACTIVIDAD (Reportes)
        // (Esto es para todos los tipos de usuario)
        List<Reporte> reportes = reporteRepository.findByUsuarioIdUsuario(userId);
        model.addAttribute("reportes", reportes);

        // 3. Cargar datos específicos según el TIPO de usuario
        if (usuario.getTipo() == Usuario.TipoUsuario.estudiante) {

            // Cargar el perfil completo de estudiante
            Usuario estudiante = usuarioRepository.findByIdWithFullProfile(userId).get();
            model.addAttribute("perfil", estudiante.getPerfilEstudiante());
            model.addAttribute("habilidades", estudiante.getHabilidades());
            model.addAttribute("universidades", estudiante.getUniversidades());

            // Cargar foto
            if (estudiante.getPerfilEstudiante() != null && estudiante.getPerfilEstudiante().getFotoPerfil() != null) {
                model.addAttribute("base64Image", Base64.getEncoder().encodeToString(estudiante.getPerfilEstudiante().getFotoPerfil()));
            }

            // --- ¡NUEVO! Cargar Postulaciones del Estudiante ---
            List<Postulacion> postulaciones = postulacionRepository.findAllByEstudianteIdWithProyecto(userId);
            model.addAttribute("postulaciones", postulaciones);

        } else if (usuario.getTipo() == Usuario.TipoUsuario.contratista) {

            // Cargar el perfil completo de contratista
            Usuario contratista = usuarioRepository.findContratistaByIdWithProfile(userId).get();
            model.addAttribute("perfil", contratista.getPerfilContratista());

            Ubicacion ubicacion = contratista.getUbicaciones().stream().findFirst().orElse(null);
            model.addAttribute("ubicacion", ubicacion);

            // Cargar logo
            if (contratista.getPerfilContratista() != null && contratista.getPerfilContratista().getLogoEmpresa() != null) {
                model.addAttribute("base64Image", Base64.getEncoder().encodeToString(contratista.getPerfilContratista().getLogoEmpresa()));
            }

            // --- ¡NUEVO! Cargar Proyectos del Contratista ---
            List<Proyecto> proyectos = proyectoRepository.findByContratistaIdUsuarioOrderByFechaCreacionDesc(userId);
            model.addAttribute("proyectos", proyectos);
        }

        return "admin/view-user-details";
    }
    /**
     * Muestra la página de gestión de todos los proyectos (CON FILTROS).
     */
    @GetMapping("/manage-projects")
    public String showManageProjects(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String estado,
            Model model) {

        // 1. Cargar todos los proyectos (ya trae contratista y categoría)
        List<Proyecto> todosLosProyectos = proyectoRepository.findAllWithContratistaAndCategoria();

        // 2. Aplicar filtros
        List<Proyecto> proyectosFiltrados = todosLosProyectos.stream()
                .filter(p -> (busqueda == null || busqueda.isEmpty()) ||
                        p.getTitulo().toLowerCase().contains(busqueda.toLowerCase()) ||
                        (p.getContratista() != null && p.getContratista().getNombre().toLowerCase().contains(busqueda.toLowerCase())) ||
                        (p.getContratista() != null && p.getContratista().getApellido().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(p -> (idCategoria == null || idCategoria == 0) ||
                        (p.getCategoria() != null && p.getCategoria().getIdCategoria().equals(idCategoria)))
                .filter(p -> (estado == null || estado.equals("all")) ||
                        p.getEstado().name().equalsIgnoreCase(estado))
                .collect(Collectors.toList());

        // 3. Cargar datos para los dropdowns
        List<Categoria> categorias = categoriaRepository.findAll();

        model.addAttribute("proyectos", proyectosFiltrados);
        model.addAttribute("categorias", categorias); // Para el dropdown
        model.addAttribute("estadosProyecto", Proyecto.EstadoProyecto.values()); // Para el dropdown
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("categoriaSeleccionada", idCategoria != null ? idCategoria : 0);
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");

        return "admin/manage-projects";
    }
    @GetMapping("/manage-universities")
    public String showManageUniversities(
            @RequestParam(required = false) String busqueda, // ¡NUEVO!
            Model model) {

        List<Universidad> universidades = universidadRepository.findAll();

        // ¡NUEVO! Filtrar por nombre
        List<Universidad> universidadesFiltradas = universidades.stream()
                .filter(u -> (busqueda == null || busqueda.isEmpty()) ||
                        u.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                .collect(Collectors.toList());

        // Convertir logos (igual que antes)
        List<Map<String, Object>> universidadesVM = new ArrayList<>();
        for (Universidad uni : universidadesFiltradas) { // Usar la lista filtrada
            Map<String, Object> vm = new HashMap<>();
            vm.put("universidad", uni);
            if (uni.getLogo() != null) {
                vm.put("base64Logo", Base64.getEncoder().encodeToString(uni.getLogo()));
            } else {
                vm.put("base64Logo", null);
            }
            universidadesVM.add(vm);
        }

        model.addAttribute("universidades", universidadesVM);
        model.addAttribute("newUniversidad", new Universidad());
        model.addAttribute("busqueda", busqueda); // ¡NUEVO!

        return "admin/manage-universities";
    }

    /**
     * Procesa el formulario para añadir una nueva universidad.
     */
    @PostMapping("/add-university")
    public String addUniversity(
            @ModelAttribute("newUniversidad") Universidad newUniversidad,
            @RequestParam("logoFile") MultipartFile logoFile,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Procesar y guardar el logo si se subió
            if (logoFile != null && !logoFile.isEmpty()) {
                newUniversidad.setLogo(logoFile.getBytes()); // Guardar como byte[]
            }

            // 2. Guardar la universidad en la BD
            universidadRepository.save(newUniversidad);

            redirectAttributes.addFlashAttribute("successMessage", "Universidad añadida exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir la universidad: " + e.getMessage());
        }

        return "redirect:/admin/manage-universities";
    }

    @GetMapping("/manage-categories")
    public String showManageCategories(
            @RequestParam(required = false) String busqueda, // ¡NUEVO!
            Model model) {

        List<Categoria> categorias = categoriaRepository.findAll();

        // ¡NUEVO! Filtrar por nombre
        List<Categoria> categoriasFiltradas = categorias.stream()
                .filter(c -> (busqueda == null || busqueda.isEmpty()) ||
                        c.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("categorias", categoriasFiltradas);
        model.addAttribute("newCategoria", new Categoria());
        model.addAttribute("busqueda", busqueda); // ¡NUEVO!

        return "admin/manage-categories";
    }

    /**
     * Procesa el formulario para añadir una nueva categoría.
     */
    @PostMapping("/add-category")
    public String addCategory(
            @ModelAttribute("newCategoria") Categoria newCategoria,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // (Podrías añadir validación aquí, ej: verificar que el nombre no esté vacío)
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error en los datos del formulario.");
            return "redirect:/admin/manage-categories";
        }

        try {
            categoriaRepository.save(newCategoria);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría añadida exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir la categoría: " + e.getMessage());
        }

        return "redirect:/admin/manage-categories";
    }

    /**
     * Muestra la página de gestión de Reportes de usuarios (CON FILTROS).
     */
    @GetMapping("/manage-reports")
    public String showManageReports(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            Model model) {

        // 1. Cargar todos los reportes (ya trae al usuario)
        List<Reporte> reportes = reporteRepository.findAllWithUsuario();

        // 2. Aplicar filtros
        List<Reporte> reportesFiltrados = reportes.stream()
                .filter(r -> (busqueda == null || busqueda.isEmpty()) ||
                        r.getDescripcion().toLowerCase().contains(busqueda.toLowerCase()) ||
                        (r.getUsuario() != null && r.getUsuario().getCorreo().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(r -> (tipo == null || tipo.equals("all")) ||
                        r.getTipo().name().equalsIgnoreCase(tipo))
                .filter(r -> (estado == null || estado.equals("all")) ||
                        r.getEstado().name().equalsIgnoreCase(estado))
                .collect(Collectors.toList());

        // 3. Añadir todo al modelo
        model.addAttribute("reportes", reportesFiltrados);
        model.addAttribute("tiposReporte", Reporte.TipoReporte.values());
        model.addAttribute("estadosReporte", Reporte.EstadoReporte.values());
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("tipoSeleccionado", tipo != null ? tipo : "all");
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");

        return "admin/manage-reports";
    }

    @GetMapping("/users/{id}/applications")
    public String viewUserApplications(
            @PathVariable("id") Integer userId,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double minPropuesta,
            @RequestParam(required = false) Double maxPropuesta,
            Model model) {

        // ... (Validación de usuario - sin cambios)
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (usuario.getTipo() != Usuario.TipoUsuario.estudiante) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario no es un estudiante.");
        }

        List<Postulacion> basePostulaciones = postulacionRepository.findAllByEstudianteIdWithProyecto(userId);

        // Aplicar filtros
        List<Postulacion> postulacionesFiltradas = basePostulaciones.stream()
                .filter(p -> (busqueda == null || p.getProyecto().getTitulo().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(p -> (estado == null || estado.equals("all") || p.getEstado().name().equalsIgnoreCase(estado)))

                // --- ¡INICIO DE LA LÓGICA CORREGIDA! ---
                .filter(p -> {
                    // Si no se aplican filtros de presupuesto, la postulación pasa.
                    if (minPropuesta == null && maxPropuesta == null) {
                        return true;
                    }
                    // Si se aplica un filtro pero la postulación no tiene presupuesto, no pasa.
                    if (p.getPresupuestoPropuesto() == null) {
                        return false;
                    }
                    // Comprobar los filtros de rango
                    boolean minOk = (minPropuesta == null || p.getPresupuestoPropuesto() >= minPropuesta);
                    boolean maxOk = (maxPropuesta == null || p.getPresupuestoPropuesto() <= maxPropuesta);
                    return minOk && maxOk;
                })
                // --- FIN DE LA CORRECCIÓN ---

                .collect(Collectors.toList());

        // ... (Resto del método - sin cambios)
        model.addAttribute("usuario", usuario);
        model.addAttribute("postulaciones", postulacionesFiltradas);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");
        model.addAttribute("estadosPostulacion", Postulacion.EstadoPostulacion.values());
        model.addAttribute("minPropuesta", minPropuesta);
        model.addAttribute("maxPropuesta", maxPropuesta);

        return "admin/user-applications";
    }

    @GetMapping("/users/{id}/projects")
    public String viewUserProjects(
            @PathVariable("id") Integer userId,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double minPresupuesto,
            @RequestParam(required = false) Double maxPresupuesto,
            Model model) {

        // ... (Validación de usuario - sin cambios)
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if (usuario.getTipo() != Usuario.TipoUsuario.contratista) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este usuario no es un contratista.");
        }

        List<Proyecto> baseProyectos = proyectoRepository.findByContratistaIdUsuarioOrderByFechaCreacionDesc(userId);

        // Aplicar filtros
        List<Proyecto> proyectosFiltrados = baseProyectos.stream()
                .filter(p -> (busqueda == null || p.getTitulo().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(p -> (estado == null || estado.equals("all") || p.getEstado().name().equalsIgnoreCase(estado)))

                // --- ¡INICIO DE LA LÓGICA CORREGIDA! ---
                .filter(p -> {
                    if (minPresupuesto == null && maxPresupuesto == null) {
                        return true;
                    }
                    if (p.getPresupuesto() == null) {
                        return false;
                    }
                    boolean minOk = (minPresupuesto == null || p.getPresupuesto() >= minPresupuesto);
                    boolean maxOk = (maxPresupuesto == null || p.getPresupuesto() <= maxPresupuesto);
                    return minOk && maxOk;
                })
                // --- FIN DE LA CORRECCIÓN ---

                .collect(Collectors.toList());

        // ... (Resto del método - sin cambios)
        model.addAttribute("usuario", usuario);
        model.addAttribute("proyectos", proyectosFiltrados);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");
        model.addAttribute("estadosProyecto", Proyecto.EstadoProyecto.values());
        model.addAttribute("minPresupuesto", minPresupuesto);
        model.addAttribute("maxPresupuesto", maxPresupuesto);

        return "admin/user-projects";
    }
    /**
     * Muestra los reportes de un usuario específico.
     */
    @GetMapping("/users/{id}/reports")
    public String viewUserReports(
            @PathVariable("id") Integer userId,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) LocalDate fechaInicio, // ¡NUEVO!
            @RequestParam(required = false) LocalDate fechaFin,     // ¡NUEVO!
            Model model) {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<Reporte> baseReportes = reporteRepository.findByUsuarioIdUsuario(userId);

        // Aplicar filtros
        List<Reporte> reportesFiltrados = baseReportes.stream()
                .filter(r -> (busqueda == null || r.getDescripcion().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(r -> (estado == null || estado.equals("all") || r.getEstado().name().equalsIgnoreCase(estado)))
                .filter(r -> (tipo == null || tipo.equals("all") || r.getTipo().name().equalsIgnoreCase(tipo)))
                // --- ¡NUEVA LÓGICA DE FILTRO! ---
                .filter(r -> (fechaInicio == null || r.getFechaReporte().toLocalDate().isAfter(fechaInicio.minusDays(1))))
                .filter(r -> (fechaFin == null || r.getFechaReporte().toLocalDate().isBefore(fechaFin.plusDays(1))))
                // --- FIN ---
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("reportes", reportesFiltrados);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");
        model.addAttribute("tipoSeleccionado", tipo != null ? tipo : "all");
        model.addAttribute("estadosReporte", Reporte.EstadoReporte.values());
        model.addAttribute("tiposReporte", Reporte.TipoReporte.values());
        model.addAttribute("fechaInicio", fechaInicio); // ¡NUEVO!
        model.addAttribute("fechaFin", fechaFin);     // ¡NUEVO!

        return "admin/user-reports";
    }
    /**
     * Muestra el formulario para EDITAR un usuario específico.
     */
    @GetMapping("/manage-users/{id}/edit")
    public String showEditUserForm(@PathVariable("id") Integer userId, Model model) {

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 1. Crear el DTO y poblarlo con los datos del usuario
        AdminUserEditDTO dto = new AdminUserEditDTO();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getCorreo());
        dto.setTipo(usuario.getTipo());
        dto.setEstado(usuario.getEstado());

        // 2. Pasar el DTO y las listas de Enums a la vista
        model.addAttribute("userDTO", dto);
        model.addAttribute("tiposUsuario", Usuario.TipoUsuario.values());
        model.addAttribute("estadosUsuario", Usuario.EstadoUsuario.values());

        return "admin/edit-user"; // Apunta a la nueva plantilla
    }
    /**
     * Procesa el formulario de EDICIÓN de un usuario.
     */
    @PostMapping("/manage-users/{id}/edit")
    public String processEditUserForm(
            @PathVariable("id") Integer userId,
            @Valid @ModelAttribute("userDTO") AdminUserEditDTO userDTO,
            BindingResult bindingResult,
            @AuthenticationPrincipal CustomUserDetails adminDetails,
            RedirectAttributes redirectAttributes, Model model) {

        Usuario admin = adminDetails.getUsuario();

        // 1. Validar que el admin no se edite a sí mismo (para evitar auto-bloqueo)
        if (admin.getIdUsuario().equals(userId)) {
            // Un admin no puede cambiar su propio tipo o estado desde este formulario.
            if (userDTO.getTipo() != Usuario.TipoUsuario.admin || userDTO.getEstado() != Usuario.EstadoUsuario.activo) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: No puedes cambiar tu propio tipo o estado.");
                return "redirect:/admin/manage-users";
            }
        }

        // 2. Validar errores del DTO
        if (bindingResult.hasErrors()) {
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("tiposUsuario", Usuario.TipoUsuario.values());
            model.addAttribute("estadosUsuario", Usuario.EstadoUsuario.values());
            return "admin/edit-user"; // Volver al formulario si hay errores
        }

        try {
            // 3. Cargar el usuario de la BD
            Usuario userToUpdate = usuarioRepository.findById(userId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado"));

            // 4. Actualizar los campos
            userToUpdate.setNombre(userDTO.getNombre());
            userToUpdate.setApellido(userDTO.getApellido());
            userToUpdate.setCorreo(userDTO.getCorreo());
            userToUpdate.setTipo(userDTO.getTipo());
            userToUpdate.setEstado(userDTO.getEstado());

            // 5. Guardar
            usuarioRepository.save(userToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado exitosamente.");
            return "redirect:/admin/manage-users";

        } catch (Exception e) {
            // Manejar errores (ej: correo duplicado)
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
            return "redirect:/admin/manage-users/{id}/edit";
        }
    }

    /**
     * "Elimina" un proyecto (Soft Delete) cambiándole el estado a 'cancelado'.
     */
    @PostMapping("/manage-projects/{id}/cancel")
    public String cancelProject(
            @PathVariable("id") Integer proyectoId,
            RedirectAttributes redirectAttributes) {

        try {
            Proyecto proyecto = proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

            // 2. Cambiar el estado y guardar
            proyecto.setEstado(Proyecto.EstadoProyecto.cancelado); //
            proyectoRepository.save(proyecto);

            redirectAttributes.addFlashAttribute("successMessage", "Proyecto cancelado (eliminado) exitosamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/manage-projects";
    }

    /**
     * Muestra la vista de "Detalles" para un proyecto específico.
     */
    @GetMapping("/manage-projects/{id}/details")
    public String viewProjectDetails(@PathVariable("id") Integer proyectoId, Model model) {

        // 1. Cargar el proyecto (usamos el método que ya trae contratista y categoría)
        Proyecto proyecto = proyectoRepository.findAllWithContratistaAndCategoria().stream()
                .filter(p -> p.getIdProyecto().equals(proyectoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado"));

        // 2. Cargar todas las postulaciones para este proyecto
        // (Usamos el método que ya trae al estudiante)
        List<Postulacion> postulaciones = postulacionRepository.findAllByProyectoIdWithEstudiante(proyectoId);

        // 3. Añadir todo al modelo
        model.addAttribute("proyecto", proyecto);
        model.addAttribute("postulaciones", postulaciones);

        return "admin/project-details"; // Apunta a la nueva plantilla
    }

    /**
     * Elimina una universidad SÓLO si no está en uso.
     */
    @PostMapping("/manage-universities/{id}/delete")
    @Transactional // Necesario para cargar la colección 'usuarios'
    public String deleteUniversity(
            @PathVariable("id") Integer uniId,
            RedirectAttributes redirectAttributes) {

        try {
            Universidad uni = universidadRepository.findById(uniId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universidad no encontrada"));

            // 1. Verificación de seguridad: ¿Está esta universidad en uso?
            if (uni.getUsuarios() != null && !uni.getUsuarios().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: No se puede eliminar la universidad '" + uni.getNombre() + "' porque está siendo utilizada por " + uni.getUsuarios().size() + " estudiante(s).");
                return "redirect:/admin/manage-universities";
            }

            // 2. Si no está en uso, eliminarla
            universidadRepository.delete(uni);
            redirectAttributes.addFlashAttribute("successMessage", "Universidad eliminada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la universidad: " + e.getMessage());
        }

        return "redirect:/admin/manage-universities";
    }

    /**
     * Muestra el formulario para EDITAR una universidad.
     */
    @GetMapping("/manage-universities/{id}/edit")
    public String showEditUniversityForm(
            @PathVariable("id") Integer uniId,
            Model model) {

        Universidad uni = universidadRepository.findById(uniId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universidad no encontrada"));

        model.addAttribute("university", uni);

        // Cargar el logo actual para mostrarlo
        if (uni.getLogo() != null) {
            model.addAttribute("base64Logo", Base64.getEncoder().encodeToString(uni.getLogo()));
        } else {
            model.addAttribute("base64Logo", null);
        }

        return "admin/edit-university"; // Apunta a la nueva plantilla
    }

    /**
     * Procesa el formulario de EDICIÓN de una universidad.
     */
    @PostMapping("/manage-universities/{id}/edit")
    public String processEditUniversity(
            @PathVariable("id") Integer uniId,
            @ModelAttribute("university") Universidad formData,
            @RequestParam("logoFile") MultipartFile logoFile,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Cargar la universidad existente
            Universidad uniToUpdate = universidadRepository.findById(uniId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Universidad no encontrada"));

            // 2. Actualizar el nombre
            uniToUpdate.setNombre(formData.getNombre());

            // 3. Actualizar el logo SÓLO si se subió uno nuevo
            if (logoFile != null && !logoFile.isEmpty()) {
                uniToUpdate.setLogo(logoFile.getBytes());
            }

            // 4. Guardar
            universidadRepository.save(uniToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Universidad actualizada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/admin/manage-universities";
    }

    /**
     * Elimina una categoría SÓLO si no está en uso.
     */
    @PostMapping("/manage-categories/{id}/delete")
    public String deleteCategory(
            @PathVariable("id") Integer catId,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Verificar si la categoría está siendo usada
            if (proyectoRepository.existsByCategoriaIdCategoria(catId)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Esta categoría no se puede eliminar porque está siendo usada por uno o más proyectos.");
                return "redirect:/admin/manage-categories";
            }

            // 2. Si no está en uso, eliminarla
            categoriaRepository.deleteById(catId);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la categoría: " + e.getMessage());
        }

        return "redirect:/admin/manage-categories";
    }

    /**
     * Muestra el formulario para EDITAR una categoría.
     */
    @GetMapping("/manage-categories/{id}/edit")
    public String showEditCategoryForm(
            @PathVariable("id") Integer catId,
            Model model) {

        Categoria cat = categoriaRepository.findById(catId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        model.addAttribute("categoria", cat);
        return "admin/edit-category"; // Apunta a la nueva plantilla
    }

    /**
     * Procesa el formulario de EDICIÓN de una categoría.
     */
    @PostMapping("/manage-categories/{id}/edit")
    public String processEditCategory(
            @PathVariable("id") Integer catId,
            @ModelAttribute("categoria") Categoria formData,
            RedirectAttributes redirectAttributes) {

        try {
            // 1. Cargar la categoría existente
            Categoria catToUpdate = categoriaRepository.findById(catId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));

            // 2. Actualizar los campos
            catToUpdate.setNombre(formData.getNombre());
            catToUpdate.setDescripcion(formData.getDescripcion());

            // 3. Guardar
            categoriaRepository.save(catToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría actualizada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/admin/manage-categories";
    }

    /**
     * Muestra la página de gestión de Habilidades (para CRUD).
     */
    @GetMapping("/manage-skills")
    public String showManageSkills(
            @RequestParam(required = false) String busqueda,
            Model model) {

        // 1. Cargar todas las habilidades
        List<Habilidad> habilidades = habilidadRepository.findAll();

        // 2. Filtrar por nombre (título)
        List<Habilidad> habilidadesFiltradas = habilidades.stream()
                .filter(h -> (busqueda == null || busqueda.isEmpty()) ||
                        h.getTitulo().toLowerCase().contains(busqueda.toLowerCase()))
                .collect(Collectors.toList());

        // 3. Añadir la lista filtrada y un objeto vacío para el formulario
        model.addAttribute("habilidades", habilidadesFiltradas);
        model.addAttribute("newHabilidad", new Habilidad()); //
        model.addAttribute("busqueda", busqueda);

        return "admin/manage-skills"; // Apunta a la nueva plantilla
    }

    /**
     * Procesa el formulario para añadir una nueva habilidad.
     */
    @PostMapping("/add-skill")
    public String addSkill(
            @ModelAttribute("newHabilidad") Habilidad newHabilidad,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error en los datos del formulario.");
            return "redirect:/admin/manage-skills";
        }

        try {
            // (Podríamos añadir validación de duplicados aquí)
            habilidadRepository.save(newHabilidad);
            redirectAttributes.addFlashAttribute("successMessage", "Habilidad añadida exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            // Capturar error si el título ya existe (UNIQUE)
            if (e.getMessage().contains("ConstraintViolationException")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: La habilidad '" + newHabilidad.getTitulo() + "' ya existe.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error al añadir la habilidad.");
            }
        }

        return "redirect:/admin/manage-skills";
    }

    /**
     * Elimina una habilidad SÓLO si no está en uso por ningún estudiante.
     */
    @PostMapping("/manage-skills/{id}/delete")
    @Transactional // Necesario para cargar la colección 'usuarios'
    public String deleteSkill(
            @PathVariable("id") Integer skillId,
            RedirectAttributes redirectAttributes) {

        try {
            Habilidad hab = habilidadRepository.findById(skillId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidad no encontrada"));

            // 1. Verificación de seguridad: ¿Está esta habilidad en uso?
            if (hab.getUsuarios() != null && !hab.getUsuarios().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: No se puede eliminar la habilidad '" + hab.getTitulo() + "' porque está siendo utilizada por " + hab.getUsuarios().size() + " estudiante(s).");
                return "redirect:/admin/manage-skills";
            }

            // 2. Si no está en uso, eliminarla
            habilidadRepository.delete(hab);
            redirectAttributes.addFlashAttribute("successMessage", "Habilidad eliminada exitosamente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la habilidad: " + e.getMessage());
        }

        return "redirect:/admin/manage-skills";
    }

    /**
     * Muestra el formulario para EDITAR una habilidad.
     */
    @GetMapping("/manage-skills/{id}/edit")
    public String showEditSkillForm(
            @PathVariable("id") Integer skillId,
            Model model) {

        Habilidad hab = habilidadRepository.findById(skillId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidad no encontrada"));

        model.addAttribute("habilidad", hab);
        return "admin/edit-skill"; // Apunta a la nueva plantilla
    }

    /**
     * Procesa el formulario de EDICIÓN de una habilidad.
     */
    @PostMapping("/manage-skills/{id}/edit")
    public String processEditSkill(
            @PathVariable("id") Integer skillId,
            @ModelAttribute("habilidad") Habilidad formData,
            RedirectAttributes redirectAttributes) {

        try {
            Habilidad skillToUpdate = habilidadRepository.findById(skillId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habilidad no encontrada"));

            skillToUpdate.setTitulo(formData.getTitulo());
            skillToUpdate.setDescripcion(formData.getDescripcion());
            habilidadRepository.save(skillToUpdate);

            redirectAttributes.addFlashAttribute("successMessage", "Habilidad actualizada exitosamente.");
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("ConstraintViolationException")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: El título '" + formData.getTitulo() + "' ya existe.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar la habilidad.");
            }
        }
        return "redirect:/admin/manage-skills";
    }

    /**
     * Muestra la página de gestión de Valoraciones (Reseñas) (CON FILTROS).
     */
    @GetMapping("/manage-reviews")
    public String showManageReviews(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Integer calificacion,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            Model model) {

        // 1. Cargar todas las valoraciones con sus detalles
        List<Valoracion> valoraciones = valoracionRepository.findAllWithDetails();

        // 2. Aplicar filtros
        List<Valoracion> valoracionesFiltradas = valoraciones.stream()
                .filter(v -> (busqueda == null || busqueda.isEmpty()) ||
                        (v.getComentario() != null && v.getComentario().toLowerCase().contains(busqueda.toLowerCase())) ||
                        (v.getEmisor() != null && v.getEmisor().getCorreo().toLowerCase().contains(busqueda.toLowerCase())) ||
                        (v.getReceptor() != null && v.getReceptor().getCorreo().toLowerCase().contains(busqueda.toLowerCase())))
                .filter(v -> (calificacion == null || calificacion == 0) ||
                        (v.getCalificacion() != null && v.getCalificacion().equals(calificacion)))
                .filter(v -> (fechaInicio == null || v.getFecha().toLocalDate().isAfter(fechaInicio.minusDays(1))))
                .filter(v -> (fechaFin == null || v.getFecha().toLocalDate().isBefore(fechaFin.plusDays(1))))
                .collect(Collectors.toList());

        // 3. Añadir todo al modelo
        model.addAttribute("valoraciones", valoracionesFiltradas);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("calificacionSeleccionada", calificacion != null ? calificacion : 0);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        // Lista para el dropdown de estrellas
        model.addAttribute("calificaciones", List.of(1, 2, 3, 4, 5));

        return "admin/manage-reviews";
    }

    /**
     * Elimina una valoración (Hard Delete).
     */
    @PostMapping("/manage-reviews/{id}/delete")
    public String deleteReview(
            @PathVariable("id") Integer reviewId,
            RedirectAttributes redirectAttributes) {

        try {
            // A diferencia de otros, este es un borrado permanente (Hard Delete)
            // ya que la tabla 'valoraciones' no tiene estado.
            valoracionRepository.deleteById(reviewId);
            redirectAttributes.addFlashAttribute("successMessage", "Valoración eliminada permanentemente.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar la valoración: " + e.getMessage());
        }

        return "redirect:/admin/manage-reviews";
    }

    /**
     * Muestra la página de gestión de Contratos (CON FILTROS DE FECHA).
     * ¡ACTUALIZADO! Muestra solo los 20 más recientes si no hay filtros.
     */
    @GetMapping("/manage-contracts")
    public String showManageContracts(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double minPago,
            @RequestParam(required = false) Double maxPago,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        // 1. Cargar todos los contratos (esto ya lo teníamos)
        List<Contrato> contratos = contratoRepository.findAllWithDetails();

        // --- ¡INICIO DE LA NUEVA LÓGICA! ---

        // 2. Determinar si hay algún filtro activo
        boolean anyFilterActive = (estado != null && !estado.equals("all")) ||
                minPago != null || maxPago != null ||
                fechaInicio != null || fechaFin != null;

        // 3. Aplicar filtros (la lógica que ya teníamos)
        List<Contrato> contratosFiltrados = contratos.stream()
                .filter(c -> (estado == null || estado.equals("all")) ||
                        c.getEstado().name().equalsIgnoreCase(estado))
                .filter(c -> (c.getTotalPago() != null))
                .filter(c -> (minPago == null || c.getTotalPago().doubleValue() >= minPago))
                .filter(c -> (maxPago == null || c.getTotalPago().doubleValue() <= maxPago))
                .filter(c -> (c.getFechaInicio() != null))
                .filter(c -> (fechaInicio == null || c.getFechaInicio().isAfter(fechaInicio.minusDays(1))))
                .filter(c -> (fechaFin == null || c.getFechaInicio().isBefore(fechaFin.plusDays(1))))
                .collect(Collectors.toList());

        // 4. Decidir qué lista mostrar
        List<Contrato> contratosAMostrar;
        if (anyFilterActive) {
            contratosAMostrar = contratosFiltrados; // Mostrar todos los resultados del filtro
        } else {
            // Si no hay filtros, mostrar los 20 más recientes por fecha de inicio
            contratosAMostrar = contratosFiltrados.stream()
                    .sorted(Comparator.comparing(Contrato::getFechaInicio, Comparator.nullsLast(Comparator.reverseOrder()))) // Ordenar (fecha más nueva primero)
                    .limit(20) // Limitar a 20
                    .collect(Collectors.toList());
        }
        // --- FIN DE LA NUEVA LÓGICA ---

        // 5. Añadir todo al modelo
        model.addAttribute("contratos", contratosAMostrar);
        model.addAttribute("anyFilterActive", anyFilterActive); // ¡NUEVO! (Para el mensaje)
        model.addAttribute("estadosContrato", Contrato.EstadoContrato.values());
        model.addAttribute("estadoSeleccionado", estado != null ? estado : "all");
        model.addAttribute("minPago", minPago);
        model.addAttribute("maxPago", maxPago);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "admin/manage-contracts";
    }

    /**
     * "Elimina" un contrato (Soft Delete) cambiándole el estado a 'cancelado'.
     */
    @PostMapping("/manage-contracts/{id}/cancel")
    public String cancelContract(
            @PathVariable("id") Integer contratoId,
            RedirectAttributes redirectAttributes) {

        try {
            Contrato contrato = contratoRepository.findById(contratoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

            // 1. Cambiar el estado y guardar
            contrato.setEstado(Contrato.EstadoContrato.cancelado); //
            contratoRepository.save(contrato);

            redirectAttributes.addFlashAttribute("successMessage", "Contrato cancelado exitosamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/manage-contracts";
    }

    /**
     * Muestra la vista de "Detalles" para un contrato específico.
     */
    @GetMapping("/manage-contracts/{id}/details")
    public String viewContractDetails(@PathVariable("id") Integer contratoId, Model model) {

        // 1. Cargar el contrato (usamos el método que ya trae todo)
        Contrato contrato = contratoRepository.findAllWithDetails().stream()
                .filter(c -> c.getIdContrato().equals(contratoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado"));

        // 2. Añadir al modelo
        model.addAttribute("contrato", contrato);

        return "admin/contract-details"; // Apunta a la nueva plantilla
    }

    /**
     * Muestra la vista de "Detalles" para un reporte específico.
     */
    @GetMapping("/manage-reports/{id}/details")
    public String viewReportDetails(@PathVariable("id") Integer reporteId, Model model) {

        Reporte reporte = reporteRepository.findByIdWithUsuario(reporteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));

        model.addAttribute("reporte", reporte);
        return "admin/report-details"; // Apunta a la nueva plantilla
    }

    /**
     * Marca un reporte como "resuelto".
     */
    @PostMapping("/manage-reports/{id}/resolve")
    public String resolveReport(
            @PathVariable("id") Integer reporteId,
            RedirectAttributes redirectAttributes) {

        try {
            Reporte reporte = reporteRepository.findById(reporteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));

            // 1. Cambiar el estado y guardar
            reporte.setEstado(Reporte.EstadoReporte.resuelto);
            reporteRepository.save(reporte);

            redirectAttributes.addFlashAttribute("successMessage", "Reporte marcado como resuelto.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el reporte.");
        }

        // Redirigir de vuelta a la lista principal
        return "redirect:/admin/manage-reports/" + reporteId + "/details";
    }

    /**
     * Marca un reporte como "en_revision".
     */
    @PostMapping("/manage-reports/{id}/review")
    public String reviewReport(
            @PathVariable("id") Integer reporteId,
            RedirectAttributes redirectAttributes) {

        try {
            Reporte reporte = reporteRepository.findById(reporteId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporte no encontrado"));

            // 1. Cambiar el estado y guardar
            reporte.setEstado(Reporte.EstadoReporte.en_revision); //
            reporteRepository.save(reporte);

            redirectAttributes.addFlashAttribute("successMessage", "Reporte marcado como 'En Revisión'.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el reporte.");
        }

        // Redirigir de vuelta a la página de detalles
        return "redirect:/admin/manage-reports/" + reporteId + "/details";
    }
}