package com.fuerzapp.scheduler;

import com.fuerzapp.repository.ClienteSuscripcionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuscripcionScheduler {

    private final ClienteSuscripcionRepository clienteSuscripcionRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void expirarSuscripcionesVencidas() {
        int actualizadas = clienteSuscripcionRepository.expirarVencidas();
        if (actualizadas > 0) {
            log.info("Suscripciones expiradas: {}", actualizadas);
        }
    }
}
