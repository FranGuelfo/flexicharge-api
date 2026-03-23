package com.dev.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.model.dto.CustomerDto;
import com.flexicharge.flexicharge.exception.ResourceNotFoundException;
import com.flexicharge.flexicharge.mapper.CustomerMapper;
import com.flexicharge.flexicharge.model.Customer;
import com.flexicharge.flexicharge.repository.CustomerRepository;
import com.flexicharge.flexicharge.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId("1");
        customer.setEmail("test@test.com");

        customerDto = new CustomerDto();
        customerDto.setId("1");
        customerDto.setEmail("test@test.com");
    }

    @Test
    void save_ShouldReturnSavedCustomerDto_WhenEmailDoesNotExist() {
        // Arrange
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(customerMapper.toEntity(any())).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toDTO(any())).thenReturn(customerDto);

        // Act
        CustomerDto result = customerService.save(customerDto);

        // Assert
        assertNotNull(result);
        assertEquals(customerDto.getEmail(), result.getEmail());
        verify(customerRepository).save(any());
    }

    @Test
    void save_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.save(customerDto));
        verify(customerRepository, never()).save(any());
    }
}
