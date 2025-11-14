package com.example.Hirelance.config;

import com.example.Hirelance.models.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private Usuario usuario; // ¡Aquí guardamos tu objeto Usuario!

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    // --- Métodos de UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(usuario.getTipo().name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo(); // Usamos el correo como username
    }

    // --- Estos los ponemos como 'true' por ahora ---
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Podrías usar tu campo 'estado' aquí si quisieras
        return usuario.getEstado() == Usuario.EstadoUsuario.activo;
    }

    // --- ¡MÉTODO CLAVE! ---
    // Este método nos permitirá obtener el objeto Usuario completo
    public Usuario getUsuario() {
        return this.usuario;
    }
}