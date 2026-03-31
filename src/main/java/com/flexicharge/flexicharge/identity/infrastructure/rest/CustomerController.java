package com.flexicharge.flexicharge.identity.infrastructure.rest;

import com.flexicharge.flexicharge.identity.application.services.CustomerService;
import com.flexicharge.flexicharge.identity.domain.entities.CustomerEntity;
import com.flexicharge.flexicharge.identity.domain.entities.CustomerProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/profile")
    public ResponseEntity<CustomerEntity> saveProfile(
            @RequestParam String email,
            @RequestBody CustomerProfileRequest request) {
        return ResponseEntity.ok(customerService.createOrUpdateProfile(email, request));
    }

    @GetMapping("/profile")
    public ResponseEntity<CustomerEntity> getProfile(@RequestParam String email) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PatchMapping("/profile/plan")
    public ResponseEntity<CustomerEntity> updatePlan(
            @RequestParam String email,
            @RequestParam String newPlanId) {
        return ResponseEntity.ok(customerService.updateSubscriptionPlan(email, newPlanId));
    }
}
