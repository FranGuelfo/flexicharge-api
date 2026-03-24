package com.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.model.Plan;
import com.flexicharge.flexicharge.model.dto.CustomerDto;
import com.flexicharge.flexicharge.exception.ResourceNotFoundException;
import com.flexicharge.flexicharge.mapper.CustomerMapper;
import com.flexicharge.flexicharge.model.Customer;
import com.flexicharge.flexicharge.repository.CustomerRepository;
import com.flexicharge.flexicharge.repository.PlanRepository;
import com.flexicharge.flexicharge.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private PlanRepository planRepository;

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
    void findAll_ShouldReturnList_WhenCustomersExist() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toDTO(any())).thenReturn(customerDto);

        // Act
        List<CustomerDto> result = customerService.findAll();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(customerRepository).findAll();
    }

    @Test
    void save_ShouldReturnSavedCustomerDto_WhenEmailDoesNotExist() {
        // Arrange
        Plan mockPlan = new Plan();
        mockPlan.setName("Premium");
        mockPlan.setPrice(9.99);

        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(planRepository.findById(any())).thenReturn(Optional.of(mockPlan));

        when(customerMapper.toEntity(any())).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);
        when(customerMapper.toDTO(any())).thenReturn(customerDto);

        // Act
        CustomerDto result = customerService.save(customerDto);

        // Assert
        assertNotNull(result);
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

    @Test
    void findById_ShouldReturnCustomer_WhenExists() {
        // Arrange
        when(customerRepository.findById("1")).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(customerDto);

        // Act
        CustomerDto result = customerService.findById("1");

        // Assert
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(customerRepository.findById("99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.findById("99"));
    }

    @Test
    void save_ShouldThrowException_WhenPlanDoesNotExist() {
        // Arrange
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);

        when(planRepository.findById(any())).thenReturn(Optional.empty());

        // 3. Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.save(customerDto));
    }

    @Test
    void delete_ShouldDelete_WhenCustomerExists() {
        // Arrange
        when(customerRepository.existsById("1")).thenReturn(true);

        // Act
        customerService.delete("1");

        // Assert
        verify(customerRepository, times(1)).deleteById("1");
    }

    @Test
    void delete_ShouldThrowException_WhenCustomerDoesNotExist() {
        // Arrange
        when(customerRepository.existsById("99")).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.delete("99"));
        verify(customerRepository, never()).deleteById(anyString());
    }

    @Test
    void findByEmail_ShouldReturnCustomer_WhenExists() {
        // Arrange
        when(customerRepository.findByEmail("test@test.com")).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(customerDto);

        // Act
        CustomerDto result = customerService.findByEmail("test@test.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void findByEmail_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(customerRepository.findByEmail("error@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.findByEmail("error@test.com"));
    }
}
