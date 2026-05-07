package com.fuerzapp.repository;

import com.fuerzapp.entity.Gimnasio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GimnasioRepository extends JpaRepository<Gimnasio, Long> {

    List<Gimnasio> findByPropietarioId(Long propietarioId);

    List<Gimnasio> findByActivoTrue();
}
