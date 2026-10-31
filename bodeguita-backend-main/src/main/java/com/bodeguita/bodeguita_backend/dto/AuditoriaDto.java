package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;

public record AuditoriaDto(
        Long idAuditoria,
        LocalDateTime fEvento,
        String usuario,
        String modulo,
        String accion,
        String detalle,
        String ip,
        String terminal
) {
}
