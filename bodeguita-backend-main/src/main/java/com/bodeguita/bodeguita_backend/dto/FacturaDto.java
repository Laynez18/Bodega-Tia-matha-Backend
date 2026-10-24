package com.bodeguita.bodeguita_backend.dto;

import java.time.LocalDateTime;

public record FacturaDto(
        Long id,
        Long idVenta,
        LocalDateTime fEmision,
        String numero,
        String serie,
        String estado
) {
}
