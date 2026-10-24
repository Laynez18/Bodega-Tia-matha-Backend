package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.LoteDto;
import com.bodeguita.bodeguita_backend.dto.LoteRequest;
import com.bodeguita.bodeguita_backend.entity.Lote;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.repository.LoteRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;

    public LoteService(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
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
        Lote entidad = new Lote();
        entidad.setProducto(producto);
        entidad.setNumeroLote(request.numeroLote());
        entidad.setfVencimiento(request.fVencimiento());
        entidad.setStock(request.stock());
        entidad.setPrecioCosto(request.precioCosto());
        entidad = loteRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public LoteDto actualizar(Long id, LoteRequest request) {
        Lote entidad = buscarActivo(id);
        Producto producto = productoRepository.findByIdAndEstado(request.idProducto(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + request.idProducto() + "."));
        entidad.setProducto(producto);
        entidad.setNumeroLote(request.numeroLote());
        entidad.setfVencimiento(request.fVencimiento());
        entidad.setStock(request.stock());
        entidad.setPrecioCosto(request.precioCosto());
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
                entidad.getNumeroLote(),
                entidad.getfVencimiento(),
                entidad.getStock(),
                entidad.getPrecioCosto()
        );
    }
}



