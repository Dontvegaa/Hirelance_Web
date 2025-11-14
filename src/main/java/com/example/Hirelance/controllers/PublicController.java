package com.example.Hirelance.controllers;

import com.example.Hirelance.dto.RegisterDTO;
import com.example.Hirelance.models.*;
import com.example.Hirelance.repository.*;
import com.example.Hirelance.service.UsuarioService; // Importar el servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PublicController {

    // Inyectar el servicio
    @Autowired
    private UsuarioService usuarioService;

    // ¡NUEVO: Inyectar el repositorio de categorías!
    @Autowired
    private CategoriaRepository categoriaRepository;

    // ... (tus otros métodos GET como welcome, explore, login, etc.) ...

    @GetMapping("/")
    public String welcome(Model model) {
        // Cargar categorías desde la base de datos
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        return "public/welcome";
    }

    @GetMapping("/welcome")
    public String welcomePage(Model model) {
        // Cargar categorías desde la base de datos
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);
        return "public/welcome";
    }
    @Autowired
    private PostulacionRepository postulacionRepository;

    @Autowired
    private HabilidadRepository habilidadRepository;

    @GetMapping("/explore")
    public String explore(
            @RequestParam(defaultValue = "proyectos") String tab,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String habilidad,
            @RequestParam(required = false) String ubicacion, // ¡NUEVO!
            @RequestParam(required = false) String busqueda,
            Model model) {

        // Cargar categorías (para pestaña proyectos)
        List<Categoria> categorias = categoriaRepository.findAll();
        model.addAttribute("categorias", categorias);

        // ¡NUEVO! Cargar habilidades (para pestaña estudiantes)
        List<Habilidad> habilidades = habilidadRepository.findAll();
        model.addAttribute("habilidades", habilidades);

        Map<String, List<String>> locationData = getDepartamentosMunicipios();
        model.addAttribute("departamentos", locationData.keySet());

        model.addAttribute("tabActivo", tab);
        model.addAttribute("categoriaSeleccionada", categoria != null ? categoria : "all");
        model.addAttribute("habilidadSeleccionada", habilidad != null ? habilidad : "all"); // ¡NUEVO!
        model.addAttribute("ubicacionSeleccionada", ubicacion != null ? ubicacion : "all");
        model.addAttribute("busqueda", busqueda);

        if ("proyectos".equals(tab)) {
            List<Proyecto> proyectos;
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                proyectos = proyectoRepository.buscarPorTituloODescripcion(busqueda);
            } else if (categoria != null && !categoria.equals("all")) {
                proyectos = proyectoRepository.buscarPorCategoria(categoria);
            } else {
                proyectos = proyectoRepository.findAllWithContratista();
            }
            model.addAttribute("proyectos", proyectos);

        } else if ("estudiantes".equals(tab)) {
            List<Usuario> estudiantes;
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                estudiantes = usuarioRepository.findEstudiantesWithProfileBySearch(busqueda);
            } else if (habilidad != null && !habilidad.equals("all")) {
                estudiantes = usuarioRepository.findEstudiantesByHabilidad(habilidad);
            } else {
                estudiantes = usuarioRepository.findAllEstudiantesWithProfile();
            }

            // --- INICIO DE LA SOLUCIÓN ---
            // Convertir los datos de byte[] a Base64 String aquí
            List<Map<String, Object>> estudiantesVM = new ArrayList<>();
            for (Usuario u : estudiantes) {
                Map<String, Object> vm = new HashMap<>();
                vm.put("usuario", u); // Contiene el objeto Usuario completo
                String base64Img = null;
                if (u.getPerfilEstudiante() != null && u.getPerfilEstudiante().getFotoPerfil() != null) {
                    base64Img = Base64.getEncoder().encodeToString(u.getPerfilEstudiante().getFotoPerfil());
                }
                vm.put("base64Image", base64Img); // Contiene la imagen como String
                estudiantesVM.add(vm);
            }
            model.addAttribute("estudiantes", estudiantesVM); // Pasamos la nueva lista

        } else if ("contratistas".equals(tab)) {
            List<Usuario> contratistas;
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                contratistas = usuarioRepository.findContratistasWithProfileBySearch(busqueda);
            } else if (ubicacion != null && !ubicacion.equals("all")) {
                contratistas = usuarioRepository.findContratistasByUbicacion(ubicacion);
            } else {
                contratistas = usuarioRepository.findAllContratistasWithProfile();
            }

            // --- INICIO DE LA SOLUCIÓN ---
            // Convertir los datos de byte[] a Base64 String aquí
            List<Map<String, Object>> contratistasVM = new ArrayList<>();
            for (Usuario u : contratistas) {
                Map<String, Object> vm = new HashMap<>();
                vm.put("contratista", u); // Contiene el objeto Usuario completo
                String base64Img = null;
                if (u.getPerfilContratista() != null && u.getPerfilContratista().getLogoEmpresa() != null) {
                    base64Img = Base64.getEncoder().encodeToString(u.getPerfilContratista().getLogoEmpresa());
                }
                vm.put("base64Image", base64Img); // Contiene la imagen como String
                contratistasVM.add(vm);
            }
            model.addAttribute("contratistas", contratistasVM); // Pasamos la nueva lista
            // --- FIN DE LA SOLUCIÓN ---
        }
        return "public/explore";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "public/login";
    }

    @GetMapping("/register")
    public String register() {
        return "public/register";
    }

    @GetMapping("/register-estudiante")
    public String registerEstudiante(Model model) {
        // Añadir el DTO al modelo si aún no existe (por si hay un error y se recarga)
        if (!model.containsAttribute("registerDTO")) {
            model.addAttribute("registerDTO", new RegisterDTO());
        }
        return "public/register-estudiante";
    }

    // MÉTODO POST DE ESTUDIANTE (MODIFICADO)
    @PostMapping("/register-estudiante")
    public String processRegisterEstudiante(@ModelAttribute RegisterDTO registerDTO,
                                            @RequestParam(value = "password-confirm", required = false) String passwordConfirm,
                                            @RequestParam(value = "foto_perfil", required = false) MultipartFile fotoPerfil,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {

        // Validar que las contraseñas coincidan
        if (registerDTO.getContrasena() == null || !registerDTO.getContrasena().equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("registerDTO", registerDTO); // Devolver datos al formulario
            return "public/register-estudiante";
        }

        // (Puedes añadir más validaciones de contraseña aquí si quieres)

        try {
            // Llamar al servicio para hacer el trabajo
            usuarioService.registrarEstudiante(registerDTO, fotoPerfil);

            // Redirigir al login con mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            // Manejar errores (ej: correo duplicado)
            System.out.println("Error en registro: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            model.addAttribute("registerDTO", registerDTO); // Devolver datos al formulario
            return "public/register-estudiante";
        }
    }

    @GetMapping("/register-contratista")
    public String registerContratista(Model model) {
        // Añadir el DTO al modelo si aún no existe
        if (!model.containsAttribute("registerDTO")) {
            model.addAttribute("registerDTO", new RegisterDTO());
        }
        Map<String, List<String>> locationData = getDepartamentosMunicipios();
        model.addAttribute("locationData", locationData);
        model.addAttribute("departamentos", locationData.keySet());
        return "public/register-contratista";
    }

    // ¡NUEVO MÉTODO POST PARA CONTRATISTA!
    @PostMapping("/register-contratista")
    public String processRegisterContratista(@ModelAttribute RegisterDTO registerDTO,
                                             @RequestParam(value = "password-confirm", required = false) String passwordConfirm,
                                             @RequestParam(value = "logo_empresa", required = false) MultipartFile logoEmpresa,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {

        // Validar que las contraseñas coincidan
        if (registerDTO.getContrasena() == null || !registerDTO.getContrasena().equals(passwordConfirm)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("registerDTO", registerDTO);
            return "public/register-contratista";
        }

        try {
            // Llamar al servicio
            usuarioService.registrarContratista(registerDTO, logoEmpresa);

            // Redirigir al login con mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Ahora puedes iniciar sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            // Manejar errores
            System.out.println("Error en registro: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("error", "Error en el registro: " + e.getMessage());
            model.addAttribute("registerDTO", registerDTO);
            return "public/register-contratista";
        }
    }


    @GetMapping("/public-profile-estudiante")
    public String publicProfileEstudiante() {
        return "public/public-profile-estudiante";
    }

    @GetMapping("/public-profile-contratista")
    public String publicProfileContratista() {
        return "public/public-profile-contratista";
    }

    @GetMapping("/how-it-works")
    public String howItWorks() {
        // Esto le dice a Spring Boot que busque "how-it-works.html"
        // dentro de la carpeta "templates/public/"
        return "public/how-it-works";
    }

    private Map<String, List<String>> getDepartamentosMunicipios() {
        // Usamos TreeMap para que los departamentos estén ordenados alfabéticamente
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
    // Agrega estas inyecciones al PublicController
    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

}



