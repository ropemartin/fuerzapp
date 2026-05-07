package com.fuerzapp.service;

import com.fuerzapp.dto.EjercicioRequest;
import com.fuerzapp.dto.EjercicioResponse;
import com.fuerzapp.entity.Ejercicio;
import com.fuerzapp.entity.Gimnasio;
import com.fuerzapp.entity.Usuario;
import com.fuerzapp.enums.GrupoMuscular;
import com.fuerzapp.repository.EjercicioRepository;
import com.fuerzapp.repository.GimnasioRepository;
import com.fuerzapp.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EjercicioService — tests unitarios")
class EjercicioServiceTest {

    @Mock private EjercicioRepository ejercicioRepository;
    @Mock private GimnasioRepository gimnasioRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private EjercicioServiceImpl ejercicioService;

    private Gimnasio gimnasio;
    private Usuario entrenador;
    private Ejercicio ejercicioPropio;
    private Ejercicio ejercicioPredefinido;

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

        ejercicioPropio = new Ejercicio();
        ejercicioPropio.setId(10L);
        ejercicioPropio.setNombre("Press banca");
        ejercicioPropio.setEsPredefinido(false);
        ejercicioPropio.setGimnasio(gimnasio);
        ejercicioPropio.setCreadoPor(entrenador);

        ejercicioPredefinido = new Ejercicio();
        ejercicioPredefinido.setId(1L);
        ejercicioPredefinido.setNombre("Sentadilla");
        ejercicioPredefinido.setEsPredefinido(true);
    }

    // ─── eliminar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar: ejercicio predefinido lanza excepción")
    void eliminar_ejercicioPredefinido_lanzaExcepcion() {
        when(ejercicioRepository.findById(1L)).thenReturn(Optional.of(ejercicioPredefinido));

        assertThatThrownBy(() -> ejercicioService.eliminar(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No se pueden eliminar ejercicios de la biblioteca global");

        verify(ejercicioRepository, never()).delete(any());
    }

    @Test
    @DisplayName("eliminar: ejercicio propio se elimina correctamente")
    void eliminar_ejercicioPropio_eliminaCorrectamente() {
        when(ejercicioRepository.findById(10L)).thenReturn(Optional.of(ejercicioPropio));

        ejercicioService.eliminar(10L);

        verify(ejercicioRepository).delete(ejercicioPropio);
    }

    @Test
    @DisplayName("eliminar: ejercicio inexistente lanza excepción")
    void eliminar_ejercicioInexistente_lanzaExcepcion() {
        when(ejercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ejercicioService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ejercicio no encontrado con id: 99");
    }

    // ─── listarDisponiblesPorGimnasio ─────────────────────────────────────────

    @Test
    @DisplayName("listar disponibles: sin filtro usa query sin grupo muscular")
    void listarDisponibles_sinFiltroGrupoMuscular_retornaTodos() {
        when(ejercicioRepository.findDisponiblesByGimnasioId(1L))
                .thenReturn(List.of(ejercicioPropio, ejercicioPredefinido));

        List<EjercicioResponse> resultado = ejercicioService.listarDisponiblesPorGimnasio(1L, null);

        assertThat(resultado).hasSize(2);
        verify(ejercicioRepository).findDisponiblesByGimnasioId(1L);
        verify(ejercicioRepository, never()).findDisponiblesByGimnasioIdAndGrupoMuscular(any(), any());
    }

    @Test
    @DisplayName("listar disponibles: con filtro usa query con grupo muscular")
    void listarDisponibles_conFiltroGrupoMuscular_aplicaFiltro() {
        when(ejercicioRepository.findDisponiblesByGimnasioIdAndGrupoMuscular(1L, GrupoMuscular.PECHO))
                .thenReturn(List.of(ejercicioPropio));

        List<EjercicioResponse> resultado =
                ejercicioService.listarDisponiblesPorGimnasio(1L, GrupoMuscular.PECHO);

        assertThat(resultado).hasSize(1);
        verify(ejercicioRepository).findDisponiblesByGimnasioIdAndGrupoMuscular(1L, GrupoMuscular.PECHO);
        verify(ejercicioRepository, never()).findDisponiblesByGimnasioId(any());
    }

    // ─── listarPredefinidos ───────────────────────────────────────────────────

    @Test
    @DisplayName("listar predefinidos: retorna solo ejercicios de la biblioteca global")
    void listarPredefinidos_retornaPredefinidos() {
        when(ejercicioRepository.findByEsPredefinidoTrue()).thenReturn(List.of(ejercicioPredefinido));

        List<EjercicioResponse> resultado = ejercicioService.listarPredefinidos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).isEsPredefinido()).isTrue();
    }

    // ─── crearEjercicioEnGimnasio ─────────────────────────────────────────────

    @Test
    @DisplayName("crear ejercicio: se asocia al gimnasio y al entrenador autenticado")
    void crearEjercicioEnGimnasio_datosValidos_guardaConAsociaciones() {
        EjercicioRequest request = new EjercicioRequest();
        request.setNombre("Remo con barra");
        request.setDescripcion("Ejercicio de espalda");
        request.setGrupoMuscular(GrupoMuscular.ESPALDA);

        Ejercicio guardado = new Ejercicio();
        guardado.setId(20L);
        guardado.setNombre("Remo con barra");
        guardado.setEsPredefinido(false);
        guardado.setGimnasio(gimnasio);
        guardado.setCreadoPor(entrenador);

        when(gimnasioRepository.findById(1L)).thenReturn(Optional.of(gimnasio));
        when(usuarioRepository.findByEmail("ana@fitgym.com")).thenReturn(Optional.of(entrenador));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(guardado);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication auth = mock(Authentication.class);
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(context.getAuthentication()).thenReturn(auth);
            when(auth.getName()).thenReturn("ana@fitgym.com");

            EjercicioResponse response = ejercicioService.crearEjercicioEnGimnasio(1L, request);

            assertThat(response.getNombre()).isEqualTo("Remo con barra");
            assertThat(response.isEsPredefinido()).isFalse();
        }
    }

    @Test
    @DisplayName("crear ejercicio: gimnasio inexistente lanza excepción")
    void crearEjercicioEnGimnasio_gimnasioInexistente_lanzaExcepcion() {
        when(gimnasioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ejercicioService.crearEjercicioEnGimnasio(99L, new EjercicioRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Gimnasio no encontrado con id: 99");
    }
}
