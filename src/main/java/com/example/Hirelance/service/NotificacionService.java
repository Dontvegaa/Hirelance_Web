package com.example.Hirelance.service;

import com.example.Hirelance.models.Notificacion;
import com.example.Hirelance.models.Usuario;
import com.example.Hirelance.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    // Método para CREAR una notificación fácilmente desde cualquier controlador
    public void crearNotificacion(Usuario destinatario, String mensaje, Notificacion.TipoNotificacion tipo, String enlace) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(destinatario);
        notificacion.setContenido(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setEnlace(enlace); // <--- Guardamos la URL
        notificacionRepository.save(notificacion);
    }

    // Obtener lista para mostrar
    public List<Notificacion> obtenerMisNotificaciones(Integer idUsuario) {
        return notificacionRepository.findByUsuarioIdUsuarioOrderByFechaDesc(idUsuario);
    }

    // Contar no leídas
    public long contarNoLeidas(Integer idUsuario) {
        return notificacionRepository.countByUsuarioIdUsuarioAndLeidoFalse(idUsuario);
    }

    // Marcar como leída
    public void marcarComoLeida(Integer idNotificacion) {
        Notificacion n = notificacionRepository.findById(idNotificacion).orElse(null);
        if (n != null) {
            n.setLeido(true);
            notificacionRepository.save(n);
        }
    }
}