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

import com.bodeguita.bodeguita_backend.dto.VentaDto;
import com.bodeguita.bodeguita_backend.dto.VentaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<VentaDto> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public VentaDto obtener(@PathVariable Long id) {
        return ventaService.obtener(id);
    }

    @RequierePermiso("venta:crear")
    @PostMapping
    public ResponseEntity<VentaDto> crear(@Valid @RequestBody VentaRequest request) {
        VentaDto creado = ventaService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("venta:editar")
    @PutMapping("/{id}")
    public VentaDto actualizar(@PathVariable Long id, @Valid @RequestBody VentaRequest request) {
        return ventaService.actualizar(id, request);
    }

    @RequierePermiso("venta:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        ventaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
