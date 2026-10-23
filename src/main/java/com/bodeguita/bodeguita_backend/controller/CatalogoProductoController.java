package com.bodeguita.bodeguita_backend.controller;

import java.util.List;

import java.net.URI;

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

import com.bodeguita.bodeguita_backend.dto.CategoriaProductoDto;
import com.bodeguita.bodeguita_backend.dto.CategoriaProductoRequest;
import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.MarcaDto;
import com.bodeguita.bodeguita_backend.dto.UnidadMedidaDto;
import com.bodeguita.bodeguita_backend.security.RequierePermiso;
import com.bodeguita.bodeguita_backend.service.CategoriaProductoService;
import com.bodeguita.bodeguita_backend.service.ProductoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CatalogoProductoController {

    private final ProductoService productoService;
    private final CategoriaProductoService categoriaProductoService;

    public CatalogoProductoController(ProductoService productoService, CategoriaProductoService categoriaProductoService) {
        this.productoService = productoService;
        this.categoriaProductoService = categoriaProductoService;
    }

    @GetMapping("/categorias-producto")
    public List<CategoriaProductoDto> listarCategorias() {
        return productoService.listarCategorias();
    }

    @GetMapping("/categorias")
    @RequierePermiso("MAN_PRODUCTO")
    public List<CategoriaProductoDto> listarCategoriasAdmin() {
        return categoriaProductoService.listar();
    }

    @GetMapping("/categorias/{idCategoriaProducto}")
    @RequierePermiso("MAN_PRODUCTO")
    public CategoriaProductoDto obtenerCategoria(@PathVariable Long idCategoriaProducto) {
        return categoriaProductoService.obtener(idCategoriaProducto);
    }

    @PostMapping("/categorias")
    @RequierePermiso("MAN_PRODUCTO")
    public ResponseEntity<CategoriaProductoDto> crearCategoria(@Valid @RequestBody CategoriaProductoRequest request) {
        CategoriaProductoDto creado = categoriaProductoService.crear(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idCategoriaProducto}")
                .buildAndExpand(creado.idCategoriaProducto())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @PutMapping("/categorias/{idCategoriaProducto}")
    @RequierePermiso("MAN_PRODUCTO")
    public CategoriaProductoDto actualizarCategoria(@PathVariable Long idCategoriaProducto, @Valid @RequestBody CategoriaProductoRequest request) {
        return categoriaProductoService.actualizar(idCategoriaProducto, request);
    }

    @PatchMapping("/categorias/{idCategoriaProducto}/estado")
    @RequierePermiso("MAN_PRODUCTO")
    public CategoriaProductoDto cambiarEstadoCategoria(@PathVariable Long idCategoriaProducto, @Valid @RequestBody EstadoRequest request) {
        return categoriaProductoService.cambiarEstado(idCategoriaProducto, request);
    }

    @DeleteMapping("/categorias/{idCategoriaProducto}")
    @RequierePermiso("MAN_PRODUCTO")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable Long idCategoriaProducto) {
        categoriaProductoService.desactivar(idCategoriaProducto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/marcas")
    public List<MarcaDto> listarMarcas() {
        return productoService.listarMarcas();
    }

    @GetMapping("/unidades-medida")
    public List<UnidadMedidaDto> listarUnidades() {
        return productoService.listarUnidades();
    }
}
