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

import com.bodeguita.bodeguita_backend.dto.MovimientoInventarioDto;
import com.bodeguita.bodeguita_backend.dto.MovimientoInventarioRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.MovimientoInventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController {

    private final MovimientoInventarioService movimientoInventarioService;

    public MovimientoInventarioController(MovimientoInventarioService movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @GetMapping
    public List<MovimientoInventarioDto> listar() {
        return movimientoInventarioService.listar();
    }

    @GetMapping("/{id}")
    public MovimientoInventarioDto obtener(@PathVariable Long id) {
        return movimientoInventarioService.obtener(id);
    }

    @RequierePermiso("inventario:crear")
    @PostMapping
    public ResponseEntity<MovimientoInventarioDto> crear(@Valid @RequestBody MovimientoInventarioRequest request) {
        MovimientoInventarioDto creado = movimientoInventarioService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("inventario:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        movimientoInventarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
