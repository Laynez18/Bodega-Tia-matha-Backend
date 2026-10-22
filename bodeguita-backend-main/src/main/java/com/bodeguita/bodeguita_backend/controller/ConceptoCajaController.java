package com.bodeguita.bodeguita_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.ConceptoCajaDto;
import com.bodeguita.bodeguita_backend.dto.ConceptoCajaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.ConceptoCajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/conceptos-caja")
public class ConceptoCajaController {

    private final ConceptoCajaService conceptoCajaService;

    public ConceptoCajaController(ConceptoCajaService conceptoCajaService) {
        this.conceptoCajaService = conceptoCajaService;
    }

    @GetMapping
    public List<ConceptoCajaDto> listar() {
        return conceptoCajaService.listar();
    }

    @GetMapping("/{id}")
    public ConceptoCajaDto obtener(@PathVariable Long id) {
        return conceptoCajaService.obtener(id);
    }

    @RequierePermiso("concepto-caja:crear")
    @PostMapping
    public ResponseEntity<ConceptoCajaDto> crear(@Valid @RequestBody ConceptoCajaRequest request) {
        ConceptoCajaDto creado = conceptoCajaService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("concepto-caja:editar")
    @PutMapping("/{id}")
    public ConceptoCajaDto actualizar(@PathVariable Long id, @Valid @RequestBody ConceptoCajaRequest request) {
        return conceptoCajaService.actualizar(id, request);
    }

    @RequierePermiso("concepto-caja:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        conceptoCajaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
