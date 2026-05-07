package com.fuerzapp.service;

import com.fuerzapp.dto.AsignarClienteRequest;
import com.fuerzapp.dto.EntrenamientoEjercicioRequest;
import com.fuerzapp.dto.EntrenamientoEjercicioResponse;
import com.fuerzapp.dto.SesionRequest;
import com.fuerzapp.entity.*;
import com.fuerzapp.enums.TipoEntrenamiento;
import com.fuerzapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EntrenamientoService — tests unitarios")
class EntrenamientoServiceTest {

    @Mock private EntrenamientoRepository entrenamientoRepository;
    @Mock private EntrenamientoEjercicioRepository entrenamientoEjercicioRepository;
    @Mock private SesionRepository sesionRepository;
    @Mock private SesionClienteRepository sesionClienteRepository;
    @Mock private EntrenamientoEspecificoRepository entrenamientoEspecificoRepository;
    @Mock private GimnasioRepository gimnasioRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EjercicioRepository ejercicioRepository;

    @InjectMocks private EntrenamientoServiceImpl entrenamientoService;

    private Gimnasio gimnasio;
    private Usuario entrenador;
    private Usuario cliente;
    private Entrenamiento entrenamientoGrupal;
    private Entrenamiento entrenamientoIndividual;
    private Entrenamiento entrenamientoEspecifico;
    private Sesion sesionGrupal;

    @BeforeEach
    void setUp() {
        gimnasio = new Gimnasio();
        gimnasio.setId(1L);
        gimnasio.setNombre("FitGym");

        entrenador = new Usuario();
        entrenador.setId(2L);
        entrenador.setNombre("Ana");
        entrenador.setApellidos("García");
        entrenador.setEmail("ana@fitgym.com");

        cliente = new Usuario();
        cliente.setId(3L);
        cliente.setNombre("Pedro");
        cliente.setApellidos("Martín");
        cliente.setEmail("pedro@email.com");

        entrenamientoGrupal = new Entrenamiento();
        entrenamientoGrupal.setId(10L);
        entrenamientoGrupal.setNombre("Crossfit Lunes");
        entrenamientoGrupal.setTipo(TipoEntrenamiento.GRUPAL);
        entrenamientoGrupal.setGimnasio(gimnasio);
        entrenamientoGrupal.setEntrenador(entrenador);
        entrenamientoGrupal.setActivo(true);

        entrenamientoIndividual = new Entrenamiento();
        entrenamientoIndividual.setId(11L);
        entrenamientoIndividual.setNombre("Personal Pedro");
        entrenamientoIndividual.setTipo(TipoEntrenamiento.INDIVIDUAL);
        entrenamientoIndividual.setGimnasio(gimnasio);
        entrenamientoIndividual.setEntrenador(entrenador);
        entrenamientoIndividual.setActivo(true);

        entrenamientoEspecifico = new Entrenamiento();
        entrenamientoEspecifico.setId(12L);
        entrenamientoEspecifico.setNombre("Plan específico Pedro");
        entrenamientoEspecifico.setTipo(TipoEntrenamiento.ESPECIFICO);
        entrenamientoEspecifico.setGimnasio(gimnasio);
        entrenamientoEspecifico.setEntrenador(entrenador);
        entrenamientoEspecifico.setActivo(true);

        sesionGrupal = new Sesion();
        sesionGrupal.setId(20L);
        sesionGrupal.setEntrenamiento(entrenamientoGrupal);
        sesionGrupal.setFechaHora(LocalDateTime.now().plusDays(1));
        sesionGrupal.setCapacidadMaxima(10);
    }

    // ─── crearSesion ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear sesión: entrenamiento ESPECIFICO lanza excepción")
    void crearSesion_tipoEspecifico_lanzaExcepcion() {
        when(entrenamientoRepository.findById(12L)).thenReturn(Optional.of(entrenamientoEspecifico));

        assertThatThrownBy(() -> entrenamientoService.crearSesion(12L, new SesionRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ESPECIFICO no tienen sesiones con horario");

        verify(sesionRepository, never()).save(any());
    }

    @Test
    @DisplayName("crear sesión: entrenamiento GRUPAL permite crear sesión")
    void crearSesion_tipoGrupal_creaCorrectamente() {
        SesionRequest request = new SesionRequest();
        request.setFechaHora(LocalDateTime.now().plusDays(3));
        request.setDuracionMinutos(60);
        request.setUbicacion("Sala 1");
        request.setCapacidadMaxima(15);

        Sesion sesionGuardada = new Sesion();
        sesionGuardada.setId(30L);
        sesionGuardada.setEntrenamiento(entrenamientoGrupal);
        sesionGuardada.setFechaHora(request.getFechaHora());
        sesionGuardada.setDuracionMinutos(60);
        sesionGuardada.setUbicacion("Sala 1");
        sesionGuardada.setCapacidadMaxima(15);

        when(entrenamientoRepository.findById(10L)).thenReturn(Optional.of(entrenamientoGrupal));
        when(sesionRepository.save(any(Sesion.class))).thenReturn(sesionGuardada);
        when(sesionClienteRepository.countBySesionId(30L)).thenReturn(0L);
        when(sesionClienteRepository.findBySesionId(30L)).thenReturn(List.of());

        var response = entrenamientoService.crearSesion(10L, request);

        assertThat(response).isNotNull();
        verify(sesionRepository).save(any(Sesion.class));
    }

    // ─── inscribirseEnSesion ──────────────────────────────────────────────────

    @Test
    @DisplayName("inscribirse: ya inscrito lanza excepción")
    void inscribirseEnSesion_yaInscrito_lanzaExcepcion() {
        when(sesionRepository.findById(20L)).thenReturn(Optional.of(sesionGrupal));

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getName()).thenReturn("pedro@email.com");
            when(usuarioRepository.findByEmail("pedro@email.com")).thenReturn(Optional.of(cliente));
            when(sesionClienteRepository.existsBySesionIdAndClienteId(20L, 3L)).thenReturn(true);

            assertThatThrownBy(() -> entrenamientoService.inscribirseEnSesion(20L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Ya estás inscrito en esta sesión");

            verify(sesionClienteRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("inscribirse: sesión completa lanza excepción")
    void inscribirseEnSesion_sesionCompleta_lanzaExcepcion() {
        sesionGrupal.setCapacidadMaxima(5);
        when(sesionRepository.findById(20L)).thenReturn(Optional.of(sesionGrupal));

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getName()).thenReturn("pedro@email.com");
            when(usuarioRepository.findByEmail("pedro@email.com")).thenReturn(Optional.of(cliente));
            when(sesionClienteRepository.existsBySesionIdAndClienteId(20L, 3L)).thenReturn(false);
            when(sesionClienteRepository.countBySesionId(20L)).thenReturn(5L);

            assertThatThrownBy(() -> entrenamientoService.inscribirseEnSesion(20L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("La sesión está completa");

            verify(sesionClienteRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("inscribirse: sin capacidad máxima permite inscripción ilimitada")
    void inscribirseEnSesion_sinCapacidadMaxima_permiteInscripcion() {
        sesionGrupal.setCapacidadMaxima(null);
        when(sesionRepository.findById(20L)).thenReturn(Optional.of(sesionGrupal));

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getName()).thenReturn("pedro@email.com");
            when(usuarioRepository.findByEmail("pedro@email.com")).thenReturn(Optional.of(cliente));
            when(sesionClienteRepository.existsBySesionIdAndClienteId(20L, 3L)).thenReturn(false);
            when(sesionClienteRepository.save(any(SesionCliente.class))).thenReturn(new SesionCliente());

            entrenamientoService.inscribirseEnSesion(20L);

            verify(sesionClienteRepository).save(any(SesionCliente.class));
        }
    }

    // ─── asignarClienteASesion ────────────────────────────────────────────────

    @Test
    @DisplayName("asignar cliente a sesión: tipo no INDIVIDUAL lanza excepción")
    void asignarClienteASesion_tipoNoIndividual_lanzaExcepcion() {
        AsignarClienteRequest request = new AsignarClienteRequest();
        request.setClienteId(3L);

        when(sesionRepository.findById(20L)).thenReturn(Optional.of(sesionGrupal));

        assertThatThrownBy(() -> entrenamientoService.asignarClienteASesion(20L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("sesiones de tipo INDIVIDUAL");
    }

    @Test
    @DisplayName("asignar cliente a sesión INDIVIDUAL: ya tiene cliente asignado lanza excepción")
    void asignarClienteASesion_yaTieneCliente_lanzaExcepcion() {
        Sesion sesionIndividual = new Sesion();
        sesionIndividual.setId(21L);
        sesionIndividual.setEntrenamiento(entrenamientoIndividual);

        AsignarClienteRequest request = new AsignarClienteRequest();
        request.setClienteId(3L);

        when(sesionRepository.findById(21L)).thenReturn(Optional.of(sesionIndividual));
        when(sesionClienteRepository.countBySesionId(21L)).thenReturn(1L);

        assertThatThrownBy(() -> entrenamientoService.asignarClienteASesion(21L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("ya tiene un cliente asignado");
    }

    // ─── asignarEspecifico ────────────────────────────────────────────────────

    @Test
    @DisplayName("asignar específico: entrenamiento tipo GRUPAL lanza excepción")
    void asignarEspecifico_tipoNoEspecifico_lanzaExcepcion() {
        AsignarClienteRequest request = new AsignarClienteRequest();
        request.setClienteId(3L);

        when(entrenamientoRepository.findById(10L)).thenReturn(Optional.of(entrenamientoGrupal));

        assertThatThrownBy(() -> entrenamientoService.asignarEspecifico(10L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("debe ser de tipo ESPECIFICO");
    }

    @Test
    @DisplayName("asignar específico: entrenamiento ESPECIFICO y cliente existen → guarda asignación")
    void asignarEspecifico_datosValidos_guardaAsignacion() {
        AsignarClienteRequest request = new AsignarClienteRequest();
        request.setClienteId(3L);
        request.setNotas("Plan de fuerza");

        EntrenamientoEspecifico guardado = new EntrenamientoEspecifico();
        guardado.setId(50L);
        guardado.setEntrenamiento(entrenamientoEspecifico);
        guardado.setCliente(cliente);
        guardado.setNotas("Plan de fuerza");
        guardado.setCompletado(false);

        when(entrenamientoRepository.findById(12L)).thenReturn(Optional.of(entrenamientoEspecifico));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(cliente));
        when(entrenamientoEspecificoRepository.save(any(EntrenamientoEspecifico.class))).thenReturn(guardado);
        when(entrenamientoEjercicioRepository.findByEntrenamientoIdOrderByOrden(12L)).thenReturn(List.of());

        var response = entrenamientoService.asignarEspecifico(12L, request);

        assertThat(response).isNotNull();
        assertThat(response.getCompletado()).isFalse();

        ArgumentCaptor<EntrenamientoEspecifico> captor =
                ArgumentCaptor.forClass(EntrenamientoEspecifico.class);
        verify(entrenamientoEspecificoRepository).save(captor.capture());
        assertThat(captor.getValue().getNotas()).isEqualTo("Plan de fuerza");
    }

    // ─── marcarCompletado ─────────────────────────────────────────────────────

    @Test
    @DisplayName("marcar completado: actualiza el estado del entrenamiento específico")
    void marcarCompletado_especificoExistente_actualizaEstado() {
        EntrenamientoEspecifico especifico = new EntrenamientoEspecifico();
        especifico.setId(50L);
        especifico.setEntrenamiento(entrenamientoEspecifico);
        especifico.setCliente(cliente);
        especifico.setCompletado(false);

        when(entrenamientoEspecificoRepository.findById(50L)).thenReturn(Optional.of(especifico));
        when(entrenamientoEspecificoRepository.save(especifico)).thenReturn(especifico);
        when(entrenamientoEjercicioRepository.findByEntrenamientoIdOrderByOrden(any())).thenReturn(List.of());

        var response = entrenamientoService.marcarCompletado(50L, true);

        assertThat(especifico.getCompletado()).isTrue();
        assertThat(response.getCompletado()).isTrue();
    }

    // ─── reasignarEntrenador ──────────────────────────────────────────────────

    @Test
    @DisplayName("reasignar entrenador: cambia el entrenador del entrenamiento")
    void reasignarEntrenador_entrenadorExiste_actualizaRelacion() {
        Usuario nuevoEntrenador = new Usuario();
        nuevoEntrenador.setId(9L);
        nuevoEntrenador.setNombre("Sofia");
        nuevoEntrenador.setApellidos("Ruiz");
        nuevoEntrenador.setEmail("sofia@gym.com");

        when(entrenamientoRepository.findById(10L)).thenReturn(Optional.of(entrenamientoGrupal));
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(nuevoEntrenador));
        when(entrenamientoRepository.save(entrenamientoGrupal)).thenReturn(entrenamientoGrupal);

        entrenamientoService.reasignarEntrenador(10L, 9L);

        ArgumentCaptor<Entrenamiento> captor = ArgumentCaptor.forClass(Entrenamiento.class);
        verify(entrenamientoRepository).save(captor.capture());
        assertThat(captor.getValue().getEntrenador().getId()).isEqualTo(9L);
    }

    @Test
    @DisplayName("reasignar entrenador: nuevo entrenador inexistente lanza excepción")
    void reasignarEntrenador_entrenadorNoExiste_lanzaExcepcion() {
        when(entrenamientoRepository.findById(10L)).thenReturn(Optional.of(entrenamientoGrupal));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenamientoService.reasignarEntrenador(10L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Entrenador no encontrado con id: 99");

        verify(entrenamientoRepository, never()).save(any());
    }

    // ─── listarPorGimnasioYEntrenador ─────────────────────────────────────────

    @Test
    @DisplayName("listar por gimnasio y entrenador: devuelve solo los entrenamientos con sesiones futuras")
    void listarPorGimnasioYEntrenador_devuelveSoloEntrenamientosConSesionesFuturas() {
        when(entrenamientoRepository.findActivosConSesionesFuturas(1L, 2L))
                .thenReturn(List.of(entrenamientoGrupal, entrenamientoIndividual));
        when(entrenamientoEjercicioRepository.findByEntrenamientoIdOrderByOrden(any()))
                .thenReturn(List.of());

        var resultado = entrenamientoService.listarPorGimnasioYEntrenador(1L, 2L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(r -> r.getEntrenadorId().equals(2L));
        verify(entrenamientoRepository).findActivosConSesionesFuturas(1L, 2L);
    }

    @Test
    @DisplayName("listar por gimnasio y entrenador: entrenador sin sesiones futuras devuelve lista vacía")
    void listarPorGimnasioYEntrenador_sinSesionesFuturas_devuelveListaVacia() {
        when(entrenamientoRepository.findActivosConSesionesFuturas(1L, 2L))
                .thenReturn(List.of());

        var resultado = entrenamientoService.listarPorGimnasioYEntrenador(1L, 2L);

        assertThat(resultado).isEmpty();
        verify(entrenamientoRepository).findActivosConSesionesFuturas(1L, 2L);
    }

    // ─── agregarEjercicio ─────────────────────────────────────────────────────

    @Test
    @DisplayName("agregar ejercicio: ejercicio inexistente lanza excepción")
    void agregarEjercicio_ejercicioInexistente_lanzaExcepcion() {
        EntrenamientoEjercicioRequest request = new EntrenamientoEjercicioRequest();
        request.setEjercicioId(99L);

        when(entrenamientoRepository.findById(10L)).thenReturn(Optional.of(entrenamientoGrupal));
        when(ejercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenamientoService.agregarEjercicio(10L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ejercicio no encontrado con id: 99");
    }
}
