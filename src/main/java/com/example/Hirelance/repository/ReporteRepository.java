package com.example.Hirelance.repository;

import com.example.Hirelance.models.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    /**
     * Busca TODOS los reportes (para el admin) y carga
     * la info del usuario que lo envió para evitar LazyInitializationException.
     */
    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.usuario")
    List<Reporte> findAllWithUsuario();

    /**
     * Busca todos los reportes enviados por un usuario específico.
     */
    @Query("SELECT r FROM Reporte r WHERE r.usuario.idUsuario = :idUsuario ORDER BY r.fechaReporte DESC")
    List<Reporte> findByUsuarioIdUsuario(@Param("idUsuario") Integer idUsuario);

    /**
     * Busca un reporte por su ID y carga (FETCH) la información del usuario.
     */
    @Query("SELECT r FROM Reporte r LEFT JOIN FETCH r.usuario u WHERE r.idReporte = :id")
    Optional<Reporte> findByIdWithUsuario(@Param("id") Integer id);

    /**
     * Cuenta cuántos reportes tienen un estado específico.
     */
    long countByEstado(Reporte.EstadoReporte estado);

    /**
     * Devuelve los 5 reportes más recientes que están en un estado específico.
     */
    List<Reporte> findTop5ByEstadoOrderByFechaReporteDesc(Reporte.EstadoReporte estado);

    /**
     * Devuelve los 5 reportes enviados más recientemente (de cualquier estado).
     */
    List<Reporte> findTop5ByOrderByFechaReporteDesc();
}


