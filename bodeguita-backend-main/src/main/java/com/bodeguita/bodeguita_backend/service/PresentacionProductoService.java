package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.PresentacionProductoDto;
import com.bodeguita.bodeguita_backend.entity.PresentacionProducto;
import com.bodeguita.bodeguita_backend.entity.Producto;
import com.bodeguita.bodeguita_backend.entity.UnidadMedida;
import com.bodeguita.bodeguita_backend.repository.PresentacionProductoRepository;
import com.bodeguita.bodeguita_backend.repository.ProductoRepository;
import com.bodeguita.bodeguita_backend.repository.UnidadMedidaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class PresentacionProductoService {

    private final PresentacionProductoRepository presentacionProductoRepository;
    private final ProductoRepository productoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public PresentacionProductoService(PresentacionProductoRepository presentacionProductoRepository,
            ProductoRepository productoRepository, UnidadMedidaRepository unidadMedidaRepository) {
        this.presentacionProductoRepository = presentacionProductoRepository;
        this.productoRepository = productoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    public List<PresentacionProductoDto> listar() {
        return presentacionProductoRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public PresentacionProductoDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public PresentacionProductoDto crear(PresentacionProducto entidad) {
        return aDto(presentacionProductoRepository.save(entidad));
    }

    @Transactional
    public PresentacionProductoDto actualizar(Long id, PresentacionProducto entidad) {
        PresentacionProducto existente = buscarActivo(id);
        existente.setProducto(entidad.getProducto());
        existente.setUnidadMedida(entidad.getUnidadMedida());
        existente.setFactorConversion(entidad.getFactorConversion());
        existente.setEsUnidadBase(entidad.getEsUnidadBase());
        return aDto(presentacionProductoRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private PresentacionProducto buscarActivo(Long id) {
        return presentacionProductoRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Presentacion de producto no encontrada: " + id + "."));
    }

    private PresentacionProductoDto aDto(PresentacionProducto entidad) {
        return new PresentacionProductoDto(entidad.getId(),
                entidad.getProducto() != null ? entidad.getProducto().getId() : null,
                entidad.getUnidadMedida() != null ? entidad.getUnidadMedida().getId() : null,
                entidad.getFactorConversion(), entidad.getEsUnidadBase(), entidad.getEstado());
    }
}
