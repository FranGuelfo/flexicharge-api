package com.flexicharge.flexicharge.service.impl;

import com.flexicharge.flexicharge.exception.ResourceNotFoundException;
import com.flexicharge.flexicharge.mapper.CustomerMapper;
import com.flexicharge.flexicharge.model.Customer;
import com.flexicharge.flexicharge.model.Plan;
import com.flexicharge.flexicharge.model.Subscription;
import com.flexicharge.flexicharge.model.dto.CustomerDto;
import com.flexicharge.flexicharge.repository.CustomerRepository;
import com.flexicharge.flexicharge.repository.PlanRepository;
import com.flexicharge.flexicharge.service.CustomerService;
import com.flexicharge.flexicharge.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PlanRepository planRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> findAll() {
        log.info("Obteniendo todos los clientes");
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDTO)
                .toList();
    }

    @Override
    public CustomerDto findById(String id) {
        log.info("Buscando cliente con id: {}", id);
        return customerRepository.findById(id)
                .map(customerMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERR_CUSTOMER_NOT_FOUND + id));    }

    @Override
    public CustomerDto findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(customerMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un cliente con email: " + email));    }

    @Override
    public CustomerDto save(CustomerDto customerDto) {
        // 1. Validar email (negocio)
        if (customerRepository.existsByEmail(customerDto.getEmail())) {
            throw new ResourceNotFoundException(AppConstants.ERR_EMAIL_EXISTS);
        }

        // 2. Buscar el Plan REAL en la base de datos para traer su nombre y precio
        Plan plan = planRepository.findById(customerDto.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ERR_PLAN_NOT_FOUND));

        // 3. Convertir DTO a Entidad (esto rellena name, email y planId)
        Customer customerEntity = customerMapper.toEntity(customerDto);

        // 4. CREAR EL OBJETO SUBSCRIPTION (El que le falta a tu entidad)
        Subscription sub = new Subscription();
        sub.setPlanName(plan.getName());    // Sacamos el nombre del Plan encontrado
        sub.setPrice(plan.getPrice());      // Sacamos el precio del Plan
        sub.setStatus(AppConstants.STATUS_ACTIVE);
        sub.setStartDate(LocalDateTime.now());
        sub.setNextBillingDate(LocalDateTime.now().plusMonths(1));

        // 5. Inyectar la suscripción en el cliente
        customerEntity.setSubscription(sub);

        // 6. Guardar en MongoDB
        Customer savedEntity = customerRepository.save(customerEntity);

        // 7. El Mapper ahora verá que 'subscription.planName' NO es null
        return customerMapper.toDTO(savedEntity);
    }

    @Override
    public void delete(String id) {
        log.info("Eliminando cliente con id: {}", id);
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Cliente no encontrado con id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
