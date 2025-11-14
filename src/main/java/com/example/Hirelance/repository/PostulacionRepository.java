package com.example.Hirelance.repository;

import com.example.Hirelance.models.Postulacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.Param; // ¡AÑADE ESTA LÍNEA!
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostulacionRepository extends JpaRepository<Postulacion, Integer> {

    // Buscar postulaciones por estudiante
    List<Postulacion> findByEstudianteIdUsuario(Integer idEstudiante);

    // Buscar postulaciones por proyecto
    List<Postulacion> findByProyectoIdProyecto(Integer idProyecto);

    // Verificar si un estudiante ya se postuló a un proyecto
    boolean existsByEstudianteIdUsuarioAndProyectoIdProyecto(Integer idEstudiante, Integer idProyecto);


    @Query("SELECT p FROM Postulacion p LEFT JOIN FETCH p.estudiante LEFT JOIN FETCH p.proyecto")
    List<Postulacion> findAllWithEstudianteAndProyecto();

    /**
     * Busca todas las postulaciones para un ID de proyecto específico,
     * e incluye (FETCH) la información del estudiante y el proyecto
     * para evitar carga perezosa.
     */
    @Query("SELECT p FROM Postulacion p " +
            "LEFT JOIN FETCH p.estudiante e " +
            "LEFT JOIN FETCH p.proyecto pr " +
            "WHERE pr.idProyecto = :idProyecto")
    List<Postulacion> findAllByProyectoIdWithEstudiante(@Param("idProyecto") Integer idProyecto);

    /**
     * Busca todas las postulaciones recibidas por un CONTRATISTA específico,
     * a través de sus proyectos. Las ordena por fecha descendente.
     */
    @Query("SELECT p FROM Postulacion p " +
            "LEFT JOIN FETCH p.estudiante e " +
            "LEFT JOIN FETCH p.proyecto pr " +
            "WHERE pr.contratista.idUsuario = :idContratista " +
            "ORDER BY p.fechaPostulacion DESC")
    List<Postulacion> findAllByContratistaId(@Param("idContratista") Integer idContratista);

    /**
     * Busca todas las postulaciones de un estudiante por su ID,
     * cargando también la información del Proyecto asociado.
     * Ordena por fecha descendente.
     */
    @Query("SELECT p FROM Postulacion p " +
            "LEFT JOIN FETCH p.proyecto pr " +
            "WHERE p.estudiante.idUsuario = :idEstudiante " +
            "ORDER BY p.fechaPostulacion DESC")
    List<Postulacion> findAllByEstudianteIdWithProyecto(@Param("idEstudiante") Integer idEstudiante);

}