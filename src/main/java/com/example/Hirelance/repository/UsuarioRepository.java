package com.example.Hirelance.repository;

import com.example.Hirelance.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByTipo(Usuario.TipoUsuario tipo);

    // Método para evitar cargar relaciones problemáticas
    @Query("SELECT u FROM Usuario u WHERE u.tipo = :tipo")
    List<Usuario> findByTipoSimple(@Param("tipo") Usuario.TipoUsuario tipo);

    // 1. Buscar Estudiantes con su perfil (para la lista general)
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfilEstudiante p WHERE u.tipo = 'estudiante'")
    List<Usuario> findAllEstudiantesWithProfile();

    // 2. Buscar Estudiantes por nombre, apellido o carrera
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfilEstudiante p " +
            "WHERE u.tipo = 'estudiante' AND (" +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(p.carrera) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Usuario> findEstudiantesWithProfileBySearch(@Param("busqueda") String busqueda);

    // 3. Buscar Contratistas con su perfil (para la lista general)
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfilContratista p WHERE u.tipo = 'contratista'")
    List<Usuario> findAllContratistasWithProfile();

    // 4. Buscar Contratistas por nombre, apellido o empresa
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfilContratista p " +
            "WHERE u.tipo = 'contratista' AND (" +
            "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
            "LOWER(p.empresa) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    List<Usuario> findContratistasWithProfileBySearch(@Param("busqueda") String busqueda);

    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN FETCH u.perfilEstudiante p " +
            "JOIN u.habilidades h " +
            "WHERE u.tipo = 'estudiante' AND h.titulo = :habilidad")
    List<Usuario> findEstudiantesByHabilidad(@Param("habilidad") String habilidad);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.perfilContratista p " +
            "WHERE u.tipo = 'contratista' AND p.ubicacion = :ubicacion")
    List<Usuario> findContratistasByUbicacion(@Param("ubicacion") String ubicacion);

    /**
     * Carga un usuario por su ID y explícitamente trae (FETCH)
     * sus perfiles, habilidades y universidades para evitar LazyInitializationException.
     */
    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN FETCH u.perfilEstudiante p " +
            "LEFT JOIN FETCH u.habilidades h " +
            "LEFT JOIN FETCH u.universidades uni " +
            "WHERE u.idUsuario = :id")
    Optional<Usuario> findByIdWithFullProfile(@Param("id") Integer id);

    // ... (dentro de public interface UsuarioRepository...)

    // --- AÑADIR ESTE MÉTODO ---
    /**
     * Carga un usuario por su ID y explícitamente trae (FETCH)
     * el perfil de CONTRATISTA y sus ubicaciones.
     */
    @Query("SELECT u FROM Usuario u " +
            "LEFT JOIN FETCH u.perfilContratista pc " +
            "LEFT JOIN FETCH u.ubicaciones loc " +
            "WHERE u.idUsuario = :id")
    Optional<Usuario> findContratistaByIdWithProfile(@Param("id") Integer id);

    /**
     * Devuelve los 5 usuarios registrados más recientemente.
     */
    List<Usuario> findTop5ByOrderByFechaRegistroDesc();

}