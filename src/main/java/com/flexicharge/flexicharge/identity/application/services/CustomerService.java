package com.flexicharge.flexicharge.identity.application.services;

import com.flexicharge.flexicharge.shared.InfrastructureException;
import com.flexicharge.flexicharge.shared.Address;
import com.flexicharge.flexicharge.identity.domain.entities.CustomerEntity;
import com.flexicharge.flexicharge.identity.domain.entities.CustomerProfileRequest;
import com.flexicharge.flexicharge.identity.domain.repository.CustomerRepository;
import com.flexicharge.flexicharge.plans.domain.repository.PricingPlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository repository;
    private final PricingPlanRepository planRepository;

    public CustomerEntity createOrUpdateProfile(String email, CustomerProfileRequest request) {
        // 1. Buscamos si ya existe el cliente o creamos uno nuevo
        CustomerEntity customer = repository.findByEmail(email)
                .orElse(CustomerEntity.builder().email(email).createdAt(OffsetDateTime.now(ZoneOffset.UTC)).build());

        updateCustomerData(customer, request);
        log.info("Perfil actualizado para {}: Plan {}", email, request);
        return repository.save(customer);
    }

    public CustomerEntity getCustomerByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new InfrastructureException("Perfil de cliente no encontrado para: " + email));
    }

    public CustomerEntity updateSubscriptionPlan(String email, String newPlanId) {
        log.info("Cambiando plan a {} para el usuario {}", newPlanId, email);

        CustomerEntity customer = getCustomerByEmail(email);

        if (!planRepository.existsById(newPlanId)) {
            throw new InfrastructureException("El plan solicitado no existe: " + newPlanId);
        }

        customer.setPlanId(newPlanId);
        return repository.save(customer);
    }

    private void updateCustomerData(CustomerEntity customer, CustomerProfileRequest request) {
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setNif(request.getNif());

        // Validamos el plan al actualizar el perfil
        String planToSet = (request.getPlanId() != null) ? request.getPlanId() : "BASIC";
        customer.setPlanId(planRepository.existsById(planToSet) ? planToSet : "BASIC");

        customer.setAddress(new Address(
                request.getStreet(),
                request.getCity(),
                request.getZipCode(),
                request.getCountry()
        ));
    }
}
