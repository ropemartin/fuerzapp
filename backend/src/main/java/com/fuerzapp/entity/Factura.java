package com.fuerzapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false, unique = true)
    private Pago pago;

    @Column(name = "numero_factura", nullable = false, unique = true)
    private String numeroFactura;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    // ─── Snapshot cliente / suscripción ──────────────────────────────────────

    @Column(name = "cliente_nombre")
    private String clienteNombre;

    @Column(name = "cliente_email")
    private String clienteEmail;

    @Column(name = "tipo_suscripcion_nombre")
    private String tipoSuscripcionNombre;

    // ─── Snapshot gimnasio ────────────────────────────────────────────────────

    @Column(name = "gimnasio_nombre")
    private String gimnasioNombre;

    @Column(name = "gimnasio_direccion")
    private String gimnasioDireccion;

    @Column(name = "gimnasio_telefono")
    private String gimnasioTelefono;

    @Column(name = "gimnasio_email")
    private String gimnasioEmail;

    @Column(name = "gimnasio_logo_url")
    private String gimnasioLogoUrl;

    // ─── Importes e IVA ──────────────────────────────────────────────────────

    @Column(name = "base_imponible", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseImponible;

    @Column(name = "porcentaje_iva", nullable = false)
    private Integer porcentajeIva = 0;

    @Column(name = "importe_iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeIva;

    @Column(name = "importe_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeTotal;

    @Column(name = "pdf_url")
    private String pdfUrl;

    // ─── Líneas de factura ───────────────────────────────────────────────────

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacturaLinea> lineas = new ArrayList<>();
}
