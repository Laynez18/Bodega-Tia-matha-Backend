package com.bodeguita.bodeguita_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ContratoDto;
import com.bodeguita.bodeguita_backend.dto.ContratoRequest;
import com.bodeguita.bodeguita_backend.entity.Contrato;
import com.bodeguita.bodeguita_backend.repository.ContratoRepository;

@Service
@Transactional(readOnly = true)
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final AuditoriaService auditoriaService;

    public ContratoService(ContratoRepository contratoRepository, AuditoriaService auditoriaService) {
        this.contratoRepository = contratoRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<ContratoDto> listar() {
        return contratoRepository.findAllActivosOrdenados().stream()
                .map(this::aDto)
                .toList();
    }

    public ContratoDto obtener(Long idContrato) {
        return aDto(buscarActivo(idContrato));
    }

    @Transactional
    public ContratoDto crear(ContratoRequest request) {
        Contrato contrato = new Contrato();
        contrato.setNContrato(request.nContrato().trim());
        contrato.setEstado(Boolean.TRUE);
        Contrato guardado = contratoRepository.save(contrato);
        auditoriaService.registrar("contrato", "INSERT", guardado.getIdContrato(), null, resumen(guardado));
        return aDto(guardado);
    }

    @Transactional
    public ContratoDto actualizar(Long idContrato, ContratoRequest request) {
        Contrato contrato = buscarActivo(idContrato);
        String anterior = resumen(contrato);
        contrato.setNContrato(request.nContrato().trim());
        auditoriaService.registrar("contrato", "UPDATE", idContrato, anterior, resumen(contrato));
        return aDto(contrato);
    }

    @Transactional
    public void desactivar(Long idContrato) {
        Contrato contrato = buscarActivo(idContrato);
        String anterior = resumen(contrato);
        contrato.setEstado(Boolean.FALSE);
        auditoriaService.registrar("contrato", "DESACTIVAR", idContrato, anterior, resumen(contrato));
    }

    private Contrato buscarActivo(Long idContrato) {
        return contratoRepository.findByIdContratoAndEstadoTrue(idContrato)
                .orElseThrow(() -> noEncontrado(idContrato));
    }

    private ContratoDto aDto(Contrato contrato) {
        return new ContratoDto(contrato.getIdContrato(), contrato.getNContrato());
    }

    private String resumen(Contrato contrato) {
        return "nContrato=" + contrato.getNContrato()
                + "; estado=" + (Boolean.TRUE.equals(contrato.getEstado()) ? "1" : "0");
    }

    private ResponseStatusException noEncontrado(Long idContrato) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato no encontrado: " + idContrato + ".");
    }
}
