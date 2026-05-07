package com.fuerzapp.repository;

import com.fuerzapp.entity.EntrenamientoEjercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrenamientoEjercicioRepository extends JpaRepository<EntrenamientoEjercicio, Long> {

    List<EntrenamientoEjercicio> findByEntrenamientoIdOrderByOrden(Long entrenamientoId);

    void deleteByEntrenamientoId(Long entrenamientoId);
}
