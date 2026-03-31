package com.flexicharge.flexicharge.identity.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfileRequest {
    private String firstName;
    private String lastName;
    private String nif;
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private String planId;
}
