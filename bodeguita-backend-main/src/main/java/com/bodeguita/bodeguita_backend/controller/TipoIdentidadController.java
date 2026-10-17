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

import com.bodeguita.bodeguita_backend.dto.TipoIdentidadDto;
import com.bodeguita.bodeguita_backend.dto.TipoIdentidadRequest;
import com.bodeguita.bodeguita_backend.service.TipoIdentidadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-identidad")
public class TipoIdentidadController {

    private final TipoIdentidadService tipoIdentidadService;

    public TipoIdentidadController(TipoIdentidadService tipoIdentidadService) {
        this.tipoIdentidadService = tipoIdentidadService;
    }

    @GetMapping
    public List<TipoIdentidadDto> listar() {
        return tipoIdentidadService.listar();
    }

    @GetMapping("/{idTipoIdentidad}")
    public TipoIdentidadDto obtener(@PathVariable Long idTipoIdentidad) {
        return tipoIdentidadService.obtener(idTipoIdentidad);
    }

    @PostMapping
    public ResponseEntity<TipoIdentidadDto> crear(@Valid @RequestBody TipoIdentidadRequest request) {
        TipoIdentidadDto creado = tipoIdentidadService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idTipoIdentidad}")
                .buildAndExpand(creado.idTipoIdentidad())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idTipoIdentidad}")
    public TipoIdentidadDto actualizar(
            @PathVariable Long idTipoIdentidad,
            @Valid @RequestBody TipoIdentidadRequest request) {
        return tipoIdentidadService.actualizar(idTipoIdentidad, request);
    }

    @DeleteMapping("/{idTipoIdentidad}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idTipoIdentidad) {
        tipoIdentidadService.desactivar(idTipoIdentidad);
        return ResponseEntity.noContent().build();
    }
}
