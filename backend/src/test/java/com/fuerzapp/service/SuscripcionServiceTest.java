package com.fuerzapp.service;

import com.fuerzapp.dto.ClienteSuscripcionRequest;
import com.fuerzapp.dto.ClienteSuscripcionResponse;
import com.fuerzapp.dto.TipoSuscripcionRequest;
import com.fuerzapp.dto.TipoSuscripcionResponse;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.EstadoSuscripcion;
import com.fuerzapp.repository.*;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionService — tests unitarios")
class SuscripcionServiceTest {

    @Mock private TipoSuscripcionRepository tipoSuscripcionRepository;
    @Mock private ExtraRepository extraRepository;
    @Mock private ClienteSuscripcionRepository clienteSuscripcionRepository;
    @Mock private ClienteSuscripcionExtraRepository clienteSuscripcionExtraRepository;
    @Mock private GimnasioRepository gimnasioRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private SuscripcionServiceImpl suscripcionService;

    private Gimnasio gimnasio;
    private Usuario cliente;
    private TipoSuscripcion tipoMensual;
    private ClienteSuscripcion suscripcionActiva;

    @BeforeEach
    void setUp() {
        gimnasio = new Gimnasio();
        gimnasio.setId(1L);
        gimnasio.setNombre("FitGym");

        cliente = new Usuario();
        cliente.setId(5L);
        cliente.setNombre("Pedro");
        cliente.setApellidos("Martín");
        cliente.setEmail("pedro@email.com");

        tipoMensual = new TipoSuscripcion();
        tipoMensual.setId(10L);
        tipoMensual.setNombre("Mensual");
        tipoMensual.setDescripcion("Acceso mensual completo");
        tipoMensual.setPrecio(new BigDecimal("49.99"));
        tipoMensual.setDuracionDias(30);
        tipoMensual.setActivo(true);
        tipoMensual.setGimnasio(gimnasio);

        suscripcionActiva = new ClienteSuscripcion();
        suscripcionActiva.setId(100L);
        suscripcionActiva.setCliente(cliente);
        suscripcionActiva.setTipoSuscripcion(tipoMensual);
        suscripcionActiva.setFechaInicio(LocalDate.now().minusDays(10));
        suscripcionActiva.setFechaFin(LocalDate.now().plusDays(20));
        suscripcionActiva.setEstado(EstadoSuscripcion.ACTIVA);
    }

    // ─── crearTipo ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear tipo: guarda con los datos correctos")
    void crearTipo_datosValidos_guardaYRetornaResponse() {
        TipoSuscripcionRequest request = new TipoSuscripcionRequest();
        request.setNombre("Trimestral");
        request.setDescripcion("Acceso 3 meses");
        request.setPrecio(new BigDecimal("119.99"));
        request.setDuracionDias(90);

        TipoSuscripcion guardado = new TipoSuscripcion();
        guardado.setId(20L);
        guardado.setNombre("Trimestral");
        guardado.setDescripcion("Acceso 3 meses");
        guardado.setPrecio(new BigDecimal("119.99"));
        guardado.setDuracionDias(90);
        guardado.setActivo(true);
        guardado.setGimnasio(gimnasio);

        when(gimnasioRepository.findById(1L)).thenReturn(Optional.of(gimnasio));
        when(tipoSuscripcionRepository.save(any(TipoSuscripcion.class))).thenReturn(guardado);

        TipoSuscripcionResponse response = suscripcionService.crearTipo(1L, request);

        assertThat(response.getNombre()).isEqualTo("Trimestral");
        assertThat(response.getDuracionDias()).isEqualTo(90);
        assertThat(response.getPrecio()).isEqualByComparingTo("119.99");

        ArgumentCaptor<TipoSuscripcion> captor = ArgumentCaptor.forClass(TipoSuscripcion.class);
        verify(tipoSuscripcionRepository).save(captor.capture());
        assertThat(captor.getValue().getGimnasio()).isEqualTo(gimnasio);
    }

    @Test
    @DisplayName("crear tipo: gimnasio inexistente lanza excepción")
    void crearTipo_gimnasioInexistente_lanzaExcepcion() {
        when(gimnasioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> suscripcionService.crearTipo(99L, new TipoSuscripcionRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Gimnasio no encontrado");
    }

    // ─── asignarSuscripcion ───────────────────────────────────────────────────

    @Test
    @DisplayName("asignar suscripción: cancela la suscripción activa previa antes de asignar")
    void asignarSuscripcion_conSuscripcionActiva_cancelaPreviaYAsignaUeva() {
        ClienteSuscripcionRequest request = new ClienteSuscripcionRequest();
        request.setTipoSuscripcionId(10L);
        request.setFechaInicio(LocalDate.now());

        ClienteSuscripcion nuevaSuscripcion = new ClienteSuscripcion();
        nuevaSuscripcion.setId(200L);
        nuevaSuscripcion.setCliente(cliente);
        nuevaSuscripcion.setTipoSuscripcion(tipoMensual);
        nuevaSuscripcion.setFechaInicio(LocalDate.now());
        nuevaSuscripcion.setFechaFin(LocalDate.now().plusDays(30));
        nuevaSuscripcion.setEstado(EstadoSuscripcion.PENDIENTE);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(cliente));
        when(tipoSuscripcionRepository.findById(10L)).thenReturn(Optional.of(tipoMensual));
        when(clienteSuscripcionRepository.findByClienteIdAndEstado(5L, EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.of(suscripcionActiva));
        when(clienteSuscripcionRepository.save(any(ClienteSuscripcion.class))).thenReturn(nuevaSuscripcion);
        when(clienteSuscripcionExtraRepository.findByClienteSuscripcionId(any())).thenReturn(List.of());

        suscripcionService.asignarSuscripcion(5L, request);

        // Verifica que la suscripción anterior fue cancelada
        assertThat(suscripcionActiva.getEstado()).isEqualTo(EstadoSuscripcion.CANCELADA);
        verify(clienteSuscripcionRepository, atLeastOnce()).save(suscripcionActiva);
    }

    @Test
    @DisplayName("asignar suscripción: sin suscripción previa no intenta cancelar nada")
    void asignarSuscripcion_sinSuscripcionPrevia_asignaDirectamente() {
        ClienteSuscripcionRequest request = new ClienteSuscripcionRequest();
        request.setTipoSuscripcionId(10L);
        request.setFechaInicio(LocalDate.now());

        ClienteSuscripcion nueva = new ClienteSuscripcion();
        nueva.setId(201L);
        nueva.setCliente(cliente);
        nueva.setTipoSuscripcion(tipoMensual);
        nueva.setFechaInicio(LocalDate.now());
        nueva.setFechaFin(LocalDate.now().plusDays(30));
        nueva.setEstado(EstadoSuscripcion.PENDIENTE);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(cliente));
        when(tipoSuscripcionRepository.findById(10L)).thenReturn(Optional.of(tipoMensual));
        when(clienteSuscripcionRepository.findByClienteIdAndEstado(5L, EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());
        when(clienteSuscripcionRepository.save(any(ClienteSuscripcion.class))).thenReturn(nueva);
        when(clienteSuscripcionExtraRepository.findByClienteSuscripcionId(any())).thenReturn(List.of());

        ClienteSuscripcionResponse response = suscripcionService.asignarSuscripcion(5L, request);

        assertThat(response).isNotNull();
        // Solo se guarda la nueva (no la anterior)
        verify(clienteSuscripcionRepository, times(1)).save(any(ClienteSuscripcion.class));
    }

    @Test
    @DisplayName("asignar suscripción: calcula fecha fin según duracion del tipo")
    void asignarSuscripcion_calculaFechaFinCorrecta() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        ClienteSuscripcionRequest request = new ClienteSuscripcionRequest();
        request.setTipoSuscripcionId(10L);
        request.setFechaInicio(inicio);

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(cliente));
        when(tipoSuscripcionRepository.findById(10L)).thenReturn(Optional.of(tipoMensual));
        when(clienteSuscripcionRepository.findByClienteIdAndEstado(5L, EstadoSuscripcion.ACTIVA))
                .thenReturn(Optional.empty());

        ArgumentCaptor<ClienteSuscripcion> captor = ArgumentCaptor.forClass(ClienteSuscripcion.class);
        when(clienteSuscripcionRepository.save(captor.capture())).thenAnswer(inv -> {
            ClienteSuscripcion cs = inv.getArgument(0);
            cs.setId(300L);
            return cs;
        });
        when(clienteSuscripcionExtraRepository.findByClienteSuscripcionId(any())).thenReturn(List.of());

        suscripcionService.asignarSuscripcion(5L, request);

        ClienteSuscripcion guardada = captor.getValue();
        assertThat(guardada.getFechaFin()).isEqualTo(inicio.plusDays(30));
        assertThat(guardada.getEstado()).isEqualTo(EstadoSuscripcion.PENDIENTE);
    }

    // ─── obtenerSuscripcionCliente ────────────────────────────────────────────

    @Test
    @DisplayName("obtener suscripción: sin suscripción activa vigente lanza excepción")
    void obtenerSuscripcionCliente_sinSuscripcionActiva_lanzaExcepcion() {
        when(clienteSuscripcionRepository.findActivaVigenteByClienteId(5L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> suscripcionService.obtenerSuscripcionCliente(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no tiene una suscripción activa");
    }

    // ─── agregarExtra ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("agregar extra: extra ya contratado lanza excepción")
    void agregarExtra_duplicado_lanzaExcepcion() {
        Extra extra = new Extra();
        extra.setId(7L);

        when(clienteSuscripcionRepository.findActivaVigenteByClienteId(5L))
                .thenReturn(Optional.of(suscripcionActiva));
        when(clienteSuscripcionExtraRepository
                .existsByClienteSuscripcionIdAndExtraId(100L, 7L))
                .thenReturn(true);

        assertThatThrownBy(() -> suscripcionService.agregarExtra(5L, 7L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene este extra contratado");

        verify(clienteSuscripcionExtraRepository, never()).save(any());
    }

    @Test
    @DisplayName("agregar extra: sin suscripción activa vigente lanza excepción")
    void agregarExtra_sinSuscripcionActiva_lanzaExcepcion() {
        when(clienteSuscripcionRepository.findActivaVigenteByClienteId(5L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> suscripcionService.agregarExtra(5L, 7L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no tiene una suscripción activa");
    }
}
