package com.fuerzapp.service;

import com.fuerzapp.dto.PagoResponse;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.EstadoPago;
import com.fuerzapp.enums.EstadoSuscripcion;
import com.fuerzapp.repository.ClienteSuscripcionRepository;
import com.fuerzapp.repository.FacturaRepository;
import com.fuerzapp.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService — tests unitarios")
class PagoServiceTest {

    @Mock private PagoRepository pagoRepository;
    @Mock private FacturaRepository facturaRepository;
    @Mock private ClienteSuscripcionRepository clienteSuscripcionRepository;

    @InjectMocks private PagoServiceImpl pagoService;

    private Gimnasio gimnasio;
    private Usuario cliente;
    private TipoSuscripcion tipoMensual;
    private ClienteSuscripcion suscripcionPendiente;

    @BeforeEach
    void setUp() {
        gimnasio = new Gimnasio();
        gimnasio.setId(1L);
        gimnasio.setNombre("FitGym");
        gimnasio.setPorcentajeIva(21);

        cliente = new Usuario();
        cliente.setId(5L);
        cliente.setNombre("Pedro");
        cliente.setApellidos("Martín");
        cliente.setEmail("pedro@email.com");

        tipoMensual = new TipoSuscripcion();
        tipoMensual.setId(10L);
        tipoMensual.setNombre("Mensual");
        tipoMensual.setPrecio(new BigDecimal("49.99"));
        tipoMensual.setDuracionDias(30);
        tipoMensual.setGimnasio(gimnasio);

        suscripcionPendiente = new ClienteSuscripcion();
        suscripcionPendiente.setId(100L);
        suscripcionPendiente.setCliente(cliente);
        suscripcionPendiente.setTipoSuscripcion(tipoMensual);
        suscripcionPendiente.setFechaInicio(LocalDate.now());
        suscripcionPendiente.setFechaFin(LocalDate.now().plusDays(30));
        suscripcionPendiente.setEstado(EstadoSuscripcion.PENDIENTE);
    }

    // ─── registrarPagoEfectivo ────────────────────────────────────────────────

    @Test
    @DisplayName("pago efectivo: suscripción no encontrada lanza excepción")
    void registrarPagoEfectivo_suscripcionInexistente_lanzaExcepcion() {
        when(clienteSuscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pagoService.registrarPagoEfectivo(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Suscripción no encontrada");

        verify(pagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("pago efectivo: crea el pago con estado COMPLETADO y el importe del tipo")
    void registrarPagoEfectivo_creaPageConEstadoCompletadoEImporteCorrecto() {
        when(clienteSuscripcionRepository.findById(100L)).thenReturn(Optional.of(suscripcionPendiente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(facturaRepository.findByPagoId(any())).thenReturn(Optional.empty());
        when(facturaRepository.countByAnio(anyInt())).thenReturn(0L);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        pagoService.registrarPagoEfectivo(100L);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository).save(captor.capture());
        Pago pagoGuardado = captor.getValue();
        assertThat(pagoGuardado.getEstado()).isEqualTo(EstadoPago.COMPLETADO);
        assertThat(pagoGuardado.getImporte()).isEqualByComparingTo("49.99");
    }

    @Test
    @DisplayName("pago efectivo: activa la suscripción del cliente")
    void registrarPagoEfectivo_activaLaSuscripcion() {
        when(clienteSuscripcionRepository.findById(100L)).thenReturn(Optional.of(suscripcionPendiente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(facturaRepository.findByPagoId(any())).thenReturn(Optional.empty());
        when(facturaRepository.countByAnio(anyInt())).thenReturn(0L);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        pagoService.registrarPagoEfectivo(100L);

        assertThat(suscripcionPendiente.getEstado()).isEqualTo(EstadoSuscripcion.ACTIVA);
        verify(clienteSuscripcionRepository).save(suscripcionPendiente);
    }

    @Test
    @DisplayName("pago efectivo: genera factura con número en formato FAC-YYYY-NNNNN")
    void registrarPagoEfectivo_generaFacturaConFormatoCorrecto() {
        when(clienteSuscripcionRepository.findById(100L)).thenReturn(Optional.of(suscripcionPendiente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(facturaRepository.findByPagoId(any())).thenReturn(Optional.empty());
        when(facturaRepository.countByAnio(anyInt())).thenReturn(4L);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        pagoService.registrarPagoEfectivo(100L);

        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        verify(facturaRepository, atLeastOnce()).save(captor.capture());
        Factura factura = captor.getAllValues().get(0);
        assertThat(factura.getNumeroFactura()).matches("FAC-\\d{4}-\\d{5}");
        assertThat(factura.getNumeroFactura()).endsWith("00005");
    }

    @Test
    @DisplayName("pago efectivo: no genera factura duplicada si ya existe una para ese pago")
    void registrarPagoEfectivo_facturaYaExiste_noGeneraOtra() {
        Factura facturaExistente = new Factura();
        facturaExistente.setId(50L);
        facturaExistente.setNumeroFactura("FAC-2026-00001");

        when(clienteSuscripcionRepository.findById(100L)).thenReturn(Optional.of(suscripcionPendiente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(facturaRepository.findByPagoId(1L)).thenReturn(Optional.of(facturaExistente));

        pagoService.registrarPagoEfectivo(100L);

        verify(facturaRepository, never()).save(any(Factura.class));
    }

    @Test
    @DisplayName("pago efectivo: retorna response con estado COMPLETADO")
    void registrarPagoEfectivo_retornaPagoResponseCorrecto() {
        when(clienteSuscripcionRepository.findById(100L)).thenReturn(Optional.of(suscripcionPendiente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
            Pago p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(facturaRepository.findByPagoId(any())).thenReturn(Optional.empty());
        when(facturaRepository.countByAnio(anyInt())).thenReturn(0L);
        when(facturaRepository.save(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponse response = pagoService.registrarPagoEfectivo(100L);

        assertThat(response.getEstado()).isEqualTo(EstadoPago.COMPLETADO);
        assertThat(response.getImporte()).isEqualByComparingTo("49.99");
        assertThat(response.getClienteNombre()).isEqualTo("Pedro Martín");
    }
}
