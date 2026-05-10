package com.fuerzapp.repository;

import com.fuerzapp.entity.Pago;
import com.fuerzapp.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Historial de pagos de un cliente
    @Query("""
            SELECT p FROM Pago p
            WHERE p.clienteSuscripcion.cliente.id = :clienteId
            ORDER BY p.fecha DESC
            """)
    List<Pago> findByClienteId(@Param("clienteId") Long clienteId);

    // Pagos de un gimnasio (para el propietario)
    @Query("""
            SELECT p FROM Pago p
            WHERE p.clienteSuscripcion.tipoSuscripcion.gimnasio.id = :gimnasioId
            ORDER BY p.fecha DESC
            """)
    List<Pago> findByGimnasioId(@Param("gimnasioId") Long gimnasioId);

    Optional<Pago> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<Pago> findByClienteSuscripcionIdAndEstado(Long clienteSuscripcionId, EstadoPago estado);

    Optional<Pago> findFirstByClienteSuscripcionIdAndEstado(Long clienteSuscripcionId, EstadoPago estado);
}
