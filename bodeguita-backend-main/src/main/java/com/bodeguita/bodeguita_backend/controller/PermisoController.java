package com.bodeguita.bodeguita_backend.controller;
import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.*;
import com.bodeguita.bodeguita_backend.dto.*;import com.bodeguita.bodeguita_backend.security.RequierePermiso;import com.bodeguita.bodeguita_backend.service.PermisoService;
import jakarta.validation.Valid;
@RestController @RequestMapping("/api/permisos") @RequierePermiso("SEG_ROL")
public class PermisoController {
 private final PermisoService service;public PermisoController(PermisoService service){this.service=service;}
 @GetMapping("/matriz") public MatrizPermisosDto matriz(){return service.matriz();}
 @PutMapping("/matriz") public ResponseEntity<Void> guardar(@Valid @RequestBody MatrizPermisosRequest r){service.guardar(r);return ResponseEntity.noContent().build();}
}
