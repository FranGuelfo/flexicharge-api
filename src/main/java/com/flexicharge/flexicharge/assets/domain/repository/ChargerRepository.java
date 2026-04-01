package com.flexicharge.flexicharge.assets.domain.repository;

import com.flexicharge.flexicharge.assets.domain.entities.ChargerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChargerRepository extends MongoRepository<ChargerEntity, String> {
    // Solo recupera cargadores que no han sido "borrados"
    List<ChargerEntity> findAllByActiveTrue();

    // Para buscar por ID solo si está activo
    Optional<ChargerEntity> findByIdAndActiveTrue(String id);
}
