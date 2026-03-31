package com.flexicharge.flexicharge.charging.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class HeartbeatLog {

    private OffsetDateTime timestamp;
    private Double kwh;
}
