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

import com.bodeguita.bodeguita_backend.dto.CargoDto;
import com.bodeguita.bodeguita_backend.dto.CargoRequest;
import com.bodeguita.bodeguita_backend.service.CargoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cargos")
public class CargoController {

    private final CargoService cargoService;

    public CargoController(CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public List<CargoDto> listar() {
        return cargoService.listar();
    }

    @GetMapping("/{idCargo}")
    public CargoDto obtener(@PathVariable Long idCargo) {
        return cargoService.obtener(idCargo);
    }

    @PostMapping
    public ResponseEntity<CargoDto> crear(@Valid @RequestBody CargoRequest request) {
        CargoDto creado = cargoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idCargo}")
                .buildAndExpand(creado.idCargo())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/{idCargo}")
    public CargoDto actualizar(@PathVariable Long idCargo, @Valid @RequestBody CargoRequest request) {
        return cargoService.actualizar(idCargo, request);
    }

    @DeleteMapping("/{idCargo}")
    public ResponseEntity<Void> desactivar(@PathVariable Long idCargo) {
        cargoService.desactivar(idCargo);
        return ResponseEntity.noContent().build();
    }
}
