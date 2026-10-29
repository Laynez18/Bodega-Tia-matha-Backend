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

import jakarta.validation.Valid;

import com.bodeguita.bodeguita_backend.dto.EmpresaDto;
import com.bodeguita.bodeguita_backend.dto.EmpresaRequest;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public List<EmpresaDto> listar() {
        return empresaService.listar();
    }

    @GetMapping("/{id}")
    public EmpresaDto obtener(@PathVariable Long id) {
        return empresaService.obtener(id);
    }

    @RequierePermiso("empresa:crear")
    @PostMapping
    public EmpresaDto crear(@Valid @RequestBody EmpresaRequest request) {
        return empresaService.crear(request);
    }

    @RequierePermiso("empresa:crear")
    @PostMapping("/desde-sunat/{ruc}")
    public EmpresaDto crearDesdeSunat(@PathVariable String ruc) {
        return empresaService.crearDesdeSunat(ruc);
    }

    @RequierePermiso("empresa:editar")
    @PutMapping("/{id}")
    public EmpresaDto actualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequest request) {
        return empresaService.actualizar(id, request);
    }

    @RequierePermiso("empresa:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        empresaService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
