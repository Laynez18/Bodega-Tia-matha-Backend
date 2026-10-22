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

import com.bodeguita.bodeguita_backend.dto.LoteDto;
import com.bodeguita.bodeguita_backend.dto.LoteRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.LoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @GetMapping
    public List<LoteDto> listar() {
        return loteService.listar();
    }

    @GetMapping("/{id}")
    public LoteDto obtener(@PathVariable Long id) {
        return loteService.obtener(id);
    }

    @RequierePermiso("lote:crear")
    @PostMapping
    public ResponseEntity<LoteDto> crear(@Valid @RequestBody LoteRequest request) {
        LoteDto creado = loteService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("lote:editar")
    @PutMapping("/{id}")
    public LoteDto actualizar(@PathVariable Long id, @Valid @RequestBody LoteRequest request) {
        return loteService.actualizar(id, request);
    }

    @RequierePermiso("lote:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        loteService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
