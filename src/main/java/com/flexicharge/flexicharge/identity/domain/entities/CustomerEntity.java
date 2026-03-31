package com.flexicharge.flexicharge.identity.domain.entities;

import com.flexicharge.flexicharge.shared.Address;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "customers")
public class CustomerEntity {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;

    private String nif;

    private Address address;

    private String planId;

    private OffsetDateTime createdAt;
}
