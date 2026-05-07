package com.fuerzapp.repository;

import com.fuerzapp.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findByPagoId(Long pagoId);

    // Para generar el número de factura correlativo por año
    @Query("""
            SELECT COUNT(f) FROM Factura f
            WHERE YEAR(f.fechaEmision) = :anio
            """)
    long countByAnio(@Param("anio") int anio);
}
