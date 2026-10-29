package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteDto(
        Long id,
        Long idProducto,
        String nombreProducto,
        String nLote,
        LocalDate fVencimiento,
        BigDecimal stock,
        BigDecimal precioCosto
) {
}
