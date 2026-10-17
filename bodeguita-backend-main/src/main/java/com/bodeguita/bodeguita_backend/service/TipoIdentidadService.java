package com.bodeguita.bodeguita_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.TipoIdentidadDto;
import com.bodeguita.bodeguita_backend.dto.TipoIdentidadRequest;
import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;
import com.bodeguita.bodeguita_backend.repository.TipoIdentidadRepository;

@Service
@Transactional(readOnly = true)
public class TipoIdentidadService {

    private final TipoIdentidadRepository tipoIdentidadRepository;

    public TipoIdentidadService(TipoIdentidadRepository tipoIdentidadRepository) {
        this.tipoIdentidadRepository = tipoIdentidadRepository;
    }

    public List<TipoIdentidadDto> listar() {
        return tipoIdentidadRepository.findAllActivosOrdenados().stream()
                .map(this::aDto)
                .toList();
    }

    public TipoIdentidadDto obtener(Long idTipoIdentidad) {
        return aDto(buscarActivo(idTipoIdentidad));
    }

    @Transactional
    public TipoIdentidadDto crear(TipoIdentidadRequest request) {
        TipoIdentidad tipo = new TipoIdentidad();
        actualizar(tipo, request);
        tipo.setEstado(Boolean.TRUE);
        return aDto(tipoIdentidadRepository.save(tipo));
    }

    @Transactional
    public TipoIdentidadDto actualizar(Long idTipoIdentidad, TipoIdentidadRequest request) {
        TipoIdentidad tipo = buscarActivo(idTipoIdentidad);
        actualizar(tipo, request);
        return aDto(tipo);
    }

    @Transactional
    public void desactivar(Long idTipoIdentidad) {
        buscarActivo(idTipoIdentidad).setEstado(Boolean.FALSE);
    }

    private TipoIdentidad buscarActivo(Long idTipoIdentidad) {
        return tipoIdentidadRepository.findByIdTipoIdentidadAndEstadoTrue(idTipoIdentidad)
                .orElseThrow(() -> noEncontrado(idTipoIdentidad));
    }

    private void actualizar(TipoIdentidad tipo, TipoIdentidadRequest request) {
        tipo.setNTipoIdentidad(request.nTipoIdentidad().trim());
        tipo.setAbreviatura(normalizarOpcional(request.abreviatura()));
        tipo.setLongitud(request.longitud());
    }

    private TipoIdentidadDto aDto(TipoIdentidad tipo) {
        return new TipoIdentidadDto(
                tipo.getIdTipoIdentidad(),
                tipo.getNTipoIdentidad(),
                tipo.getAbreviatura(),
                tipo.getLongitud());
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private ResponseStatusException noEncontrado(Long idTipoIdentidad) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Tipo de identidad no encontrado: " + idTipoIdentidad + ".");
    }
}
