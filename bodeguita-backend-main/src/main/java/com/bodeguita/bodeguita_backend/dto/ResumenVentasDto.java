package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResumenVentasDto(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Long totalVentas,
        BigDecimal montoTotal
) {
}
