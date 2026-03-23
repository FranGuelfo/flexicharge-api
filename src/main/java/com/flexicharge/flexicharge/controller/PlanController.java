package com.flexicharge.flexicharge.controller;


import com.flexicharge.flexicharge.model.dto.PlanDto;
import com.flexicharge.flexicharge.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Planes", description = "Gestión de planes de suscripción")
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "Obtener todos los planes")
    @GetMapping
    public ResponseEntity<List<PlanDto>> getAllPlans() {
        return ResponseEntity.ok(planService.findAll());
    }

    @Operation(summary = "Crear un nuevo plan")
    @PostMapping
    public ResponseEntity<PlanDto> create(@RequestBody PlanDto plan) {
        return new ResponseEntity<>(planService.save(plan), HttpStatus.CREATED);
    }

    @Operation(summary = "Eliminar un plan")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
