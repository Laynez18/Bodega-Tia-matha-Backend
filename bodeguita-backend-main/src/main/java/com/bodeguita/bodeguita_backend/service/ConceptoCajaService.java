package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ConceptoCajaDto;
import com.bodeguita.bodeguita_backend.dto.ConceptoCajaRequest;
import com.bodeguita.bodeguita_backend.entity.ConceptoCaja;
import com.bodeguita.bodeguita_backend.entity.TipoMovimientoCaja;
import com.bodeguita.bodeguita_backend.repository.ConceptoCajaRepository;
import com.bodeguita.bodeguita_backend.repository.TipoMovimientoCajaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ConceptoCajaService {

    private final ConceptoCajaRepository conceptoCajaRepository;
    private final TipoMovimientoCajaRepository tipoMovimientoCajaRepository;

    public ConceptoCajaService(ConceptoCajaRepository conceptoCajaRepository, TipoMovimientoCajaRepository tipoMovimientoCajaRepository) {
        this.conceptoCajaRepository = conceptoCajaRepository;
        this.tipoMovimientoCajaRepository = tipoMovimientoCajaRepository;
    }

    public List<ConceptoCajaDto> listar() {
        return conceptoCajaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public ConceptoCajaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public ConceptoCajaDto crear(ConceptoCajaRequest request) {
        if (conceptoCajaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "El concepto de caja ya esta registrado.");
        }
        TipoMovimientoCaja tipoMovimiento = tipoMovimientoCajaRepository.findByIdAndEstado(request.idTipoMovimiento(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de movimiento no encontrado: " + request.idTipoMovimiento() + "."));
        ConceptoCaja entidad = new ConceptoCaja();
        entidad.setTipoMovimiento(tipoMovimiento);
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setAfectaEfectivo(request.afectaEfectivo());
        entidad = conceptoCajaRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public ConceptoCajaDto actualizar(Long id, ConceptoCajaRequest request) {
        ConceptoCaja entidad = buscarActivo(id);
        if (conceptoCajaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(CONFLICT, "El concepto de caja ya esta registrado.");
        }
        TipoMovimientoCaja tipoMovimiento = tipoMovimientoCajaRepository.findByIdAndEstado(request.idTipoMovimiento(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de movimiento no encontrado: " + request.idTipoMovimiento() + "."));
        entidad.setTipoMovimiento(tipoMovimiento);
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setAfectaEfectivo(request.afectaEfectivo());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private ConceptoCaja buscarActivo(Long id) {
        return conceptoCajaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Concepto de caja no encontrado: " + id + "."));
    }

    private ConceptoCajaDto aDto(ConceptoCaja entidad) {
        return new ConceptoCajaDto(
                entidad.getId(),
                entidad.getTipoMovimiento().getId(),
                entidad.getTipoMovimiento().getNombre(),
                entidad.getNombre(),
                entidad.getDescripcion(),
                entidad.getAfectaEfectivo()
        );
    }
}
