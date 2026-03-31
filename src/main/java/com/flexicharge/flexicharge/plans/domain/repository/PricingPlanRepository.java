package com.flexicharge.flexicharge.plans.domain.repository;

import com.flexicharge.flexicharge.plans.domain.entities.PricingPlanEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PricingPlanRepository extends MongoRepository<PricingPlanEntity, String> {
}
