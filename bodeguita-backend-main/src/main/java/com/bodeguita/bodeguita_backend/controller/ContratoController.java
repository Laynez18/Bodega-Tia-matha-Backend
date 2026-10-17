package com.bodeguita.bodeguita_backend.controller;

import java.net.URI;
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

import com.bodeguita.bodeguita_backend.dto.ContratoDto;
import com.bodeguita.bodeguita_backend.dto.ContratoRequest;
import com.bodeguita.bodeguita_backend.service.ContratoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping
    public List<ContratoDto> listar() {
        return contratoService.listar();
    }

    @GetMapping("/{idContrato}")
    public ContratoDto obtener(@PathVariable Long idContrato) {
        return contratoService.obtener(idContrato);
    }

    @PostMapping
    public ResponseEntity<ContratoDto> crear(@Valid @RequestBody ContratoRequest request) {
        ContratoDto creado = contratoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idContrato}")
                .buildAndExpand(creado.idContrato())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idContrato}")
    public ContratoDto actualizar(@PathVariable Long idContrato, @Valid @RequestBody ContratoRequest request) {
        return contratoService.actualizar(idContrato, request);
    }

    @DeleteMapping("/{idContrato}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idContrato) {
        contratoService.desactivar(idContrato);
        return ResponseEntity.noContent().build();
    }
}
