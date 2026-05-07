package com.fuerzapp.repository;

import com.fuerzapp.entity.ClienteSuscripcionExtra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteSuscripcionExtraRepository extends JpaRepository<ClienteSuscripcionExtra, Long> {

    List<ClienteSuscripcionExtra> findByClienteSuscripcionId(Long clienteSuscripcionId);

    boolean existsByClienteSuscripcionIdAndExtraId(Long clienteSuscripcionId, Long extraId);
}
