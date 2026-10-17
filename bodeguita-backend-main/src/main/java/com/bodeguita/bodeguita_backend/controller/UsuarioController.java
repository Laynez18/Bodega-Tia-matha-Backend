package com.bodeguita.bodeguita_backend.controller;

import java.net.URI;
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

import com.bodeguita.bodeguita_backend.dto.RolResumenDto;
import com.bodeguita.bodeguita_backend.dto.TipoUsuarioDto;
import com.bodeguita.bodeguita_backend.dto.UsuarioDto;
import com.bodeguita.bodeguita_backend.dto.UsuarioRequest;
import com.bodeguita.bodeguita_backend.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioDto> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{idUsuario}")
    public UsuarioDto obtener(@PathVariable Long idUsuario) {
        return usuarioService.obtener(idUsuario);
    }

    @GetMapping("/tipos")
    public List<TipoUsuarioDto> listarTipos() {
        return usuarioService.listarTiposUsuario();
    }

    @GetMapping("/roles")
    public List<RolResumenDto> listarRoles() {
        return usuarioService.listarRoles();
    }

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody UsuarioRequest request) {
        UsuarioDto creado = usuarioService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idUsuario}")
                .buildAndExpand(creado.idUsuario())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idUsuario}")
    public UsuarioDto actualizar(@PathVariable Long idUsuario, @Valid @RequestBody UsuarioRequest request) {
        return usuarioService.actualizar(idUsuario, request);
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idUsuario) {
        usuarioService.desactivar(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
