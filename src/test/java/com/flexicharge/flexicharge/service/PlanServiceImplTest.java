package com.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.exception.ResourceNotFoundException;
import com.flexicharge.flexicharge.mapper.PlanMapper;
import com.flexicharge.flexicharge.model.Plan;
import com.flexicharge.flexicharge.model.dto.PlanDto;
import com.flexicharge.flexicharge.repository.PlanRepository;
import com.flexicharge.flexicharge.service.impl.PlanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanServiceImplTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private PlanMapper planMapper;

    @InjectMocks
    private PlanServiceImpl planService;

    @Test
    void save_ShouldReturnSavedPlan_WhenNameIsUnique() {
        // Arrange
        PlanDto inputDto = new PlanDto();
        inputDto.setName("Gold");

        Plan entity = new Plan();
        entity.setName("Gold");

        when(planRepository.existsByName("Gold")).thenReturn(false);
        when(planMapper.toEntity(inputDto)).thenReturn(entity);
        when(planRepository.save(entity)).thenReturn(entity);
        when(planMapper.toDTO(entity)).thenReturn(inputDto);

        // Act
        PlanDto result = planService.save(inputDto);

        // Assert
        assertNotNull(result);
        assertEquals("Gold", result.getName());
        verify(planRepository).save(any());
    }

    @Test
    void delete_ShouldWork_WhenIdExists() {
        // Arrange
        when(planRepository.existsById("1")).thenReturn(true);

        // Act
        planService.delete("1");

        // Assert
        verify(planRepository).deleteById("1");
    }

    @Test
    void save_ShouldThrowException_WhenNameExists() {
        PlanDto dto = new PlanDto();
        dto.setName("Premium");

        when(planRepository.existsByName("Premium")).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> planService.save(dto));
    }

    @Test
    void findAll_ShouldReturnList() {
        when(planRepository.findAll()).thenReturn(List.of(new Plan()));
        when(planMapper.toDTO(any())).thenReturn(new PlanDto());

        List<PlanDto> result = planService.findAll();

        assertFalse(result.isEmpty());
        verify(planRepository).findAll();
    }

    @Test
    void delete_ShouldThrowException_WhenIdNotFound() {
        when(planRepository.existsById("99")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> planService.delete("99"));
    }
}