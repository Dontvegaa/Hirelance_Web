package com.example.Hirelance.repository;

import com.example.Hirelance.models.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Integer> {

    // Contratos del Estudiante (ordenados por fecha inicio, más reciente primero)
    List<Contrato> findByEstudianteIdUsuarioOrderByFechaInicioDesc(Integer idEstudiante);

    // Contratos del Contratista
    List<Contrato> findByContratistaIdUsuarioOrderByFechaInicioDesc(Integer idContratista);

    // Verificar si ya existe contrato para un proyecto (para evitar duplicados)
    boolean existsByProyectoIdProyecto(Integer idProyecto);

    // Agrega esto dentro de public interface ContratoRepository ... {

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Contrato c LEFT JOIN FETCH c.proyecto LEFT JOIN FETCH c.estudiante LEFT JOIN FETCH c.contratista ORDER BY c.fechaInicio DESC")
    List<Contrato> findAllWithDetails();

    // }
}