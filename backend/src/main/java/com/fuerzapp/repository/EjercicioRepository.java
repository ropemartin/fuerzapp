package com.fuerzapp.repository;

import com.fuerzapp.entity.Ejercicio;
import com.fuerzapp.enums.GrupoMuscular;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    List<Ejercicio> findByGimnasioId(Long gimnasioId);

    List<Ejercicio> findByGimnasioIdAndGrupoMuscular(Long gimnasioId, GrupoMuscular grupoMuscular);
}
