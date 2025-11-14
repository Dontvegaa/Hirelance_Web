package com.example.Hirelance.repository;

import com.example.Hirelance.models.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Integer> {

    /**
     * Busca TODAS las valoraciones y carga (FETCH) la información del emisor,
     * receptor y proyecto para el panel de admin.
     */
    @Query("SELECT v FROM Valoracion v " +
            "LEFT JOIN FETCH v.emisor " +
            "LEFT JOIN FETCH v.receptor " +
            "LEFT JOIN FETCH v.proyecto")
    List<Valoracion> findAllWithDetails();
}