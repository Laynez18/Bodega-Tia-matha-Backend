package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.BoletaDto;
import com.bodeguita.bodeguita_backend.dto.BoletaRequest;
import com.bodeguita.bodeguita_backend.entity.Boleta;
import com.bodeguita.bodeguita_backend.entity.Venta;
import com.bodeguita.bodeguita_backend.repository.BoletaRepository;
import com.bodeguita.bodeguita_backend.repository.VentaRepository;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class BoletaService {

    private final BoletaRepository boletaRepository;
    private final VentaRepository ventaRepository;

    public BoletaService(BoletaRepository boletaRepository, VentaRepository ventaRepository) {
        this.boletaRepository = boletaRepository;
        this.ventaRepository = ventaRepository;
    }

    @Transactional
    public BoletaDto emitir(Long idVenta, BoletaRequest request) {
        Venta venta = ventaRepository.findByIdAndEstado(idVenta, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Venta no encontrada: " + idVenta + "."));
        Boleta boleta = new Boleta();
        boleta.setVenta(venta);
        boleta.setFEmision(request.fEmision() != null ? request.fEmision() : LocalDateTime.now());
        boleta.setNumero(request.numero());
        boleta.setSerie(request.serie());
        boleta = boletaRepository.saveAndFlush(boleta);
        return aDto(boleta);
    }

    public BoletaDto obtener(Long id) {
        return aDto(boletaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Boleta no encontrada: " + id + ".")));
    }

    private BoletaDto aDto(Boleta entidad) {
        return new BoletaDto(
                entidad.getId(),
                entidad.getVenta() != null ? entidad.getVenta().getId() : null,
                entidad.getFEmision(),
                entidad.getNumero(),
                entidad.getSerie(),
                entidad.getEstado()
        );
    }
}
