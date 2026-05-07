package com.fuerzapp.service;

import com.fuerzapp.dto.*;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.EstadoPago;
import com.fuerzapp.enums.EstadoSuscripcion;
import com.fuerzapp.repository.*;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final ClienteSuscripcionRepository clienteSuscripcionRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    // ─── Crear sesión de pago en Stripe ──────────────────────────────────────

    @Override
    @Transactional
    public SesionPagoResponse crearSesionPago(CrearSesionPagoRequest request) {
        ClienteSuscripcion suscripcion = clienteSuscripcionRepository
                .findById(request.getClienteSuscripcionId())
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        BigDecimal precio = suscripcion.getTipoSuscripcion().getPrecio();
        String nombreSuscripcion = suscripcion.getTipoSuscripcion().getNombre();
        String nombreGimnasio = suscripcion.getTipoSuscripcion().getGimnasio().getNombre();

        Pago pago = new Pago();
        pago.setClienteSuscripcion(suscripcion);
        pago.setImporte(precio);
        pago.setEstado(EstadoPago.PENDIENTE);
        pagoRepository.save(pago);

        try {
            long importeCentimos = precio.multiply(BigDecimal.valueOf(100)).longValue();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(request.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(request.getCancelUrl())
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("eur")
                                                    .setUnitAmount(importeCentimos)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(nombreSuscripcion + " — " + nombreGimnasio)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .putMetadata("pagoId", pago.getId().toString())
                    .build();

            Session session = Session.create(params);

            pago.setStripePaymentIntentId(session.getPaymentIntent());
            pagoRepository.save(pago);

            return new SesionPagoResponse(session.getUrl(), session.getId());

        } catch (StripeException e) {
            pago.setEstado(EstadoPago.FALLIDO);
            pagoRepository.save(pago);
            throw new RuntimeException("Error al crear la sesión de pago en Stripe: " + e.getMessage());
        }
    }

    // ─── Webhook de Stripe ────────────────────────────────────────────────────

    @Override
    @Transactional
    public void procesarWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Firma del webhook de Stripe inválida");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow(() -> new RuntimeException("No se pudo deserializar el evento de Stripe"));

            String pagoIdStr = session.getMetadata().get("pagoId");
            if (pagoIdStr == null) return;

            Long pagoId = Long.parseLong(pagoIdStr);
            Pago pago = pagoRepository.findById(pagoId)
                    .orElseThrow(() -> new RuntimeException("Pago no encontrado: " + pagoId));

            pago.setEstado(EstadoPago.COMPLETADO);
            pagoRepository.save(pago);

            ClienteSuscripcion suscripcion = pago.getClienteSuscripcion();
            suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
            clienteSuscripcionRepository.save(suscripcion);

            generarFactura(pago);
        }
    }

    // ─── Generación automática de facturas ───────────────────────────────────

    private void generarFactura(Pago pago) {
        if (facturaRepository.findByPagoId(pago.getId()).isPresent()) return;

        int anioActual = LocalDateTime.now().getYear();
        long totalFacturasAnio = facturaRepository.countByAnio(anioActual);
        String numeroFactura = String.format("FAC-%d-%05d", anioActual, totalFacturasAnio + 1);

        Factura factura = new Factura();
        factura.setPago(pago);
        factura.setNumeroFactura(numeroFactura);
        factura.setImporteTotal(pago.getImporte());
        facturaRepository.save(factura);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Override
    public List<PagoResponse> listarPagosPorCliente(Long clienteId) {
        return pagoRepository.findByClienteId(clienteId).stream()
                .map(this::toPagoResponse)
                .toList();
    }

    @Override
    public List<PagoResponse> listarPagosPorGimnasio(Long gimnasioId) {
        return pagoRepository.findByGimnasioId(gimnasioId).stream()
                .map(this::toPagoResponse)
                .toList();
    }

    @Override
    public FacturaResponse obtenerFacturaPorPago(Long pagoId) {
        Factura factura = facturaRepository.findByPagoId(pagoId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada para el pago: " + pagoId));
        return toFacturaResponse(factura);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private PagoResponse toPagoResponse(Pago p) {
        PagoResponse r = new PagoResponse();
        r.setId(p.getId());
        r.setClienteSuscripcionId(p.getClienteSuscripcion().getId());
        r.setClienteNombre(
                p.getClienteSuscripcion().getCliente().getNombre() + " " +
                p.getClienteSuscripcion().getCliente().getApellidos()
        );
        r.setClienteEmail(p.getClienteSuscripcion().getCliente().getEmail());
        r.setTipoSuscripcionNombre(p.getClienteSuscripcion().getTipoSuscripcion().getNombre());
        r.setImporte(p.getImporte());
        r.setFecha(p.getFecha());
        r.setEstado(p.getEstado());

        facturaRepository.findByPagoId(p.getId()).ifPresent(f -> {
            r.setFacturaId(f.getId());
            r.setNumeroFactura(f.getNumeroFactura());
        });

        return r;
    }

    private FacturaResponse toFacturaResponse(Factura f) {
        FacturaResponse r = new FacturaResponse();
        r.setId(f.getId());
        r.setPagoId(f.getPago().getId());
        r.setNumeroFactura(f.getNumeroFactura());
        r.setFechaEmision(f.getFechaEmision());
        r.setImporteTotal(f.getImporteTotal());
        r.setClienteNombre(
                f.getPago().getClienteSuscripcion().getCliente().getNombre() + " " +
                f.getPago().getClienteSuscripcion().getCliente().getApellidos()
        );
        r.setClienteEmail(f.getPago().getClienteSuscripcion().getCliente().getEmail());
        r.setTipoSuscripcionNombre(f.getPago().getClienteSuscripcion().getTipoSuscripcion().getNombre());
        r.setGimnasioNombre(f.getPago().getClienteSuscripcion().getTipoSuscripcion().getGimnasio().getNombre());
        return r;
    }
}
