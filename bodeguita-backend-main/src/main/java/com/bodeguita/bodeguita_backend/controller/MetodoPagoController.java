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

import com.bodeguita.bodeguita_backend.dto.MetodoPagoDto;
import com.bodeguita.bodeguita_backend.dto.MetodoPagoRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.MetodoPagoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/metodos-pago")
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    public MetodoPagoController(MetodoPagoService metodoPagoService) {
        this.metodoPagoService = metodoPagoService;
    }

    @GetMapping
    public List<MetodoPagoDto> listar() {
        return metodoPagoService.listar();
    }

    @GetMapping("/{id}")
    public MetodoPagoDto obtener(@PathVariable Long id) {
        return metodoPagoService.obtener(id);
    }

    @RequierePermiso("metodo-pago:crear")
    @PostMapping
    public ResponseEntity<MetodoPagoDto> crear(@Valid @RequestBody MetodoPagoRequest request) {
        MetodoPagoDto creado = metodoPagoService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("metodo-pago:editar")
    @PutMapping("/{id}")
    public MetodoPagoDto actualizar(@PathVariable Long id, @Valid @RequestBody MetodoPagoRequest request) {
        return metodoPagoService.actualizar(id, request);
    }

    @RequierePermiso("metodo-pago:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        metodoPagoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
