package com.bodeguita.bodeguita_backend.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.bodeguita.bodeguita_backend.dto.ClienteDto;
import com.bodeguita.bodeguita_backend.dto.ClienteRequest;
import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.ClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
@RequierePermiso("MAN_CLIENTE")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteDto> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{idCliente}")
    public ClienteDto obtener(@PathVariable Long idCliente) {
        return clienteService.obtener(idCliente);
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crear(@Valid @RequestBody ClienteRequest request) {
        ClienteDto creado = clienteService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idCliente}")
                .buildAndExpand(creado.idCliente())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idCliente}")
    public ClienteDto actualizar(@PathVariable Long idCliente, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(idCliente, request);
    }

    @PatchMapping("/{idCliente}/estado")
    public ClienteDto cambiarEstado(@PathVariable Long idCliente, @Valid @RequestBody EstadoRequest request) {
        return clienteService.cambiarEstado(idCliente, request);
    }

    @DeleteMapping("/{idCliente}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idCliente) {
        clienteService.desactivar(idCliente);
        return ResponseEntity.noContent().build();
    }
}
