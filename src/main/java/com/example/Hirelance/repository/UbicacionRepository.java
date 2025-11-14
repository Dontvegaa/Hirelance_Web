// UbicacionRepository.java
package com.example.Hirelance.repository;

import com.example.Hirelance.models.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    // No se necesitan métodos personalizados por ahora
    List<Ubicacion> findByUsuarioIdUsuario(Integer idUsuario);
}