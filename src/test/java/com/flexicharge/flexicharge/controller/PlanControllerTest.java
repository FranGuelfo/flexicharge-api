package com.flexicharge.flexicharge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicharge.flexicharge.model.dto.PlanDto;
import com.flexicharge.flexicharge.security.JwtService;
import com.flexicharge.flexicharge.service.PlanService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlanController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlanService planService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllPlans_ShouldReturn200() throws Exception {
        when(planService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/plans"))
                .andExpect(status().isOk());
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        PlanDto dto = new PlanDto();
        dto.setName("Basic");

        when(planService.save(any())).thenReturn(dto);

        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/plans/1"))
                .andExpect(status().isNoContent());

        // Verificamos que el controlador realmente llamó al servicio
        verify(planService).delete("1");
    }
}
