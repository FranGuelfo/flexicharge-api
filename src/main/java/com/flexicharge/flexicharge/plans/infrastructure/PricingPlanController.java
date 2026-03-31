package com.flexicharge.flexicharge.plans.infrastructure;

import com.flexicharge.flexicharge.plans.application.PricingPlanService;
import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PricingPlanController {
    private final PricingPlanService service;

    @GetMapping
    public ResponseEntity<List<PricingPlanEntity>> getPlans() {
        return ResponseEntity.ok(service.getAllPlans());
    }

    @PostMapping
    public ResponseEntity<PricingPlanEntity> createPlan(@RequestBody PricingPlanEntity plan) {
        return ResponseEntity.ok(service.savePlan(plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        service.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
