package com.flexicharge.flexicharge.charging.infrastructure.adapters.out.persistence;

import com.flexicharge.flexicharge.charging.domain.entities.ChargingSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChargingSessionRepository extends MongoRepository<ChargingSession, String> {
    // Buscamos la sesión activa de un usuario (para que no cargue dos veces a la vez)
    Optional<ChargingSession> findByUserEmailAndStatus(String userEmail, String status);

    Optional<ChargingSession> findFirstByUserEmailAndStatusOrderByStartTimeDesc(String email, String status);
}
