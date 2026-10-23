package com.bodeguita.bodeguita_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bodeguita.bodeguita_backend.dto.AuditoriaDto;
import com.bodeguita.bodeguita_backend.dto.AuditoriaFiltrosDto;
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
    public List<AuditoriaDto> listar(
            @RequestParam(required = false) Long idUsuario,
            @RequestParam(required = false) String nTabla,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String texto) {
        return auditoriaService.listar(idUsuario, nTabla, accion, desde, hasta, texto);
    }

    @GetMapping("/filtros")
    public AuditoriaFiltrosDto filtros() {
        return auditoriaService.filtros();
    }

    @GetMapping("/{idAuditoria}")
    public AuditoriaDto obtener(@PathVariable Long idAuditoria) {
        return auditoriaService.obtener(idAuditoria);
    }
}
