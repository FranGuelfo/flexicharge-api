package com.flexicharge.flexicharge.repository;

import com.flexicharge.flexicharge.model.Plan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanRepository extends MongoRepository<Plan, String> {

    boolean existsByName(String name);
}
