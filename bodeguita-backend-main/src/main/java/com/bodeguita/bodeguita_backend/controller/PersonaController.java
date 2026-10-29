package com.bodeguita.bodeguita_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import com.bodeguita.bodeguita_backend.security.Publico;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.security.SinCsrf;
import com.bodeguita.bodeguita_backend.service.PersonaService;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public List<PersonaDto> listar() {
        return personaService.listar();
    }

    @Publico
    @SinCsrf
    @GetMapping("/consultar-dni/{dni}")
    public DniResponse consultarDni(@PathVariable String dni) {
        return personaService.consultarDni(dni);
    }

    @Publico
    @SinCsrf
    @GetMapping("/consultar-ruc/{ruc}")
    public RucResponse consultarRuc(@PathVariable String ruc) {
        return personaService.consultarRuc(ruc);
    }

    @RequierePermiso("persona:crear")
    @PostMapping("/desde-dni/{dni}")
    public PersonaDto crearDesdeDni(@PathVariable String dni) {
        return personaService.crearDesdeDni(dni, null, null);
    }

    @RequierePermiso("persona:crear")
    @PostMapping("/desde-ruc/{ruc}")
    public PersonaDto crearDesdeRuc(@PathVariable String ruc) {
        return personaService.crearDesdeRuc(ruc, null, null);
    }
}
