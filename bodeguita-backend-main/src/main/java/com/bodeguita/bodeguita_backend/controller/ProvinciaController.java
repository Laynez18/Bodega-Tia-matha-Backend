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

import com.bodeguita.bodeguita_backend.dto.ProvinciaDto;
import com.bodeguita.bodeguita_backend.entity.Provincia;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.ProvinciaService;

@RestController
@RequestMapping("/api/provincias")
public class ProvinciaController {

    private final ProvinciaService provinciaService;

    public ProvinciaController(ProvinciaService provinciaService) {
        this.provinciaService = provinciaService;
    }

    @GetMapping
    public List<ProvinciaDto> listar() {
        return provinciaService.listar();
    }

    @GetMapping("/{id}")
    public ProvinciaDto obtener(@PathVariable Long id) {
        return provinciaService.obtener(id);
    }

    @RequierePermiso("provincia:crear")
    @PostMapping
    public ProvinciaDto crear(@RequestBody Provincia entidad) {
        return provinciaService.crear(entidad);
    }

    @RequierePermiso("provincia:editar")
    @PutMapping("/{id}")
    public ProvinciaDto actualizar(@PathVariable Long id, @RequestBody Provincia entidad) {
        return provinciaService.actualizar(id, entidad);
    }

    @RequierePermiso("provincia:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        provinciaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
