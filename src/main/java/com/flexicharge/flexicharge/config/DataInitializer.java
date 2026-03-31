package com.flexicharge.flexicharge.config;

import com.flexicharge.flexicharge.assets.domain.entities.ChargerEntity;
import com.flexicharge.flexicharge.assets.domain.repository.ChargerRepository;
import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import com.flexicharge.flexicharge.plans.domain.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PricingPlanRepository planRepository;
    private final ChargerRepository chargerRepository;

    @Override
    public void run(String... args) {
        // Solo insertamos si la tabla está vacía
        if (planRepository.count() == 0) {
            planRepository.save(new PricingPlanEntity("BASIC", "Plan Básico", "Tarifa estándar",
                    new BigDecimal("0.50"), new BigDecimal("0.30"), BigDecimal.ZERO));
        }

        if (chargerRepository.count() == 0) {
            chargerRepository.save(ChargerEntity.builder()
                    .id("POSTE-001")
                    .model("FLEXI-ULTRA-200")
                    .status("AVAILABLE")
                    .maxKw(150.0)
                    .build());
        }
    }
}
