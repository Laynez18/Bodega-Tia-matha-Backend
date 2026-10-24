package com.bodeguita.bodeguita_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.FacturaDto;
import com.bodeguita.bodeguita_backend.dto.FacturaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.FacturaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @RequierePermiso("factura:emitir")
    @PostMapping("/venta/{idVenta}")
    public ResponseEntity<FacturaDto> emitir(@PathVariable Long idVenta, @Valid @RequestBody FacturaRequest request) {
        FacturaDto factura = facturaService.emitir(idVenta, request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(factura.id()).toUri()).body(factura);
    }
}
