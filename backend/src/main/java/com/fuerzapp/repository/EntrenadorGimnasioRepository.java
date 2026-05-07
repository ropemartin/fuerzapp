package com.fuerzapp.repository;

import com.fuerzapp.entity.EntrenadorGimnasio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EntrenadorGimnasioRepository extends JpaRepository<EntrenadorGimnasio, Long> {

    List<EntrenadorGimnasio> findByGimnasioIdAndActivoTrue(Long gimnasioId);

    List<EntrenadorGimnasio> findByGimnasioId(Long gimnasioId);

    @org.springframework.data.jpa.repository.Query("SELECT eg FROM EntrenadorGimnasio eg WHERE eg.entrenador.id = :entrenadorId AND eg.activo = true")
    List<EntrenadorGimnasio> findByEntrenadorIdActivo(@org.springframework.data.repository.query.Param("entrenadorId") Long entrenadorId);

    Optional<EntrenadorGimnasio> findByEntrenadorIdAndGimnasioId(Long entrenadorId, Long gimnasioId);

    boolean existsByEntrenadorIdAndGimnasioId(Long entrenadorId, Long gimnasioId);
}
