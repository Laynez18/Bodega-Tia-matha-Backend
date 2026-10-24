package com.bodeguita.bodeguita_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.BoletaDto;
import com.bodeguita.bodeguita_backend.dto.BoletaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.BoletaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/boletas")
public class BoletaController {

    private final BoletaService boletaService;

    public BoletaController(BoletaService boletaService) {
        this.boletaService = boletaService;
    }

    @RequierePermiso("boleta:emitir")
    @PostMapping("/venta/{idVenta}")
    public ResponseEntity<BoletaDto> emitir(@PathVariable Long idVenta, @Valid @RequestBody BoletaRequest request) {
        BoletaDto boleta = boletaService.emitir(idVenta, request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(boleta.id()).toUri()).body(boleta);
    }
}
