package com.fuerzapp.repository;

import com.fuerzapp.entity.Entrenamiento;
import com.fuerzapp.enums.TipoEntrenamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {

    List<Entrenamiento> findByGimnasioIdAndActivoTrue(Long gimnasioId);

    List<Entrenamiento> findByGimnasioIdAndTipoAndActivoTrue(Long gimnasioId, TipoEntrenamiento tipo);

    List<Entrenamiento> findByEntrenadorIdAndActivoTrue(Long entrenadorId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT e FROM Entrenamiento e " +
        "WHERE e.gimnasio.id = :gimnasioId " +
        "AND e.entrenador.id = :entrenadorId " +
        "AND e.activo = true " +
        "AND EXISTS (SELECT s FROM Sesion s WHERE s.entrenamiento = e AND s.fechaHora > CURRENT_TIMESTAMP)")
    List<Entrenamiento> findActivosConSesionesFuturas(
        @org.springframework.data.repository.query.Param("gimnasioId") Long gimnasioId,
        @org.springframework.data.repository.query.Param("entrenadorId") Long entrenadorId);
}
