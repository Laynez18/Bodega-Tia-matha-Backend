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

import com.bodeguita.bodeguita_backend.dto.AperturaCajaDto;
import com.bodeguita.bodeguita_backend.dto.AperturaCajaCierreRequest;
import com.bodeguita.bodeguita_backend.dto.AperturaCajaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.AperturaCajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/aperturas-caja")
public class AperturaCajaController {

    private final AperturaCajaService aperturaCajaService;

    public AperturaCajaController(AperturaCajaService aperturaCajaService) {
        this.aperturaCajaService = aperturaCajaService;
    }

    @GetMapping
    public List<AperturaCajaDto> listar() {
        return aperturaCajaService.listar();
    }

    @GetMapping("/{id}")
    public AperturaCajaDto obtener(@PathVariable Long id) {
        return aperturaCajaService.obtener(id);
    }

    @RequierePermiso("apertura-caja:crear")
    @PostMapping
    public ResponseEntity<AperturaCajaDto> crear(@Valid @RequestBody AperturaCajaRequest request) {
        AperturaCajaDto creado = aperturaCajaService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("apertura-caja:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        aperturaCajaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @RequierePermiso("apertura-caja:editar")
    @PutMapping("/{id}")
    public AperturaCajaDto cerrar(@PathVariable Long id, @Valid @RequestBody AperturaCajaCierreRequest request) {
        return aperturaCajaService.cerrar(id, request);
    }
}
