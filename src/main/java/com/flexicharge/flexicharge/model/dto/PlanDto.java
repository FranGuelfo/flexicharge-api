package com.flexicharge.flexicharge.model.dto;

import lombok.Data;

@Data
public class PlanDto {

    private String id;
    private String name;
    private double price;
    private int durationMonths;
}
