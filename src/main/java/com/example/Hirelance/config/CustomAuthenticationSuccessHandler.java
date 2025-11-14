package com.example.Hirelance.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Obtenemos los "roles" (autoridades) del usuario
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Iteramos sobre los roles (aunque solo esperamos uno)
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("contratista".equals(role)) {
                // ¡Éxito! Redirigir al dashboard del cliente
                response.sendRedirect("/client/dashboard");
                return;
            } else if ("estudiante".equals(role)) {
                // (Para el futuro) Redirigir al dashboard del estudiante
                response.sendRedirect("/student/dashboard"); // ¡La nueva URL del dashboard!
                return;
            } else if ("admin".equals(role)) {
                // (Para el futuro) Redirigir al dashboard de admin
                response.sendRedirect("/admin/dashboard");
                return;
            }
        }

        // Si no es ninguno (o por si acaso), redirigir al inicio
        response.sendRedirect("/");
    }
}