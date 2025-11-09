package com.example.Hirelance.repository;

import com.example.Hirelance.models.Universidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UniversidadRepository extends JpaRepository<Universidad, Integer> {
    // Método para buscar una universidad por su nombre
    Optional<Universidad> findByNombre(String nombre);
}