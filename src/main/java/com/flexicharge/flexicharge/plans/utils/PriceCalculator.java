package com.flexicharge.flexicharge.plans.utils;

import com.flexicharge.flexicharge.shared.InfrastructureException;
import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import com.flexicharge.flexicharge.plans.domain.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class PriceCalculator {

    private final PricingPlanRepository planRepository;

    public BigDecimal calculatePrice(OffsetDateTime fecha, String planId) {

        String effectivePlanId = (planId == null) ? "BASIC" : planId;

        // 1. Buscamos el plan en la base de datos
        PricingPlanEntity plan = planRepository.findById(effectivePlanId)
                .orElseThrow(() -> new InfrastructureException("Plan de precios no encontrado: " + planId));

        // 2. Lógica de tiempo (la que ya teníamos)
        LocalTime horaActual = fecha.toLocalTime();
        LocalTime inicioPunta = LocalTime.of(8, 0);
        LocalTime finPunta = LocalTime.of(22, 0);

        // 3. Aplicamos los precios DEL PLAN
        if (!horaActual.isBefore(inicioPunta) && horaActual.isBefore(finPunta)) {
            return plan.getPricePunta();
        } else {
            return plan.getPriceValle();
        }
    }
}
