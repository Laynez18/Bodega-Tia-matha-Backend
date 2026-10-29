package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteDto(
        Long id,
        Long idProducto,
        String nombreProducto,
        Long idAlmacen,
        String nLote,
        LocalDate fProduccion,
        LocalDate fVencimiento,
        BigDecimal cantidadInicial,
        BigDecimal cantidadActual,
        BigDecimal costoUnitario
) {
}
