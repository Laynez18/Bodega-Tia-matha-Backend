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

import com.bodeguita.bodeguita_backend.dto.EmpleadoDto;
import com.bodeguita.bodeguita_backend.dto.EmpleadoRequest;
import com.bodeguita.bodeguita_backend.service.EmpleadoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public List<EmpleadoDto> listar() {
        return empleadoService.listar();
    }

    @GetMapping("/{idEmpleado}")
    public EmpleadoDto obtener(@PathVariable Long idEmpleado) {
        return empleadoService.obtener(idEmpleado);
    }

    @PostMapping
    public ResponseEntity<EmpleadoDto> crear(@Valid @RequestBody EmpleadoRequest request) {
        EmpleadoDto creado = empleadoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idEmpleado}")
                .buildAndExpand(creado.idEmpleado())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idEmpleado}")
    public EmpleadoDto actualizar(@PathVariable Long idEmpleado, @Valid @RequestBody EmpleadoRequest request) {
        return empleadoService.actualizar(idEmpleado, request);
    }

    @DeleteMapping("/{idEmpleado}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idEmpleado) {
        empleadoService.desactivar(idEmpleado);
        return ResponseEntity.noContent().build();
    }
}
