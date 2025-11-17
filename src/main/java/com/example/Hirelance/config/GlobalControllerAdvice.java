package com.example.Hirelance.config;

import com.example.Hirelance.models.Usuario;
import com.example.Hirelance.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificacionService notificacionService;

    @ModelAttribute("contadorNotificaciones")
    public Long agregarContadorNotificaciones(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return notificacionService.contarNoLeidas(userDetails.getUsuario().getIdUsuario());
        }
        return 0L;
    }
    // También inyectamos el usuario logueado globalmente (muy útil)
    @ModelAttribute("usuarioLogueado")
    public Usuario agregarUsuarioLogueado(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsuario() : null;
    }

}