package com.fuerzapp.repository;

import com.fuerzapp.entity.EntrenamientoEspecifico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntrenamientoEspecificoRepository extends JpaRepository<EntrenamientoEspecifico, Long> {

    List<EntrenamientoEspecifico> findByClienteIdOrderByFechaAsignacionDesc(Long clienteId);

    List<EntrenamientoEspecifico> findByEntrenamientoGimnasioId(Long gimnasioId);
}
