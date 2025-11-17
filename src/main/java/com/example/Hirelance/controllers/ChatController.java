package com.example.Hirelance.controllers;

import com.example.Hirelance.config.CustomUserDetails;
import com.example.Hirelance.dto.MensajeDTO;
import com.example.Hirelance.models.*;
import com.example.Hirelance.repository.*;
import com.example.Hirelance.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private MensajeRepository mensajeRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private NotificacionService notificacionService;

    @GetMapping(value = {"", "/{id}"})
    public String showChatPage(
            @PathVariable(required = false) Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {

        Usuario usuarioActual = userDetails.getUsuario();

        // --- 1. Lógica de la Barra Lateral (Contactos) ---
        List<Mensaje> todosMisMensajes = mensajeRepository.findAllByUserInvolved(usuarioActual.getIdUsuario());
        Set<Integer> procesados = new HashSet<>();
        List<Map<String, Object>> conversaciones = new ArrayList<>();

        for (Mensaje m : todosMisMensajes) {
            Usuario otro = m.getEmisor().getIdUsuario().equals(usuarioActual.getIdUsuario()) ? m.getReceptor() : m.getEmisor();

            if (!procesados.contains(otro.getIdUsuario())) {
                procesados.add(otro.getIdUsuario());
                Map<String, Object> map = new HashMap<>();
                map.put("usuario", otro);
                map.put("ultimoMensaje", m.getContenido());
                map.put("fecha", m.getFechaEnvio()); // Agregamos la fecha también por si acaso

                // Foto
                String base64 = null;
                if(otro.getPerfilEstudiante() != null && otro.getPerfilEstudiante().getFotoPerfil() != null)
                    base64 = Base64.getEncoder().encodeToString(otro.getPerfilEstudiante().getFotoPerfil());
                else if(otro.getPerfilContratista() != null && otro.getPerfilContratista().getLogoEmpresa() != null)
                    base64 = Base64.getEncoder().encodeToString(otro.getPerfilContratista().getLogoEmpresa());
                map.put("foto", base64);

                // --- ¡ESTA ES LA PARTE QUE FALTABA! ---
                long noLeidos = mensajeRepository.countByReceptorIdUsuarioAndEmisorIdUsuarioAndLeidoFalse(usuarioActual.getIdUsuario(), otro.getIdUsuario());
                map.put("noLeidos", noLeidos);
                // --------------------------------------

                conversaciones.add(map);
            }
        }
        model.addAttribute("conversaciones", conversaciones);
        model.addAttribute("usuarioActual", usuarioActual);
        model.addAttribute("tipoUsuario", usuarioActual.getTipo().name());

        // --- 2. Lógica del Chat Activo (Derecha) ---
        if (id != null) {
            Usuario chatActivoUsuario = usuarioRepository.findById(id).orElse(null);
            if (chatActivoUsuario != null) {
                List<Mensaje> historial = mensajeRepository.findChatHistory(usuarioActual.getIdUsuario(), id);

                // Marcar como leídos
                historial.stream()
                        .filter(m -> m.getReceptor().getIdUsuario().equals(usuarioActual.getIdUsuario()) && !m.isLeido())
                        .forEach(m -> { m.setLeido(true); mensajeRepository.save(m); });

                model.addAttribute("chatActivo", chatActivoUsuario);
                model.addAttribute("historial", historial);

                MensajeDTO dto = new MensajeDTO();
                dto.setReceptorId(id);
                model.addAttribute("mensajeDTO", dto);
            }
        }

        if (usuarioActual.getTipo().name().equals("contratista")) {
            return "client/chat";
        } else {
            return "student/chat";
        }
    }

    @PostMapping("/send")
    public String sendMessage(@ModelAttribute MensajeDTO mensajeDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Usuario emisor = userDetails.getUsuario();
        Usuario receptor = usuarioRepository.findById(mensajeDTO.getReceptorId()).orElseThrow();

        Mensaje m = new Mensaje();
        m.setEmisor(emisor);
        m.setReceptor(receptor);
        m.setContenido(mensajeDTO.getContenido());
        mensajeRepository.save(m);

        // Notificar
        String urlChat = "/chat/" + emisor.getIdUsuario();
        notificacionService.crearNotificacion(receptor, "Nuevo mensaje de " + emisor.getNombre(), Notificacion.TipoNotificacion.mensaje, urlChat);

        return "redirect:/chat/" + receptor.getIdUsuario();
    }
}