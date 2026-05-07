package com.fuerzapp.repository;

import com.fuerzapp.entity.Extra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtraRepository extends JpaRepository<Extra, Long> {

    List<Extra> findByGimnasioIdAndActivoTrue(Long gimnasioId);
}
