package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;

public record AuditoriaDto(
        Long idAuditoria,
        Long idUsuario,
        String usuario,
        String nombreUsuario,
        String nTabla,
        String accion,
        Long idRegistro,
        String valorAnterior,
        String valorNuevo,
        LocalDateTime fEvento,
        String ip,
        String terminal,
        Boolean estado) {
}
