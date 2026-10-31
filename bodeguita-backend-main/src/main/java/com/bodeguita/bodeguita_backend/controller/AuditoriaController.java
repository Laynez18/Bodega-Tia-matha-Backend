package com.bodeguita.bodeguita_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bodeguita.bodeguita_backend.dto.AuditoriaDto;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.AuditoriaService;

@RestController
@RequestMapping("/api/auditoria")
@RequierePermiso("SEG_AUDITORIA")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaDto>> listar() {
        return ResponseEntity.ok(auditoriaService.listar());
    }
}
