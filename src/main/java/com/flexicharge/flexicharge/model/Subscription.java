package com.flexicharge.flexicharge.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Subscription {
    private String planName;      // Ej: "Premium", "Basic"
    private Double price;         // Ej: 9.99
    private String status;        // Ej: "ACTIVE", "INACTIVE"
    private LocalDateTime startDate;
    private LocalDateTime nextBillingDate;
}
