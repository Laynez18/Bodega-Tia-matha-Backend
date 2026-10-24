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

import com.bodeguita.bodeguita_backend.dto.DistritoDto;
import com.bodeguita.bodeguita_backend.dto.DistritoRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.DistritoService;

@RestController
@RequestMapping("/api/distritos")
public class DistritoController {

    private final DistritoService distritoService;

    public DistritoController(DistritoService distritoService) {
        this.distritoService = distritoService;
    }

    @GetMapping
    public List<DistritoDto> listar() {
        return distritoService.listar();
    }

    @GetMapping("/{id}")
    public DistritoDto obtener(@PathVariable Long id) {
        return distritoService.obtener(id);
    }

    @RequierePermiso("distrito:crear")
    @PostMapping
    public DistritoDto crear(@Valid @RequestBody DistritoRequest request) {
        return distritoService.crear(request);
    }

    @RequierePermiso("distrito:editar")
    @PutMapping("/{id}")
    public DistritoDto actualizar(@PathVariable Long id, @Valid @RequestBody DistritoRequest request) {
        return distritoService.actualizar(id, request);
    }

    @RequierePermiso("distrito:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        distritoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
