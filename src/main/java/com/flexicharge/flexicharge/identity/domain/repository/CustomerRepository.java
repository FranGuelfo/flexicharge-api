package com.flexicharge.flexicharge.identity.domain.repository;

import com.flexicharge.flexicharge.identity.domain.entities.CustomerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<CustomerEntity, String> {

    Optional<CustomerEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
