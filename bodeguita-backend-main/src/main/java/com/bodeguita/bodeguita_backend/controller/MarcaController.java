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

import com.bodeguita.bodeguita_backend.dto.MarcaDto;
import com.bodeguita.bodeguita_backend.entity.Marca;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.MarcaService;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @GetMapping
    public List<MarcaDto> listar() {
        return marcaService.listar();
    }

    @GetMapping("/{id}")
    public MarcaDto obtener(@PathVariable Long id) {
        return marcaService.obtener(id);
    }

    @RequierePermiso("marca:crear")
    @PostMapping
    public MarcaDto crear(@RequestBody Marca entidad) {
        return marcaService.crear(entidad);
    }

    @RequierePermiso("marca:editar")
    @PutMapping("/{id}")
    public MarcaDto actualizar(@PathVariable Long id, @RequestBody Marca entidad) {
        return marcaService.actualizar(id, entidad);
    }

    @RequierePermiso("marca:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        marcaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
