package com.fuerzapp.repository;

import com.fuerzapp.entity.TipoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoSuscripcionRepository extends JpaRepository<TipoSuscripcion, Long> {

    List<TipoSuscripcion> findByGimnasioIdAndActivoTrue(Long gimnasioId);
}
