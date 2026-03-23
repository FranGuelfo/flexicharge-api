package com.flexicharge.flexicharge.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Set;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username; // Será el email o un alias
    private String password;
    private Set<String> roles; // Ejemplo: ["ROLE_USER", "ROLE_ADMIN"]
}
