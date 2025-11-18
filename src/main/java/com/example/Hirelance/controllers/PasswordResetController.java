package com.example.Hirelance.controllers;

import com.example.Hirelance.models.PasswordResetToken;
import com.example.Hirelance.models.Usuario;
import com.example.Hirelance.repository.PasswordResetTokenRepository;
import com.example.Hirelance.repository.UsuarioRepository;
import com.example.Hirelance.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Inyectamos la URL base desde application.properties (si la configuraste)
    // Si no la tienes configurada, usa un valor por defecto para que no falle.
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // 1. Mostrar formulario de "Olvidé mi contraseña"
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "public/forgot-password";
    }

    // 2. Procesar el correo enviado
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findByCorreo(email);

        if (usuarioOptional.isEmpty()) {
            // Por seguridad no decimos si el correo no existe
            redirectAttributes.addFlashAttribute("infoMessage", "Si el correo existe, recibirás un enlace.");
            return "redirect:/forgot-password";
        }

        Usuario usuario = usuarioOptional.get();

        // --- INICIO DE LA LÓGICA DE LIMPIEZA ---
        // Buscar si ya existe un token para este usuario y ELIMINARLO
        Optional<PasswordResetToken> tokenExistente = tokenRepository.findByUsuario(usuario);
        if (tokenExistente.isPresent()) {
            tokenRepository.delete(tokenExistente.get());
            // Forzamos que se ejecute el borrado en la DB inmediatamente
            tokenRepository.flush();
        }
        // --- FIN DE LA LÓGICA DE LIMPIEZA ---

        // Ahora sí, generamos el token nuevo
        String token = UUID.randomUUID().toString();
        PasswordResetToken myToken = new PasswordResetToken(token, usuario);
        tokenRepository.save(myToken);

        // Construir URL usando la variable configurada (mejor para Render)
        // Si no configuraste app.base-url, usará localhost:8080 por defecto
        String link = baseUrl + "/reset-password?token=" + token;

        // Enviar correo
        emailService.enviarCorreoRecuperacion(usuario.getCorreo(), link);

        redirectAttributes.addFlashAttribute("infoMessage", "Revisa tu correo para restablecer la contraseña.");
        return "redirect:/forgot-password";
    }

    // 3. Mostrar formulario de cambio de contraseña (valida token)
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().isExpirado()) {
            model.addAttribute("errorMessage", "El enlace es inválido o ha expirado.");
            return "public/login"; // O redirigir a forgot-password
        }

        model.addAttribute("token", token);
        return "public/reset-password";
    }

    // 4. Guardar nueva contraseña
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "public/reset-password";
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().isExpirado()) {
            return "redirect:/forgot-password";
        }

        // Actualizar contraseña
        Usuario usuario = tokenOpt.get().getUsuario();
        usuario.setContrasena(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        // Borrar el token usado para limpieza
        tokenRepository.delete(tokenOpt.get());

        redirectAttributes.addFlashAttribute("success", "¡Contraseña restablecida! Inicia sesión.");
        return "redirect:/login";
    }
}