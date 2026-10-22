package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.entity.Caja;
import com.bodeguita.bodeguita_backend.repository.CajaRepository;

import com.bodeguita.bodeguita_backend.dto.CajaDto;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CajaService {

    private final CajaRepository cajaRepository;

    public CajaService(CajaRepository cajaRepository) {
        this.cajaRepository = cajaRepository;
    }

    public List<CajaDto> listar() {
        return cajaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public CajaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public CajaDto crear(CajaDto request) {
        if (cajaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "La caja ya esta registrada.");
        }
        Caja entidad = new Caja();
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setUbicacion(request.ubicacion());
        entidad.setSerieTerminal(request.serieTerminal());
        entidad.setMoneda(request.moneda());
        entidad.setMontoBase(request.montoBase());
        entidad = cajaRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public CajaDto actualizar(Long id, CajaDto request) {
        Caja entidad = buscarActivo(id);
        if (cajaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "La caja ya esta registrada.");
        }
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setUbicacion(request.ubicacion());
        entidad.setSerieTerminal(request.serieTerminal());
        entidad.setMoneda(request.moneda());
        entidad.setMontoBase(request.montoBase());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado(Boolean.FALSE);
    }

    private Caja buscarActivo(Long id) {
        return cajaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Caja no encontrada: " + id + "."));
    }

    private CajaDto aDto(Caja entidad) {
        return new CajaDto(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getUbicacion(),
                entidad.getSerieTerminal(),
                entidad.getMoneda(),
                entidad.getMontoBase(),
                entidad.getAperturada()
        );
    }
}
