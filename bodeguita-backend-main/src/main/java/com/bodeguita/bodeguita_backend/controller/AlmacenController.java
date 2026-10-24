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

import com.bodeguita.bodeguita_backend.dto.AlmacenDto;
import com.bodeguita.bodeguita_backend.dto.AlmacenRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.AlmacenService;

@RestController
@RequestMapping("/api/almacenes")
public class AlmacenController {

    private final AlmacenService almacenService;

    public AlmacenController(AlmacenService almacenService) {
        this.almacenService = almacenService;
    }

    @GetMapping
    public List<AlmacenDto> listar() {
        return almacenService.listar();
    }

    @GetMapping("/{id}")
    public AlmacenDto obtener(@PathVariable Long id) {
        return almacenService.obtener(id);
    }

    @RequierePermiso("almacen:crear")
    @PostMapping
    public AlmacenDto crear(@Valid @RequestBody AlmacenRequest request) {
        return almacenService.crear(request);
    }

    @RequierePermiso("almacen:editar")
    @PutMapping("/{id}")
    public AlmacenDto actualizar(@PathVariable Long id, @Valid @RequestBody AlmacenRequest request) {
        return almacenService.actualizar(id, request);
    }

    @RequierePermiso("almacen:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        almacenService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
