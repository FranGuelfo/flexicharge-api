package com.flexicharge.flexicharge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicharge.flexicharge.exception.ResourceNotFoundException;
import com.flexicharge.flexicharge.model.dto.CustomerDto;
import com.flexicharge.flexicharge.security.JwtService;
import com.flexicharge.flexicharge.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setId("1");
        customerDto.setName("Fran");
        customerDto.setEmail("fran@test.com");
        customerDto.setPlanId("plan123");
    }

    @Test
    void createCustomer_ShouldReturn201() throws Exception {
        // Arrange
        when(customerService.save(any(CustomerDto.class))).thenReturn(customerDto);

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fran"))
                .andExpect(jsonPath("$.email").value("fran@test.com"));
    }

    @Test
    void getAllCustomer_ShouldReturnList() throws Exception {
        // Arrange
        when(customerService.findAll()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/customers/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_ShouldReturnCustomer_WhenExists() throws Exception {
        // Arrange
        when(customerService.findById("1")).thenReturn(customerDto);

        // Act & Assert
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("fran@test.com"));
    }

    @Test
    void getById_ShouldReturn404_WhenNotExists() throws Exception {
        // Arrange
        when(customerService.findById("99")).thenThrow(new ResourceNotFoundException("No encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByEmail_ShouldReturnCustomer() throws Exception {
        // Arrange
        when(customerService.findByEmail("fran@test.com")).thenReturn(customerDto);

        // Act & Assert
        mockMvc.perform(get("/api/customers/search")
                        .param("email", "fran@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("fran@test.com"));
    }

    @Test
    void getById_ShouldReturn404_WhenCustomerNotFound() throws Exception {
        // 1. Forzamos al servicio a lanzar la excepción de negocio
        when(customerService.findById("999"))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado con id: 999"));

        // 2. MockMvc capturará la excepción y el GlobalExceptionHandler entrará en acción
        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound()) // Comprueba que devuelve 404
                .andExpect(jsonPath("$.message").value("Cliente no encontrado con id: 999")) // Cubre ErrorDetails
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void handleGlobalException_ShouldReturn500() throws Exception {
        // 1. Simulamos un error catastrófico e inesperado en el servicio
        when(customerService.findAll())
                .thenThrow(new RuntimeException("Error inesperado de base de datos"));

        // 2. Ejecutamos la petición
        mockMvc.perform(get("/api/customers/all"))
                .andExpect(status().isInternalServerError()) // Comprueba que es 500
                .andExpect(jsonPath("$.message").value("Ha ocurrido un error interno en el servidor"))
                .andExpect(jsonPath("$.details").exists());
    }
}