package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ProductoDto;
import com.bodeguita.bodeguita_backend.dto.ProductoRequest;
import com.bodeguita.bodeguita_backend.entity.Categoria;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.repository.CategoriaRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoDto> listar() {
        return productoRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public ProductoDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public ProductoDto crear(ProductoRequest request) {
        if (productoRepository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new ResponseStatusException(CONFLICT, "El codigo del producto ya esta registrado.");
        }
        Categoria categoria = categoriaRepository.findByIdAndEstado(request.idCategoria(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoria no encontrada: " + request.idCategoria() + "."));
        Producto entidad = new Producto();
        entidad.setCategoria(categoria);
        entidad.setCodigo(request.codigo());
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setPrecioVenta(request.precioVenta());
        entidad.setStock(request.stock());
        entidad.setStockMinimo(request.stockMinimo());
        entidad.setUnidadMedida(request.unidadMedida());
        entidad = productoRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public ProductoDto actualizar(Long id, ProductoRequest request) {
        Producto entidad = buscarActivo(id);
        if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(request.codigo(), id)) {
            throw new ResponseStatusException(CONFLICT, "El codigo del producto ya esta registrado.");
        }
        Categoria categoria = categoriaRepository.findByIdAndEstado(request.idCategoria(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoria no encontrada: " + request.idCategoria() + "."));
        entidad.setCategoria(categoria);
        entidad.setCodigo(request.codigo());
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setPrecioVenta(request.precioVenta());
        entidad.setStock(request.stock());
        entidad.setStockMinimo(request.stockMinimo());
        entidad.setUnidadMedida(request.unidadMedida());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Producto buscarActivo(Long id) {
        return productoRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + id + "."));
    }

    private ProductoDto aDto(Producto entidad) {
        Categoria c = entidad.getCategoria();
        return new ProductoDto(
                entidad.getId(),
                c.getId(),
                c.getNombre(),
                entidad.getCodigo(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getPrecioVenta(),
                entidad.getStock(),
                entidad.getStockMinimo(),
                entidad.getUnidadMedida()
        );
    }
}



