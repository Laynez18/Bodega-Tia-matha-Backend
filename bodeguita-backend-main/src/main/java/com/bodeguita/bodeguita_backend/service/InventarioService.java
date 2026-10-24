package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.InventarioDto;
import com.bodeguita.bodeguita_backend.entity.Almacen;
import com.bodeguita.bodeguita_backend.entity.Inventario;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.repository.AlmacenRepository;
import com.bodeguita.bodeguita_backend.repository.InventarioRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;

    public InventarioService(InventarioRepository inventarioRepository, ProductoRepository productoRepository,
            AlmacenRepository almacenRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
    }

    public List<InventarioDto> listar() {
        return inventarioRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public InventarioDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public InventarioDto crear(Inventario entidad) {
        return aDto(inventarioRepository.save(entidad));
    }

    @Transactional
    public InventarioDto actualizar(Long id, Inventario entidad) {
        Inventario existente = buscarActivo(id);
        existente.setProducto(entidad.getProducto());
        existente.setAlmacen(entidad.getAlmacen());
        existente.setStock(entidad.getStock());
        existente.setStockReservado(entidad.getStockReservado());
        existente.setUbicacionFisica(entidad.getUbicacionFisica());
        return aDto(inventarioRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Inventario buscarActivo(Long id) {
        return inventarioRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Inventario no encontrado: " + id + "."));
    }

    private InventarioDto aDto(Inventario entidad) {
        return new InventarioDto(entidad.getId(),
                entidad.getProducto() != null ? entidad.getProducto().getId() : null,
                entidad.getAlmacen() != null ? entidad.getAlmacen().getId() : null,
                entidad.getStock(), entidad.getStockReservado(), entidad.getUbicacionFisica(), entidad.getEstado());
    }
}
