package com.example.Hirelance.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // --- PERMITIR ACCESO PÚBLICO A ESTAS RUTAS ---
                        .requestMatchers(
                                "/",
                                "/welcome",
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
                                "/uploads/**"     // ¡IMPORTANTE! Para ver las fotos de perfil subidas
                        ).permitAll()

                        // --- PROTEGER TODAS LAS DEMÁS RUTAS ---
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // --- CONFIGURAR TU PROPIO LOGIN ---
                        .loginPage("/login") // Le dice a Spring cuál es TU página de login
                        .loginProcessingUrl("/login") // La URL a la que el form de login.html debe enviar los datos
                        .defaultSuccessUrl("/welcome", true) // A dónde ir después de un login exitoso
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