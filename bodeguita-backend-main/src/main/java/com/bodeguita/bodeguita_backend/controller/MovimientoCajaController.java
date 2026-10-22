package com.bodeguita.bodeguita_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.MovimientoCajaDto;
import com.bodeguita.bodeguita_backend.dto.MovimientoCajaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.MovimientoCajaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimientos-caja")
public class MovimientoCajaController {

    private final MovimientoCajaService movimientoCajaService;

    public MovimientoCajaController(MovimientoCajaService movimientoCajaService) {
        this.movimientoCajaService = movimientoCajaService;
    }

    @GetMapping
    public List<MovimientoCajaDto> listar() {
        return movimientoCajaService.listar();
    }

    @GetMapping("/{id}")
    public MovimientoCajaDto obtener(@PathVariable Long id) {
        return movimientoCajaService.obtener(id);
    }

    @RequierePermiso("movimiento-caja:crear")
    @PostMapping
    public ResponseEntity<MovimientoCajaDto> crear(@Valid @RequestBody MovimientoCajaRequest request) {
        MovimientoCajaDto creado = movimientoCajaService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("movimiento-caja:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        movimientoCajaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
