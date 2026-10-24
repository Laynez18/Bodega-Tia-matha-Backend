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

import com.bodeguita.bodeguita_backend.dto.DepartamentoDto;
import com.bodeguita.bodeguita_backend.entity.Departamento;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.DepartamentoService;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @GetMapping
    public List<DepartamentoDto> listar() {
        return departamentoService.listar();
    }

    @GetMapping("/{id}")
    public DepartamentoDto obtener(@PathVariable Long id) {
        return departamentoService.obtener(id);
    }

    @RequierePermiso("departamento:crear")
    @PostMapping
    public DepartamentoDto crear(@RequestBody Departamento entidad) {
        return departamentoService.crear(entidad);
    }

    @RequierePermiso("departamento:editar")
    @PutMapping("/{id}")
    public DepartamentoDto actualizar(@PathVariable Long id, @RequestBody Departamento entidad) {
        return departamentoService.actualizar(id, entidad);
    }

    @RequierePermiso("departamento:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        departamentoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
