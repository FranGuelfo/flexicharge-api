package com.flexicharge.flexicharge.mapper;

import com.flexicharge.flexicharge.model.Plan;
import com.flexicharge.flexicharge.model.dto.PlanDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    PlanDto toDTO(Plan plan);

    Plan toEntity(PlanDto planDto);
}
