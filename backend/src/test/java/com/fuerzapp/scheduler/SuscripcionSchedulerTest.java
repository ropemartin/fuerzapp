package com.fuerzapp.scheduler;

import com.fuerzapp.repository.ClienteSuscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionScheduler — tests unitarios")
class SuscripcionSchedulerTest {

    @Mock private ClienteSuscripcionRepository clienteSuscripcionRepository;

    @InjectMocks private SuscripcionScheduler scheduler;

    @Test
    @DisplayName("expirar suscripciones: llama al repositorio y registra el resultado")
    void expirarSuscripcionesVencidas_llamaAlRepositorio() {
        when(clienteSuscripcionRepository.expirarVencidas()).thenReturn(3);

        scheduler.expirarSuscripcionesVencidas();

        verify(clienteSuscripcionRepository).expirarVencidas();
    }

    @Test
    @DisplayName("expirar suscripciones: sin vencidas no lanza excepción")
    void expirarSuscripcionesVencidas_sinVencidas_noLanzaExcepcion() {
        when(clienteSuscripcionRepository.expirarVencidas()).thenReturn(0);

        scheduler.expirarSuscripcionesVencidas();

        verify(clienteSuscripcionRepository).expirarVencidas();
    }
}
