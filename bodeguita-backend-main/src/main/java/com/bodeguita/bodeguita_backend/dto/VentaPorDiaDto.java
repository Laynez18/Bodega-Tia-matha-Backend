package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record VentaPorDiaDto(
        String fecha,
        Long cantidad,
        BigDecimal monto
) {
}
