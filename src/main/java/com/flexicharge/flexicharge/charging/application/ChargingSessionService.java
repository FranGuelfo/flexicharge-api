package com.flexicharge.flexicharge.charging.application;

import com.flexicharge.flexicharge.assets.domain.entities.ChargerEntity;
import com.flexicharge.flexicharge.assets.domain.repository.ChargerRepository;
import com.flexicharge.flexicharge.billing.application.GenerateInvoiceService;
import com.flexicharge.flexicharge.plans.application.PriceCalculator;
import com.flexicharge.flexicharge.shared.exception.InfrastructureException;
import com.flexicharge.flexicharge.charging.application.dtos.ActiveSessionDTO;
import com.flexicharge.flexicharge.charging.domain.entities.ChargingSession;
import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import com.flexicharge.flexicharge.charging.domain.utils.EnergyCalculator;
import com.flexicharge.flexicharge.charging.infrastructure.adapters.out.persistence.ChargingSessionRepository;
import com.flexicharge.flexicharge.identity.domain.entities.CustomerEntity;
import com.flexicharge.flexicharge.identity.domain.repository.CustomerRepository;
import com.flexicharge.flexicharge.shared.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChargingSessionService {

    private final ChargingSessionRepository repository;
    private final GenerateInvoiceService invoiceService;
    private final EnergyCalculator energyCalculator;
    private final PriceCalculator priceCalculator;
    private final CustomerRepository customerRepository;
    private final ChargerRepository chargerRepository;

    public ChargingSession startSession(String email, String chargerId, Double initialKwh) {
        // 1. VALIDAR CLIENTE
        // No solo vemos si existe, sino que traemos sus datos para el log o para el ActiveSessionDTO
        CustomerEntity customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new InfrastructureException("Perfil de cliente no encontrado. Debes completar tus datos antes de cargar."));

        // 2. VALIDAR CARGADOR
        // Cambiamos el findById por el que filtra por activos
        ChargerEntity charger = chargerRepository.findByIdAndActiveTrue(chargerId)
                .orElseThrow(() -> new InfrastructureException("El cargador no está disponible en la red actual."));

        // 3. CONTROL DE ESTADO DEL CARGADOR
        // Si alguien ya lo está usando, lanzamos error para evitar "pisar" otra carga
        if (!"AVAILABLE".equalsIgnoreCase(charger.getStatus())) {
            throw new IllegalArgumentException("Cargador no disponible. Estado actual: " + charger.getStatus());
        }

        // 4. BLOQUEAR CARGADOR
        // Cambiamos el estado a CHARGING para que en el mapa aparezca como ocupado
        charger.setStatus("CHARGING");
        charger.setLastHeartbeat(OffsetDateTime.now(ZoneOffset.UTC)); // Marcamos actividad
        chargerRepository.save(charger);

        log.info("Iniciando sesión para {} en el cargador {}. Plan del cliente: {}",
                customer.getEmail(), chargerId, customer.getPlanId());

        // 5. CREAR Y PERSISTIR LA SESIÓN
        ChargingSession session = ChargingSession.builder()
                .userEmail(email)
                .chargerId(chargerId)
                .initialKwh(initialKwh)
                .currentKwh(initialKwh)
                .status(AppConstants.STARTED)
                .heartbeats(new ArrayList<>())
                .build();

        return repository.save(session);
    }

    public void stopSession(String sessionId, Double finalKwh) {
        ChargingSession session = repository.findById(sessionId)
                .orElseThrow(() -> new InfrastructureException("Sesión no encontrada"));

        // 1. BUSCAMOS EL CARGADOR que tiene asignada esta sesión
        ChargerEntity charger = chargerRepository.findById(session.getChargerId())
                .orElseThrow(() -> new InfrastructureException("Cargador no encontrado"));

        // 2. LO LIBERAMOS
        charger.setStatus("AVAILABLE");
        chargerRepository.save(charger); // Ahora el poste vuelve a estar verde en el mapa

        // 3. El resto de la lógica de fin de sesión...
        Double totalConsumed = energyCalculator.calculateConsumedEnergy(session.getInitialKwh(), finalKwh);
        session.setEndTime(OffsetDateTime.now(ZoneOffset.UTC));
        session.setCurrentKwh(finalKwh);
        session.setStatus("COMPLETED");

        // Enviamos a facturación
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
        log.info("Sesión terminada y cargador {} liberado con éxito.", charger.getId());
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
        // 1. Buscamos la sesión activa
        ChargingSession session = repository.findFirstByUserEmailAndStatusOrderByStartTimeDesc(email, AppConstants.STARTED)
                .orElseThrow(() -> new InfrastructureException("No se ha encontrado ninguna sesión activa para el usuario."));

        // 2. Buscamos al cliente (para obtener su plan de precios)
        CustomerEntity customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new InfrastructureException("Error: Cliente no identificado."));

        // 3. Delegamos la creación del DTO al método especializado
        return mapToActiveSessionDTO(session, customer);
    }

    /**
     * Método privado para encapsular la lógica de mapeo y cálculos financieros.
     * Mantiene el método principal limpio y fácil de leer.
     */
    private ActiveSessionDTO mapToActiveSessionDTO(ChargingSession session, CustomerEntity customer) {
        // Calculamos kWh consumidos hasta el momento
        Double consumedSoFar = energyCalculator.calculateConsumedEnergy(session.getInitialKwh(), session.getCurrentKwh());

        // Calculamos el precio actual según el plan del cliente (Delegado al PriceCalculator)
        BigDecimal currentPrice = priceCalculator.calculatePrice(OffsetDateTime.now(), customer.getPlanId());

        // Calculamos el coste estimado
        BigDecimal estimatedCost = currentPrice.multiply(BigDecimal.valueOf(consumedSoFar))
                .setScale(2, RoundingMode.HALF_UP);

        log.debug("Mapeando sesión activa. Consumo: {} kWh, Coste est.: {}€", consumedSoFar, estimatedCost);

        return ActiveSessionDTO.builder()
                .sessionId(session.getId())
                .chargerId(session.getChargerId())
                .customerName(customer.getFirstName() + " " + customer.getLastName()) // Añadido para mejor UX
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
