package com.flexicharge.flexicharge.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "plans")
public class Plan {
    @Id
    private String id;
    private String name;        // Ej: "Premium"
    private double price;       // Ej: 19.99
    private int durationMonths; // Ej: 12
}
