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

import com.bodeguita.bodeguita_backend.dto.TipoMovimientoInvDto;
import com.bodeguita.bodeguita_backend.entity.TipoMovimientoInv;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.TipoMovimientoInvService;

@RestController
@RequestMapping("/api/tipos-movimiento-inv")
public class TipoMovimientoInvController {

    private final TipoMovimientoInvService tipoMovimientoInvService;

    public TipoMovimientoInvController(TipoMovimientoInvService tipoMovimientoInvService) {
        this.tipoMovimientoInvService = tipoMovimientoInvService;
    }

    @GetMapping
    public List<TipoMovimientoInvDto> listar() {
        return tipoMovimientoInvService.listar();
    }

    @GetMapping("/{id}")
    public TipoMovimientoInvDto obtener(@PathVariable Long id) {
        return tipoMovimientoInvService.obtener(id);
    }

    @RequierePermiso("tipo-movimiento-inv:crear")
    @PostMapping
    public TipoMovimientoInvDto crear(@RequestBody TipoMovimientoInv entidad) {
        return tipoMovimientoInvService.crear(entidad);
    }

    @RequierePermiso("tipo-movimiento-inv:editar")
    @PutMapping("/{id}")
    public TipoMovimientoInvDto actualizar(@PathVariable Long id, @RequestBody TipoMovimientoInv entidad) {
        return tipoMovimientoInvService.actualizar(id, entidad);
    }

    @RequierePermiso("tipo-movimiento-inv:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        tipoMovimientoInvService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
