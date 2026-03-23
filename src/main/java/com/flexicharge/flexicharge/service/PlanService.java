package com.flexicharge.flexicharge.service;

import com.flexicharge.flexicharge.model.dto.PlanDto;

import java.util.List;

public interface PlanService {

    List<PlanDto> findAll();

    PlanDto save(PlanDto plan);

    void delete(String id);
}
