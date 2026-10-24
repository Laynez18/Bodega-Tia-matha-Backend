package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;

public record BoletaDto(
        Long id,
        Long idVenta,
        LocalDateTime fEmision,
        String numero,
        String serie,
        String estado
) {
}
