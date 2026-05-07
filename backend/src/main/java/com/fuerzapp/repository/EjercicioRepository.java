package com.fuerzapp.repository;

import com.fuerzapp.entity.Ejercicio;
import com.fuerzapp.enums.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    // Biblioteca global (predefinidos)
    List<Ejercicio> findByEsPredefinidoTrue();

    // Ejercicios propios de un gimnasio (creados por sus entrenadores)
    List<Ejercicio> findByGimnasioId(Long gimnasioId);

    // Todos los ejercicios disponibles para un gimnasio: globales + propios
    @Query("""
            SELECT e FROM Ejercicio e
            WHERE e.esPredefinido = true
            OR e.gimnasio.id = :gimnasioId
            """)
    List<Ejercicio> findDisponiblesByGimnasioId(@Param("gimnasioId") Long gimnasioId);

    // Filtrar por grupo muscular dentro de los disponibles para un gimnasio
    @Query("""
            SELECT e FROM Ejercicio e
            WHERE (e.esPredefinido = true OR e.gimnasio.id = :gimnasioId)
            AND e.grupoMuscular = :grupoMuscular
            """)
    List<Ejercicio> findDisponiblesByGimnasioIdAndGrupoMuscular(
            @Param("gimnasioId") Long gimnasioId,
            @Param("grupoMuscular") GrupoMuscular grupoMuscular
    );
}
