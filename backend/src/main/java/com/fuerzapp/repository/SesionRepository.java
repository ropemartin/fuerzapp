package com.fuerzapp.repository;

import com.fuerzapp.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

    List<Sesion> findByEntrenamientoIdOrderByFechaHora(Long entrenamientoId);

    // Sesiones futuras de un gimnasio (para el panel del propietario/entrenador)
    List<Sesion> findByEntrenamientoGimnasioIdAndFechaHoraAfterOrderByFechaHora(
            Long gimnasioId, LocalDateTime desde);
}
