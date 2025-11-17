package com.example.Hirelance.repository;

import com.example.Hirelance.models.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    // Obtener notificaciones de un usuario ordenadas por las más nuevas
    List<Notificacion> findByUsuarioIdUsuarioOrderByFechaDesc(Integer idUsuario);

    // Contar cuántas no leídas tiene (para el numerito rojo en la campana)
    long countByUsuarioIdUsuarioAndLeidoFalse(Integer idUsuario);
}