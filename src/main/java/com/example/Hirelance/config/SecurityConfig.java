package com.example.Hirelance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // --- PERMITIR ACCESO PÚBLICO A ESTAS RUTAS ---
                        .requestMatchers(
                                "/",
                                "/welcome",
                                "/how-it-works",
                                "/login",
                                "/register",
                                "/register-estudiante",
                                "/register-contratista",
                                "/explore",
                                "/public-profile-estudiante/**", // Permite ver perfiles
                                "/public-profile-contratista/**", // Permite ver perfiles
                                "/static/img/**", // Permite logos e imágenes
                                "/img/**",        // Carpeta de imágenes (si usas otra)
                                "/js/**",         // Tus archivos JavaScript
                                "/css/**",        // Tus archivos CSS
                                "/uploads/**",
                                "/downloads/**"// ¡IMPORTANTE! Para ver las fotos de perfil subidas
                        ).permitAll()

                        // --- PROTEGER TODAS LAS DEMÁS RUTAS ---
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // --- CONFIGURAR TU PROPIO LOGIN ---
                        .loginPage("/login") // Le dice a Spring cuál es TU página de login
                        .loginProcessingUrl("/login") // La URL a la que el form de login.html debe enviar los datos
                        .successHandler(successHandler) // ¡USA TU NUEVO MANEJADOR!
                        .permitAll() // Permite que todos vean tu página de login
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para desloguearse
                        .logoutSuccessUrl("/login?logout") // A dónde ir después de cerrar sesión
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable()); // Deshabilitar CSRF temporalmente para que funcionen los forms POST
        // (Más adelante se debe habilitar y configurar)

        return http.build();
    }
}