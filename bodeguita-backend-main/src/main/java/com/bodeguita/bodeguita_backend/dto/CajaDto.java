package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record CajaDto(
        Long id,
        String nombre,
        String descripcion,
        String ubicacion,
        String serieTerminal,
        String moneda,
        BigDecimal montoBase,
        Boolean aperturada
) {
}
