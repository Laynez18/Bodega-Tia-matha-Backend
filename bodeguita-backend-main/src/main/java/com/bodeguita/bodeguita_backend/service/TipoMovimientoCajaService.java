package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.TipoMovimientoCajaDto;
import com.bodeguita.bodeguita_backend.dto.TipoMovimientoCajaRequest;
import com.bodeguita.bodeguita_backend.entity.TipoMovimientoCaja;
import com.bodeguita.bodeguita_backend.repository.TipoMovimientoCajaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class TipoMovimientoCajaService {

    private final TipoMovimientoCajaRepository repository;

    public TipoMovimientoCajaService(TipoMovimientoCajaRepository repository) {
        this.repository = repository;
    }

    public List<TipoMovimientoCajaDto> listar() {
        return repository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public TipoMovimientoCajaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public TipoMovimientoCajaDto crear(TipoMovimientoCajaRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "El tipo de movimiento ya esta registrado.");
        }
        TipoMovimientoCaja entidad = new TipoMovimientoCaja();
        entidad.setNombre(request.nombre());
        entidad.setAbreviatura(request.abreviatura());
        entidad.setSigno(request.signo());
        entidad = repository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public TipoMovimientoCajaDto actualizar(Long id, TipoMovimientoCajaRequest request) {
        TipoMovimientoCaja entidad = buscarActivo(id);
        if (repository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(CONFLICT, "El tipo de movimiento ya esta registrado.");
        }
        entidad.setNombre(request.nombre());
        entidad.setAbreviatura(request.abreviatura());
        entidad.setSigno(request.signo());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private TipoMovimientoCaja buscarActivo(Long id) {
        return repository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de movimiento no encontrado: " + id + "."));
    }

    private TipoMovimientoCajaDto aDto(TipoMovimientoCaja entidad) {
        return new TipoMovimientoCajaDto(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getAbreviatura(),
                entidad.getSigno()
        );
    }
}

