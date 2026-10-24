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

import com.bodeguita.bodeguita_backend.dto.InventarioDto;
import com.bodeguita.bodeguita_backend.entity.Inventario;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.InventarioService;

@RestController
@RequestMapping("/api/inventarios")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<InventarioDto> listar() {
        return inventarioService.listar();
    }

    @GetMapping("/{id}")
    public InventarioDto obtener(@PathVariable Long id) {
        return inventarioService.obtener(id);
    }

    @RequierePermiso("inventario:crear")
    @PostMapping
    public InventarioDto crear(@RequestBody Inventario entidad) {
        return inventarioService.crear(entidad);
    }

    @RequierePermiso("inventario:editar")
    @PutMapping("/{id}")
    public InventarioDto actualizar(@PathVariable Long id, @RequestBody Inventario entidad) {
        return inventarioService.actualizar(id, entidad);
    }

    @RequierePermiso("inventario:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        inventarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
