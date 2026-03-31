package com.flexicharge.flexicharge.plans.application;

import com.flexicharge.flexicharge.shared.InfrastructureException;
import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import com.flexicharge.flexicharge.plans.domain.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingPlanService {
    private final PricingPlanRepository repository;

    public List<PricingPlanEntity> getAllPlans() {
        return repository.findAll();
    }

    public PricingPlanEntity savePlan(PricingPlanEntity plan) {
        log.info("Guardando nuevo plan de precios: {}", plan.getId());
        return repository.save(plan);
    }

    public void deletePlan(String id) {
        if ("BASIC".equalsIgnoreCase(id)) {
            throw new IllegalArgumentException("No se puede eliminar el plan BASIC por ser el sistema de tarifa por defecto.");
        }
        if (!repository.existsById(id)) {
            throw new InfrastructureException("El plan [" + id + "] no existe.");
        }
        log.warn("Eliminando plan de precios: {}", id);
        repository.deleteById(id);
    }
}
