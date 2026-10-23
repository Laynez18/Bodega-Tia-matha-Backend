package com.bodeguita.bodeguita_backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.ProveedorDto;
import com.bodeguita.bodeguita_backend.dto.ProveedorRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.ProveedorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proveedores")
@RequierePermiso("MAN_PROVEEDOR")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @GetMapping
    public List<ProveedorDto> listar() {
        return proveedorService.listar();
    }

    @GetMapping("/{idProveedor}")
    public ProveedorDto obtener(@PathVariable Long idProveedor) {
        return proveedorService.obtener(idProveedor);
    }

    @PostMapping
    public ResponseEntity<ProveedorDto> crear(@Valid @RequestBody ProveedorRequest request) {
        ProveedorDto creado = proveedorService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idProveedor}")
                .buildAndExpand(creado.idProveedor())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idProveedor}")
    public ProveedorDto actualizar(@PathVariable Long idProveedor, @Valid @RequestBody ProveedorRequest request) {
        return proveedorService.actualizar(idProveedor, request);
    }

    @PatchMapping("/{idProveedor}/estado")
    public ProveedorDto cambiarEstado(@PathVariable Long idProveedor, @Valid @RequestBody EstadoRequest request) {
        return proveedorService.cambiarEstado(idProveedor, request);
    }

    @DeleteMapping("/{idProveedor}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idProveedor) {
        proveedorService.desactivar(idProveedor);
        return ResponseEntity.noContent().build();
    }
}
