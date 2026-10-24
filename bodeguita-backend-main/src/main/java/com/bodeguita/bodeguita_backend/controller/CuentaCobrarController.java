package com.bodeguita.bodeguita_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.CuentaCobrarDto;
import com.bodeguita.bodeguita_backend.dto.CuentaCobrarRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.CuentaCobrarService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuentas-cobrar")
public class CuentaCobrarController {

    private final CuentaCobrarService cuentaCobrarService;

    public CuentaCobrarController(CuentaCobrarService cuentaCobrarService) {
        this.cuentaCobrarService = cuentaCobrarService;
    }

    @RequierePermiso("cuenta-cobrar:generar")
    @PostMapping("/venta/{idVenta}")
    public ResponseEntity<CuentaCobrarDto> generar(@PathVariable Long idVenta, @Valid @RequestBody CuentaCobrarRequest request) {
        CuentaCobrarDto cuenta = cuentaCobrarService.generar(idVenta, request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(cuenta.id()).toUri()).body(cuenta);
    }

    @RequierePermiso("cuenta-cobrar:ver")
    @GetMapping("/{id}")
    public CuentaCobrarDto obtener(@PathVariable Long id) {
        return cuentaCobrarService.obtener(id);
    }
}
