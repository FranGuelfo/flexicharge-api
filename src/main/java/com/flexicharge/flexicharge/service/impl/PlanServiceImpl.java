package com.flexicharge.flexicharge.service.impl;

import com.flexicharge.flexicharge.exception.ResourceNotFoundException;
import com.flexicharge.flexicharge.mapper.PlanMapper;
import com.flexicharge.flexicharge.model.Plan;
import com.flexicharge.flexicharge.model.dto.PlanDto;
import com.flexicharge.flexicharge.repository.PlanRepository;
import com.flexicharge.flexicharge.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    private final PlanMapper planMapper;

    @Override
    public List<PlanDto> findAll() {
        log.info("Obteniendo todos los planes");
        return planRepository.findAll()
                .stream()
                .map(planMapper::toDTO)
                .toList();
    }

    @Override
    public PlanDto save(PlanDto plan) {
        log.info("Guardando un nuevo plan: {}", plan.getName());

        // 1. Validar si ya existe un plan con ese nombre
        if (planRepository.existsByName(plan.getName())) {
        throw new ResourceNotFoundException("Ya existe un plan llamado: " + plan.getName());
        }

        // 2. Convertir DTO a Entidad
        Plan planEntity = planMapper.toEntity(plan);

        // 3. Guardar en MongoDB
        Plan savedPlan = planRepository.save(planEntity);

        // 4. Devolver el DTO con el ID generado por la base de datos
        return planMapper.toDTO(savedPlan);
    }

    @Override
    public void delete(String id) {
        log.info("Eliminando plan con id: {}", id);
        if(!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se encontro el plan con id: " + id);
        }
        planRepository.deleteById(id);
    }
}
