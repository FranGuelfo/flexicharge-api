package com.flexicharge.flexicharge.plans.application;

import com.flexicharge.flexicharge.shared.exception.InfrastructureException;
import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import com.flexicharge.flexicharge.plans.domain.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceCalculator {

    private final PricingPlanRepository planRepository;

    public BigDecimal calculatePrice(OffsetDateTime time, String planId) {
        // Buscamos el plan activo en la base de datos
        PricingPlanEntity plan = planRepository.findByIdAndActiveTrue(planId)
                .orElseGet(() -> {
                    log.warn("Plan {} no encontrado o inactivo. Aplicando plan BASIC.", planId);
                    return planRepository.findById("BASIC")
                            .orElseThrow(() -> new InfrastructureException("Error crítico: No existe el plan BASIC."));
                });

        // Determinamos si es hora Punta o Valle
        // Punta: 10:00 a 14:00 y 18:00 a 22:00 (Ejemplo estándar)
        int hour = time.getHour();
        boolean isPunta = (hour >= 10 && hour < 14) || (hour >= 18 && hour < 22);

        BigDecimal finalPrice = isPunta ? plan.getPricePunta() : plan.getPriceValle();

        log.debug("Calculando precio para plan {}: {} €/kWh (Hora: {})", planId, finalPrice, hour);
        return finalPrice;
    }
}
