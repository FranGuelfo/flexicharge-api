package com.flexicharge.flexicharge.plans.domain.repository;

import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PricingPlanRepository extends MongoRepository<PricingPlanEntity, String> {

    List<PricingPlanEntity> findAllByActiveTrue();

    Optional<PricingPlanEntity> findByIdAndActiveTrue(String id);
}
