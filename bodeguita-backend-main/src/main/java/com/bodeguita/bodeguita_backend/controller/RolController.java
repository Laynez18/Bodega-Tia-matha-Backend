package com.bodeguita.bodeguita_backend.controller;
import java.net.URI;import java.util.List;
import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.*;import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.bodeguita.bodeguita_backend.dto.*;import com.bodeguita.bodeguita_backend.security.RequierePermiso;import com.bodeguita.bodeguita_backend.service.RolService;
import jakarta.validation.Valid;
@RestController @RequestMapping("/api/roles") @RequierePermiso("SEG_ROL")
public class RolController {
 private final RolService service; public RolController(RolService service){this.service=service;}
 @GetMapping public List<RolDto> listar(){return service.listar();}
 @GetMapping("/{id}") public RolDto obtener(@PathVariable Long id){return service.obtener(id);}
 @PostMapping public ResponseEntity<RolDto> crear(@Valid @RequestBody RolRequest r){RolDto creado=service.crear(r);URI uri=ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(creado.idRol()).toUri();return ResponseEntity.created(uri).body(creado);}
 @PutMapping("/{id}") public RolDto actualizar(@PathVariable Long id,@Valid @RequestBody RolRequest r){return service.actualizar(id,r);}
 @PatchMapping("/{id}/estado") public RolDto estado(@PathVariable Long id,@Valid @RequestBody EstadoRequest r){return service.cambiarEstado(id,r);}
}
