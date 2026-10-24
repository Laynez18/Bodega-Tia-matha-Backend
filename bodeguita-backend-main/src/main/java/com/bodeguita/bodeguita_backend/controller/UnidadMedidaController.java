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

import jakarta.validation.Valid;

import com.bodeguita.bodeguita_backend.dto.UnidadMedidaDto;
import com.bodeguita.bodeguita_backend.dto.UnidadMedidaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.UnidadMedidaService;

@RestController
@RequestMapping("/api/unidades-medida")
public class UnidadMedidaController {

    private final UnidadMedidaService unidadMedidaService;

    public UnidadMedidaController(UnidadMedidaService unidadMedidaService) {
        this.unidadMedidaService = unidadMedidaService;
    }

    @GetMapping
    public List<UnidadMedidaDto> listar() {
        return unidadMedidaService.listar();
    }

    @GetMapping("/{id}")
    public UnidadMedidaDto obtener(@PathVariable Long id) {
        return unidadMedidaService.obtener(id);
    }

    @RequierePermiso("unidad-medida:crear")
    @PostMapping
    public UnidadMedidaDto crear(@Valid @RequestBody UnidadMedidaRequest request) {
        return unidadMedidaService.crear(request);
    }

    @RequierePermiso("unidad-medida:editar")
    @PutMapping("/{id}")
    public UnidadMedidaDto actualizar(@PathVariable Long id, @Valid @RequestBody UnidadMedidaRequest request) {
        return unidadMedidaService.actualizar(id, request);
    }

    @RequierePermiso("unidad-medida:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        unidadMedidaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
