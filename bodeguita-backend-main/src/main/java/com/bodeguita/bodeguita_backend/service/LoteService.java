package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.LoteDto;
import com.bodeguita.bodeguita_backend.dto.LoteRequest;
import com.bodeguita.bodeguita_backend.entity.Almacen;
import com.bodeguita.bodeguita_backend.entity.Lote;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.repository.LoteRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;
import com.bodeguita.bodeguita_backend.repository.AlmacenRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;

    public LoteService(LoteRepository loteRepository, ProductoRepository productoRepository, AlmacenRepository almacenRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
        this.almacenRepository = almacenRepository;
    }

    public List<LoteDto> listar() {
        return loteRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public LoteDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public LoteDto crear(LoteRequest request) {
        Producto producto = productoRepository.findByIdAndEstado(request.idProducto(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + request.idProducto() + "."));
        Almacen almacen = almacenRepository.findByIdAndEstado(request.idAlmacen(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Almacen no encontrado: " + request.idAlmacen() + "."));
        Lote entidad = new Lote();
        entidad.setProducto(producto);
        entidad.setAlmacen(almacen);
        entidad.setNLote(request.nLote());
        entidad.setFProduccion(request.fProduccion());
        entidad.setFVencimiento(request.fVencimiento());
        entidad.setCantidadInicial(request.cantidadInicial());
        entidad.setCantidadActual(request.cantidadActual());
        entidad.setCostoUnitario(request.costoUnitario());
        entidad = loteRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public LoteDto actualizar(Long id, LoteRequest request) {
        Lote entidad = buscarActivo(id);
        Producto producto = productoRepository.findByIdAndEstado(request.idProducto(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + request.idProducto() + "."));
        Almacen almacen = almacenRepository.findByIdAndEstado(request.idAlmacen(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Almacen no encontrado: " + request.idAlmacen() + "."));
        entidad.setProducto(producto);
        entidad.setAlmacen(almacen);
        entidad.setNLote(request.nLote());
        entidad.setFProduccion(request.fProduccion());
        entidad.setFVencimiento(request.fVencimiento());
        entidad.setCantidadInicial(request.cantidadInicial());
        entidad.setCantidadActual(request.cantidadActual());
        entidad.setCostoUnitario(request.costoUnitario());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Lote buscarActivo(Long id) {
        return loteRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Lote no encontrado: " + id + "."));
    }

    private LoteDto aDto(Lote entidad) {
        return new LoteDto(
                entidad.getId(),
                entidad.getProducto().getId(),
                entidad.getProducto().getNombre(),
                entidad.getAlmacen().getId(),
                entidad.getNLote(),
                entidad.getFProduccion(),
                entidad.getFVencimiento(),
                entidad.getCantidadInicial(),
                entidad.getCantidadActual(),
                entidad.getCostoUnitario()
        );
    }
}



