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

import com.bodeguita.bodeguita_backend.dto.CajaDto;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.CajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    @GetMapping
    public List<CajaDto> listar() {
        return cajaService.listar();
    }

    @GetMapping("/{id}")
    public CajaDto obtener(@PathVariable Long id) {
        return cajaService.obtener(id);
    }

    @RequierePermiso("caja:crear")
    @PostMapping
    public ResponseEntity<CajaDto> crear(@Valid @RequestBody CajaDto request) {
        CajaDto creado = cajaService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("caja:editar")
    @PutMapping("/{id}")
    public CajaDto actualizar(@PathVariable Long id, @Valid @RequestBody CajaDto request) {
        return cajaService.actualizar(id, request);
    }

    @RequierePermiso("caja:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        cajaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
