package com.dev.flexicharge.flexicharge.controller;

import com.flexicharge.flexicharge.controller.CustomerController;
import com.flexicharge.flexicharge.model.dto.CustomerDto;
import com.flexicharge.flexicharge.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ShouldReturn201() throws Exception {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Fran");
        customerDto.setEmail("fran@test.com");

        when(customerService.save(any())).thenReturn(customerDto);

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fran"))
                .andExpect(jsonPath("$.email").value("fran@test.com"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }
}