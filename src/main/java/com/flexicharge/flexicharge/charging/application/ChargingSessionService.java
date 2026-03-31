package com.flexicharge.flexicharge.charging.application;

import com.flexicharge.flexicharge.billing.application.GenerateInvoiceService;
import com.flexicharge.flexicharge.billing.domain.utils.PriceCalculator;
import com.flexicharge.flexicharge.billing.exceptions.InfrastructureException;
import com.flexicharge.flexicharge.charging.application.dtos.ActiveSessionDTO;
import com.flexicharge.flexicharge.charging.domain.entities.ChargingSession;
import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import com.flexicharge.flexicharge.charging.domain.utils.EnergyCalculator;
import com.flexicharge.flexicharge.charging.infrastructure.adapters.out.persistence.ChargingSessionRepository;
import com.flexicharge.flexicharge.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionService {

    private final ChargingSessionRepository repository;
    private final GenerateInvoiceService invoiceService;
    private final EnergyCalculator energyCalculator;
    private final PriceCalculator priceCalculator;

    public ChargingSession startSession(String email, String chargerId, Double initialKwh) {
        repository.findByUserEmailAndStatus(email, AppConstants.STARTED)
                .ifPresent(s -> { throw new IllegalArgumentException("Ya tienes una sesión en curso."); });

        ChargingSession session = ChargingSession.builder()
                .userEmail(email)
                .chargerId(chargerId)
                .startTime(OffsetDateTime.now(ZoneOffset.UTC))
                .initialKwh(initialKwh)
                .currentKwh(initialKwh)
                .status(AppConstants.STARTED)
                .build();

        return repository.save(session);
    }

    public void stopSession(String sessionId, Double finalKwh) {
        ChargingSession session = repository.findById(sessionId)
                .orElseThrow(() -> new InfrastructureException("Sesión no encontrada"));

        Double totalConsumed = energyCalculator.calculateConsumedEnergy(session.getInitialKwh(), finalKwh);

        session.setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
        session.setCurrentKwh(finalKwh);
        session.setStatus("COMPLETED");

        invoiceService.createAndSaveInvoice(
                session.getUserEmail(),
                totalConsumed,
                session.getStartTime(),
                session.getEndTime(),
                session.getInitialKwh(),
                session.getCurrentKwh(),
                session.getHeartbeats()
        );
        repository.save(session);
    }

    public ChargingSession updateHeartbeat(String sessionId, Double currentKwh) {
        ChargingSession session = repository.findById(sessionId)
                .orElseThrow(() -> new InfrastructureException("Sesión no encontrada."));

        if (!AppConstants.STARTED.equals(session.getStatus())) {
            throw new IllegalArgumentException("La sesión ya no está activa.");
        }

        // Siempre actualizamos el valor "en vivo" de la sesión
        session.setCurrentKwh(currentKwh);

        // Lógica de filtrado para el HISTORIAL (Log)
        if (shouldValueBeLogged(session, currentKwh)) {
            session.getHeartbeats().add(new HeartbeatLog(OffsetDateTime.now(ZoneOffset.UTC), currentKwh));
            log.debug("Nuevo hito registrado en el historial: {} kWh", currentKwh);
        }

        return repository.save(session);
    }

    private boolean shouldValueBeLogged(ChargingSession session, Double newKwh) {
        // Si es el primer heartbeat, lo guardamos siempre
        if (session.getHeartbeats().isEmpty()) {
            return true;
        }

        // Obtenemos el último registro guardado
        HeartbeatLog lastLog = session.getHeartbeats().get(session.getHeartbeats().size() - 1);

        // Condición 1: Delta de Energía (Ej: > 0.1 kWh)
        double deltaKwh = newKwh - lastLog.getKwh();
        if (deltaKwh >= 0.1) {
            return true;
        }

        // Condición 2: Ventana de Tiempo (Ej: > 5 minutos)
        long minutesSinceLastLog = java.time.Duration.between(lastLog.getTimestamp(), OffsetDateTime.now()).toMinutes();
        return minutesSinceLastLog >= 5;
    }

    public ActiveSessionDTO getActiveSession(String email) {
        ChargingSession session = repository.findFirstByUserEmailAndStatusOrderByStartTimeDesc(email, AppConstants.STARTED)
                .orElseThrow(() -> new InfrastructureException("No hay sesión activa."));

        Double consumedSoFar = energyCalculator.calculateConsumedEnergy(session.getInitialKwh(), session.getCurrentKwh());

        BigDecimal currentPrice = priceCalculator.calculatePrice(OffsetDateTime.now(ZoneOffset.UTC));
        BigDecimal estimatedCost = currentPrice.multiply(BigDecimal.valueOf(consumedSoFar))
                .setScale(2, RoundingMode.HALF_UP);

        return ActiveSessionDTO.builder()
                .sessionId(session.getId())
                .chargerId(session.getChargerId())
                .startTime(session.getStartTime())
                .initialKwh(session.getInitialKwh())
                .currentKwh(session.getCurrentKwh())
                .consumedKwh(consumedSoFar)
                .estimatedCost(estimatedCost.doubleValue())
                .status(session.getStatus())
                .heartbeats(session.getHeartbeats())
                .build();
    }
}
