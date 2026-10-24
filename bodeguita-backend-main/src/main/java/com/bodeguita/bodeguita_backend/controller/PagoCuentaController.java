package com.bodeguita.bodeguita_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.PagoCuentaDto;
import com.bodeguita.bodeguita_backend.dto.PagoCuentaRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.PagoCuentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuentas-cobrar/{idCuenta}/pagos")
public class PagoCuentaController {

    private final PagoCuentaService pagoCuentaService;

    public PagoCuentaController(PagoCuentaService pagoCuentaService) {
        this.pagoCuentaService = pagoCuentaService;
    }

    @RequierePermiso("pago-cuenta:registrar")
    @PostMapping
    public ResponseEntity<PagoCuentaDto> registrar(@PathVariable Long idCuenta, @Valid @RequestBody PagoCuentaRequest request) {
        PagoCuentaDto pago = pagoCuentaService.registrar(idCuenta, request);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(pago.id()).toUri()).body(pago);
    }

    @RequierePermiso("pago-cuenta:ver")
    @GetMapping("/{id}")
    public PagoCuentaDto obtener(@PathVariable Long idCuenta, @PathVariable Long id) {
        return pagoCuentaService.obtener(id);
    }
}
