package com.bodeguita.bodeguita_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodeguita.bodeguita_backend.dto.AuditoriaDto;
import com.bodeguita.bodeguita_backend.entity.Auditoria;
import com.bodeguita.bodeguita_backend.repository.AuditoriaRepository;

@Service
@Transactional(readOnly = true)
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public List<AuditoriaDto> listar() {
        return auditoriaRepository.findByEstadoOrderByFEventoDesc("1")
                .stream()
                .map(this::aDto)
                .collect(Collectors.toList());
    }

    public List<AuditoriaDto> listarPorUsuario(Long idUsuario) {
        return auditoriaRepository.findByEstadoAndUsuario_IdUsuarioOrderByFEventoDesc("1", idUsuario)
                .stream()
                .map(this::aDto)
                .collect(Collectors.toList());
    }

    private AuditoriaDto aDto(Auditoria e) {
        String usuario = e.getUsuario() != null ? e.getUsuario().getLogeo() : null;
        String detalle = null;
        if (e.getValorAnterior() != null || e.getValorNuevo() != null) {
            detalle = (e.getValorAnterior() != null ? e.getValorAnterior() : "-")
                    + " -> " + (e.getValorNuevo() != null ? e.getValorNuevo() : "-");
        }
        return new AuditoriaDto(
                e.getIdAuditoria(),
                e.getFEvento(),
                usuario,
                e.getNTabla(),
                e.getAccion(),
                detalle,
                e.getIp(),
                e.getTerminal()
        );
    }
}
