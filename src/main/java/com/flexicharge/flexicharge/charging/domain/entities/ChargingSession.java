package com.flexicharge.flexicharge.charging.domain.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "charging_sessions")
public class ChargingSession {

    @Id
    private String id;

    private String userEmail;
    private String chargerId;      // ID del poste de carga
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Double initialKwh;     // Lectura del contador al empezar
    private Double currentKwh;     // Lectura actual (se actualiza)
    private String status;          // "STARTED", "CHARGING", "COMPLETED" TODO: quizas mejor un enum

    @Builder.Default
    private List<HeartbeatLog> heartbeats = new ArrayList<>();

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime updatedAt;
}
