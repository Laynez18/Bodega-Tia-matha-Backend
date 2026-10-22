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

import com.bodeguita.bodeguita_backend.dto.TipoMovimientoCajaDto;
import com.bodeguita.bodeguita_backend.dto.TipoMovimientoCajaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.TipoMovimientoCajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-movimiento-caja")
public class TipoMovimientoCajaController {

    private final TipoMovimientoCajaService tipoMovimientoCajaService;

    public TipoMovimientoCajaController(TipoMovimientoCajaService tipoMovimientoCajaService) {
        this.tipoMovimientoCajaService = tipoMovimientoCajaService;
    }

    @GetMapping
    public List<TipoMovimientoCajaDto> listar() {
        return tipoMovimientoCajaService.listar();
    }

    @GetMapping("/{id}")
    public TipoMovimientoCajaDto obtener(@PathVariable Long id) {
        return tipoMovimientoCajaService.obtener(id);
    }

    @RequierePermiso("tipo-movimiento-caja:crear")
    @PostMapping
    public ResponseEntity<TipoMovimientoCajaDto> crear(@Valid @RequestBody TipoMovimientoCajaRequest request) {
        TipoMovimientoCajaDto creado = tipoMovimientoCajaService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("tipo-movimiento-caja:editar")
    @PutMapping("/{id}")
    public TipoMovimientoCajaDto actualizar(@PathVariable Long id, @Valid @RequestBody TipoMovimientoCajaRequest request) {
        return tipoMovimientoCajaService.actualizar(id, request);
    }

    @RequierePermiso("tipo-movimiento-caja:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        tipoMovimientoCajaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
