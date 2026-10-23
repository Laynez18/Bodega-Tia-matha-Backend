package com.bodeguita.bodeguita_backend.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.ProductoDto;
import com.bodeguita.bodeguita_backend.dto.ProductoRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@RequierePermiso("MAN_PRODUCTO")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public Page<ProductoDto> listar(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Long idMarca,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "false") boolean stockBajo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productoService.listar(search, idCategoria, idMarca, estado, stockBajo, page, size);
    }

    @GetMapping("/{idProducto}")
    public ProductoDto obtener(@PathVariable Long idProducto) {
        return productoService.obtener(idProducto);
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoDto creado = productoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idProducto}")
                .buildAndExpand(creado.idProducto())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idProducto}")
    public ProductoDto actualizar(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(idProducto, request);
    }

    @PatchMapping("/{idProducto}/estado")
    public ProductoDto cambiarEstado(
            @PathVariable Long idProducto,
            @Valid @RequestBody EstadoRequest request) {
        return productoService.cambiarEstado(idProducto, request);
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idProducto) {
        productoService.desactivar(idProducto);
        return ResponseEntity.noContent().build();
    }
}
