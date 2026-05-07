package com.fuerzapp.repository;

import com.fuerzapp.entity.SesionCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SesionClienteRepository extends JpaRepository<SesionCliente, Long> {

    List<SesionCliente> findBySesionId(Long sesionId);

    Optional<SesionCliente> findBySesionIdAndClienteId(Long sesionId, Long clienteId);

    boolean existsBySesionIdAndClienteId(Long sesionId, Long clienteId);

    long countBySesionId(Long sesionId);

    // Sesiones en las que está inscrito un cliente
    @Query("""
            SELECT sc FROM SesionCliente sc
            WHERE sc.cliente.id = :clienteId
            ORDER BY sc.sesion.fechaHora ASC
            """)
    List<SesionCliente> findByClienteId(@Param("clienteId") Long clienteId);
}
