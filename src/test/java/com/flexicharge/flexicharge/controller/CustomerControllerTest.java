package com.flexicharge.flexicharge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicharge.flexicharge.model.dto.CustomerDto;
import com.flexicharge.flexicharge.security.JwtService;
import com.flexicharge.flexicharge.service.CustomerService;
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

    @Test
    void createCustomer_ShouldReturn201() throws Exception {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Fran");
        customerDto.setEmail("fran@test.com");
        customerDto.setPlanId("plan123");

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
}