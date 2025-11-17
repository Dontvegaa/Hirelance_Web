package com.example.Hirelance.repository;

import com.example.Hirelance.models.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {

    // 1. Obtener historial de chat entre Usuario A y Usuario B (ordenado por fecha)
    @Query("SELECT m FROM Mensaje m WHERE " +
            "(m.emisor.idUsuario = :userId1 AND m.receptor.idUsuario = :userId2) OR " +
            "(m.emisor.idUsuario = :userId2 AND m.receptor.idUsuario = :userId1) " +
            "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findChatHistory(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    // 2. Encontrar todos los mensajes donde participo (para sacar la lista de contactos)
    @Query("SELECT m FROM Mensaje m WHERE m.emisor.idUsuario = :userId OR m.receptor.idUsuario = :userId ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findAllByUserInvolved(@Param("userId") Integer userId);

    // 3. Contar mensajes no leídos de un emisor específico
    long countByReceptorIdUsuarioAndEmisorIdUsuarioAndLeidoFalse(Integer receptorId, Integer emisorId);
}