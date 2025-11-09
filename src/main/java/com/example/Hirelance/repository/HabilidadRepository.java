package com.example.Hirelance.repository;

import com.example.Hirelance.models.Habilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HabilidadRepository extends JpaRepository<Habilidad, Integer> {
    // Método para buscar una habilidad por su nombre (titulo)
    Optional<Habilidad> findByTitulo(String titulo);
}