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
    private final AuditoriaService auditoriaService;

    public TipoIdentidadService(
            TipoIdentidadRepository tipoIdentidadRepository,
            AuditoriaService auditoriaService) {
        this.tipoIdentidadRepository = tipoIdentidadRepository;
        this.auditoriaService = auditoriaService;
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
        TipoIdentidad guardado = tipoIdentidadRepository.save(tipo);
        auditoriaService.registrar(
                "tipo_identidad", "INSERT", guardado.getIdTipoIdentidad(), null, resumen(guardado));
        return aDto(guardado);
    }

    @Transactional
    public TipoIdentidadDto actualizar(Long idTipoIdentidad, TipoIdentidadRequest request) {
        TipoIdentidad tipo = buscarActivo(idTipoIdentidad);
        String anterior = resumen(tipo);
        actualizar(tipo, request);
        auditoriaService.registrar(
                "tipo_identidad", "UPDATE", idTipoIdentidad, anterior, resumen(tipo));
        return aDto(tipo);
    }

    @Transactional
    public void desactivar(Long idTipoIdentidad) {
        TipoIdentidad tipo = buscarActivo(idTipoIdentidad);
        String anterior = resumen(tipo);
        tipo.setEstado(Boolean.FALSE);
        auditoriaService.registrar(
                "tipo_identidad", "DESACTIVAR", idTipoIdentidad, anterior, resumen(tipo));
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

    private String resumen(TipoIdentidad tipo) {
        return "nTipoIdentidad=" + tipo.getNTipoIdentidad()
                + "; abreviatura=" + texto(tipo.getAbreviatura())
                + "; longitud=" + texto(tipo.getLongitud())
                + "; estado=" + (Boolean.TRUE.equals(tipo.getEstado()) ? "1" : "0");
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private ResponseStatusException noEncontrado(Long idTipoIdentidad) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Tipo de identidad no encontrado: " + idTipoIdentidad + ".");
    }
}
