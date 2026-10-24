package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.FacturaDto;
import com.bodeguita.bodeguita_backend.dto.FacturaRequest;
import com.bodeguita.bodeguita_backend.entity.Factura;
import com.bodeguita.bodeguita_backend.entity.Venta;
import com.bodeguita.bodeguita_backend.repository.FacturaRepository;
import com.bodeguita.bodeguita_backend.repository.VentaRepository;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;

    public FacturaService(FacturaRepository facturaRepository, VentaRepository ventaRepository) {
        this.facturaRepository = facturaRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public FacturaDto emitir(Long idVenta, FacturaRequest request) {
        Venta venta = ventaRepository.findByIdAndEstado(idVenta, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada: " + idVenta + "."));
        Factura factura = new Factura();
        factura.setVenta(venta);
        factura.setFEmision(request.fEmision() != null ? request.fEmision() : LocalDateTime.now());
        factura.setNumero(request.numero());
        factura.setSerie(request.serie());
        factura = facturaRepository.saveAndFlush(factura);
        return aDto(factura);
    }

    public FacturaDto obtener(Long id) {
        return aDto(facturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Factura no encontrada: " + id + ".")));
    }

    private FacturaDto aDto(Factura entidad) {
        return new FacturaDto(
                entidad.getId(),
                entidad.getVenta() != null ? entidad.getVenta().getId() : null,
                entidad.getFEmision(),
                entidad.getNumero(),
                entidad.getSerie(),
                entidad.getEstado()
        );
    }
}
