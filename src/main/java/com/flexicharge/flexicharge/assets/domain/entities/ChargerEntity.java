package com.flexicharge.flexicharge.assets.domain.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chargers")
public class ChargerEntity {

    @Id
    private String id; // Ej: "POSTE-001"

    private String model;
    private Double maxKw; // Potencia máxima (ej: 50.0)

    private String status; // AVAILABLE, CHARGING, OUT_OF_SERVICE

    private Double latitude;
    private Double longitude;

    private OffsetDateTime lastHeartbeat;

    private boolean active = true;
}
