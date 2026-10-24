package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.MovimientoInventarioDto;
import com.bodeguita.bodeguita_backend.dto.MovimientoInventarioRequest;
import com.bodeguita.bodeguita_backend.entity.MovimientoInventario;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.repository.MovimientoInventarioRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final ProductoRepository productoRepository;

    public MovimientoInventarioService(MovimientoInventarioRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<MovimientoInventarioDto> listar() {
        return repository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public MovimientoInventarioDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public MovimientoInventarioDto crear(MovimientoInventarioRequest request) {
        Producto producto = productoRepository.findByIdAndEstado(request.idProducto(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + request.idProducto() + "."));
        MovimientoInventario entidad = new MovimientoInventario();
        entidad.setProducto(producto);
        entidad.setTipoMovimiento(request.tipoMovimiento());
        entidad.setCantidad(request.cantidad());
        entidad.setStockAnterior(request.stockAnterior());
        entidad.setStockNuevo(request.stockNuevo());
        entidad.setfMovimiento(request.fMovimiento());
        entidad.setReferencia(request.referencia());
        entidad.setObservacion(request.observacion());
        entidad.setValorUnitario(request.valorUnitario());
        entidad = repository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private MovimientoInventario buscarActivo(Long id) {
        return repository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Movimiento de inventario no encontrado: " + id + "."));
    }

    private MovimientoInventarioDto aDto(MovimientoInventario entidad) {
        return new MovimientoInventarioDto(
                entidad.getId(),
                entidad.getProducto().getId(),
                entidad.getProducto().getNombre(),
                entidad.getTipoMovimiento(),
                entidad.getCantidad(),
                entidad.getStockAnterior(),
                entidad.getStockNuevo(),
                entidad.getfMovimiento(),
                entidad.getReferencia(),
                entidad.getObservacion(),
                entidad.getValorUnitario()
        );
    }
}



