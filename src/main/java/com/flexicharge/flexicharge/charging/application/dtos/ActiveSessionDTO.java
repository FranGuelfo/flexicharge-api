package com.flexicharge.flexicharge.charging.application.dtos;

import com.flexicharge.flexicharge.charging.domain.entities.HeartbeatLog;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class ActiveSessionDTO {
    private String sessionId;
    private String chargerId;
    private OffsetDateTime startTime;
    private Double initialKwh;
    private Double currentKwh;
    private Double consumedKwh;    // Calculado: current - initial
    private Double estimatedCost;  // Calculado: consumed * precio actual
    private String status;
    private List<HeartbeatLog> heartbeats;
}
