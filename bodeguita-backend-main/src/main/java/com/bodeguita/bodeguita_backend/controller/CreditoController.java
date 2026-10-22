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

import com.bodeguita.bodeguita_backend.dto.CreditoDto;
import com.bodeguita.bodeguita_backend.dto.CreditoRequest;
import com.bodeguita.bodeguita_backend.dto.PagoCreditoRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.CreditoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/creditos")
public class CreditoController {

    private final CreditoService creditoService;

    public CreditoController(CreditoService creditoService) {
        this.creditoService = creditoService;
    }

    @GetMapping
    public List<CreditoDto> listar() {
        return creditoService.listar();
    }

    @GetMapping("/{id}")
    public CreditoDto obtener(@PathVariable Long id) {
        return creditoService.obtener(id);
    }

    @RequierePermiso("credito:crear")
    @PostMapping
    public ResponseEntity<CreditoDto> crear(@Valid @RequestBody CreditoRequest request) {
        CreditoDto creado = creditoService.crear(request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri()).body(creado);
    }

    @RequierePermiso("credito:cobrar")
    @PostMapping("/{idCredito}/pagos")
    public CreditoDto registrarPago(@PathVariable Long idCredito, @Valid @RequestBody PagoCreditoRequest request) {
        return creditoService.registrarPago(idCredito, request);
    }

    @RequierePermiso("credito:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        creditoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
