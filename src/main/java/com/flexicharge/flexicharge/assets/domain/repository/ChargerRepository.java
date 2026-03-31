package com.flexicharge.flexicharge.assets.domain.repository;

import com.flexicharge.flexicharge.assets.domain.entities.ChargerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChargerRepository extends MongoRepository<ChargerEntity, String> {
}
