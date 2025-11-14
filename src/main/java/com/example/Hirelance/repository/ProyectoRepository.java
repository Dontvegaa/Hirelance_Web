package com.example.Hirelance.repository;

import com.example.Hirelance.models.Postulacion;
import com.example.Hirelance.models.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    // Buscar proyectos por título o descripción
    @Query("SELECT p FROM Proyecto p WHERE " +
            "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Proyecto> buscarPorTituloODescripcion(@Param("busqueda") String busqueda);

    @Query("SELECT p FROM Proyecto p WHERE p.categoria.nombre = :categoria")
    List<Proyecto> buscarPorCategoria(@Param("categoria") String categoria);

    // Buscar proyectos por estado
    List<Proyecto> findByEstado(Proyecto.EstadoProyecto estado);

    // Buscar proyectos abiertos
    List<Proyecto> findByEstadoOrderByFechaCreacionDesc(Proyecto.EstadoProyecto estado);

    @Query("SELECT p FROM Proyecto p LEFT JOIN FETCH p.contratista WHERE p.estado = com.example.Hirelance.models.Proyecto.EstadoProyecto.publicado")
    List<Proyecto> findAllWithContratista();

    // Método alternativo si el anterior no funciona
    @Query("SELECT p FROM Proyecto p WHERE p.estado = 'ABIERTO'")
    List<Proyecto> findProyectosAbiertos();

    // --- AÑADIR ESTE MÉTODO ---
    /**
     * Busca todos los proyectos de un contratista específico por su ID de usuario.
     * Ordena por fecha de creación descendente.
     */
    List<Proyecto> findByContratistaIdUsuarioOrderByFechaCreacionDesc(Integer idContratista);

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

    /**
     * Busca TODOS los proyectos (para el admin) y carga
     * la info del contratista y la categoría.
     */
    @Query("SELECT p FROM Proyecto p " +
            "LEFT JOIN FETCH p.contratista " +
            "LEFT JOIN FETCH p.categoria")
    List<Proyecto> findAllWithContratistaAndCategoria();

    /**
     * Verifica si existe algún proyecto asociado a un id_categoria.
     * Esto es para prevenir que se borre una categoría en uso.
     */
    boolean existsByCategoriaIdCategoria(Integer idCategoria);
    /**
     * Devuelve los 5 proyectos creados más recientemente.
     */
    List<Proyecto> findTop5ByOrderByFechaCreacionDesc();

    @Query("SELECT p FROM Proyecto p LEFT JOIN FETCH p.habilidadesRequeridas WHERE p.idProyecto = :id")
    Optional<Proyecto> findByIdWithHabilidades(@Param("id") Integer idProyecto);

    /**
     * Busca proyectos de un contratista, permitiendo filtrar por
     * título de proyecto, estado y rango de presupuesto.
     */
    @Query("SELECT p FROM Proyecto p " +
            "WHERE p.contratista.idUsuario = :idContratista " +
            "AND (:busqueda IS NULL OR p.titulo LIKE %:busqueda%) " +
            "AND (:estado = 'all' OR p.estado = :estadoEnum) " +
            // --- NUEVAS LÍNEAS PARA PRESUPUESTO ---
            "AND (:minBudget IS NULL OR p.presupuesto >= :minBudget) " +
            "AND (:maxBudget IS NULL OR p.presupuesto <= :maxBudget) " +
            // --- FIN DE LÍNEAS NUEVAS ---
            "ORDER BY p.fechaCreacion DESC")
    List<Proyecto> findByContratistaIdAndFilters(
            @Param("idContratista") Integer idContratista,
            @Param("busqueda") String busqueda,
            @Param("estado") String estado,
            @Param("estadoEnum") Proyecto.EstadoProyecto estadoEnum,
            // --- NUEVOS PARÁMETROS ---
            @Param("minBudget") Double minBudget,
            @Param("maxBudget") Double maxBudget);
}