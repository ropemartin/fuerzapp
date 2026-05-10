package com.fuerzapp.repository;

import com.fuerzapp.entity.ClienteSuscripcion;
import com.fuerzapp.enums.EstadoSuscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteSuscripcionRepository extends JpaRepository<ClienteSuscripcion, Long> {

    // Usado en asignarSuscripcion para cancelar la anterior (sin filtro de fecha)
    Optional<ClienteSuscripcion> findByClienteIdAndEstado(Long clienteId, EstadoSuscripcion estado);

    // Suscripción activa y vigente de un cliente (para obtener/agregar extras)
    @Query("""
            SELECT cs FROM ClienteSuscripcion cs
            WHERE cs.cliente.id = :clienteId
            AND cs.estado = 'ACTIVA'
            AND cs.fechaFin >= CURRENT_DATE
            """)
    Optional<ClienteSuscripcion> findActivaVigenteByClienteId(@Param("clienteId") Long clienteId);

    // Suscripción activa o pendiente de pago de un cliente (para el panel cliente)
    @Query("""
            SELECT cs FROM ClienteSuscripcion cs
            WHERE cs.cliente.id = :clienteId
            AND cs.estado IN ('ACTIVA', 'PENDIENTE')
            ORDER BY cs.fechaInicio DESC
            """)
    List<ClienteSuscripcion> findActivaOPendienteByClienteId(@Param("clienteId") Long clienteId);

    // Suscripciones activas y vigentes de un gimnasio (para el propietario)
    @Query("""
            SELECT cs FROM ClienteSuscripcion cs
            JOIN cs.tipoSuscripcion ts
            WHERE ts.gimnasio.id = :gimnasioId
            AND cs.estado = 'ACTIVA'
            AND cs.fechaFin >= CURRENT_DATE
            """)
    List<ClienteSuscripcion> findActivasByGimnasioId(@Param("gimnasioId") Long gimnasioId);

    // Todas las suscripciones de un gimnasio
    @Query("""
            SELECT cs FROM ClienteSuscripcion cs
            JOIN cs.tipoSuscripcion ts
            WHERE ts.gimnasio.id = :gimnasioId
            """)
    List<ClienteSuscripcion> findAllByGimnasioId(@Param("gimnasioId") Long gimnasioId);

    // Suscripción activa y vigente de un cliente en un gimnasio concreto (para aviso de baja)
    @Query("""
            SELECT cs FROM ClienteSuscripcion cs
            JOIN cs.tipoSuscripcion ts
            WHERE cs.cliente.id = :clienteId
            AND ts.gimnasio.id = :gimnasioId
            AND cs.estado = 'ACTIVA'
            AND cs.fechaFin >= CURRENT_DATE
            """)
    Optional<ClienteSuscripcion> findActivaByClienteIdAndGimnasioId(
            @Param("clienteId") Long clienteId,
            @Param("gimnasioId") Long gimnasioId);

    // Job de expiración: pone VENCIDA todas las ACTIVA cuya fecha_fin ya pasó
    @Modifying
    @Query("UPDATE ClienteSuscripcion cs SET cs.estado = 'VENCIDA' WHERE cs.estado = 'ACTIVA' AND cs.fechaFin < CURRENT_DATE")
    int expirarVencidas();
}
