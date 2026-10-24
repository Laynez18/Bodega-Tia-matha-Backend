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

import com.bodeguita.bodeguita_backend.dto.TipoUsuarioDto;
import com.bodeguita.bodeguita_backend.entity.TipoUsuario;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.TipoUsuarioService;

@RestController
@RequestMapping("/api/tipos-usuario")
public class TipoUsuarioController {

    private final TipoUsuarioService tipoUsuarioService;

    public TipoUsuarioController(TipoUsuarioService tipoUsuarioService) {
        this.tipoUsuarioService = tipoUsuarioService;
    }

    @GetMapping
    public List<TipoUsuarioDto> listar() {
        return tipoUsuarioService.listar();
    }

    @GetMapping("/{id}")
    public TipoUsuarioDto obtener(@PathVariable Long id) {
        return tipoUsuarioService.obtener(id);
    }

    @RequierePermiso("tipo-usuario:crear")
    @PostMapping
    public TipoUsuarioDto crear(@RequestBody TipoUsuario entidad) {
        return tipoUsuarioService.crear(entidad);
    }

    @RequierePermiso("tipo-usuario:editar")
    @PutMapping("/{id}")
    public TipoUsuarioDto actualizar(@PathVariable Long id, @RequestBody TipoUsuario entidad) {
        return tipoUsuarioService.actualizar(id, entidad);
    }

    @RequierePermiso("tipo-usuario:eliminar")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        tipoUsuarioService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
