package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResumenComprasDto(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Long totalCompras,
        BigDecimal montoTotal
) {
}
