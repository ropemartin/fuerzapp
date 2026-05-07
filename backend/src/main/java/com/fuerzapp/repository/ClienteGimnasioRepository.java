package com.fuerzapp.repository;

import com.fuerzapp.entity.ClienteGimnasio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteGimnasioRepository extends JpaRepository<ClienteGimnasio, Long> {

    List<ClienteGimnasio> findByGimnasioIdAndActivoTrue(Long gimnasioId);

    List<ClienteGimnasio> findByClienteIdAndActivoTrue(Long clienteId);

    Optional<ClienteGimnasio> findByClienteIdAndGimnasioId(Long clienteId, Long gimnasioId);

    boolean existsByClienteIdAndGimnasioId(Long clienteId, Long gimnasioId);

    List<ClienteGimnasio> findByGimnasioId(Long gimnasioId);
}
