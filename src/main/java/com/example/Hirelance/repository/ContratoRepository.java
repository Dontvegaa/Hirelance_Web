package com.example.Hirelance.repository;

import com.example.Hirelance.models.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    /**
     * Busca TODOS los contratos y carga (FETCH) la información del proyecto,
     * estudiante y contratista para el panel de admin.
     */
    @Query("SELECT c FROM Contrato c " +
            "LEFT JOIN FETCH c.proyecto p " +
            "LEFT JOIN FETCH c.estudiante e " +
            "LEFT JOIN FETCH c.contratista co ")
    List<Contrato> findAllWithDetails();
}