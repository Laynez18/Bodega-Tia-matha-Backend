package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.TipoMovimientoInvDto;
import com.bodeguita.bodeguita_backend.entity.TipoMovimientoInv;
import com.bodeguita.bodeguita_backend.repository.TipoMovimientoInvRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class TipoMovimientoInvService {

    private final TipoMovimientoInvRepository tipoMovimientoInvRepository;

    public TipoMovimientoInvService(TipoMovimientoInvRepository tipoMovimientoInvRepository) {
        this.tipoMovimientoInvRepository = tipoMovimientoInvRepository;
    }

    public List<TipoMovimientoInvDto> listar() {
        return tipoMovimientoInvRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public TipoMovimientoInvDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public TipoMovimientoInvDto crear(TipoMovimientoInv entidad) {
        return aDto(tipoMovimientoInvRepository.save(entidad));
    }

    @Transactional
    public TipoMovimientoInvDto actualizar(Long id, TipoMovimientoInv entidad) {
        TipoMovimientoInv existente = buscarActivo(id);
        existente.setNombre(entidad.getNombre());
        existente.setAbreviatura(entidad.getAbreviatura());
        existente.setSigno(entidad.getSigno());
        return aDto(tipoMovimientoInvRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private TipoMovimientoInv buscarActivo(Long id) {
        return tipoMovimientoInvRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de movimiento de inventario no encontrado: " + id + "."));
    }

    private TipoMovimientoInvDto aDto(TipoMovimientoInv entidad) {
        return new TipoMovimientoInvDto(entidad.getId(), entidad.getNombre(), entidad.getAbreviatura(),
                entidad.getSigno(), entidad.getEstado());
    }
}
