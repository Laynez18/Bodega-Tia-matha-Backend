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

import com.bodeguita.bodeguita_backend.dto.PresentacionProductoDto;
import com.bodeguita.bodeguita_backend.entity.PresentacionProducto;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.PresentacionProductoService;

@RestController
@RequestMapping("/api/presentaciones-producto")
public class PresentacionProductoController {

    private final PresentacionProductoService presentacionProductoService;

    public PresentacionProductoController(PresentacionProductoService presentacionProductoService) {
        this.presentacionProductoService = presentacionProductoService;
    }

    @GetMapping
    public List<PresentacionProductoDto> listar() {
        return presentacionProductoService.listar();
    }

    @GetMapping("/{id}")
    public PresentacionProductoDto obtener(@PathVariable Long id) {
        return presentacionProductoService.obtener(id);
    }

    @RequierePermiso("presentacion-producto:crear")
    @PostMapping
    public PresentacionProductoDto crear(@RequestBody PresentacionProducto entidad) {
        return presentacionProductoService.crear(entidad);
    }

    @RequierePermiso("presentacion-producto:editar")
    @PutMapping("/{id}")
    public PresentacionProductoDto actualizar(@PathVariable Long id, @RequestBody PresentacionProducto entidad) {
        return presentacionProductoService.actualizar(id, entidad);
    }

    @RequierePermiso("presentacion-producto:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        presentacionProductoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
