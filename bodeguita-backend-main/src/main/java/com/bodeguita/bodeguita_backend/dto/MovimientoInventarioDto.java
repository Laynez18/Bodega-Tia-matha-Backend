package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioDto(
        Long id,
        Long idProducto,
        String nombreProducto,
        String tipoMovimiento,
        Integer cantidad,
        Integer stockAnterior,
        Integer stockNuevo,
        LocalDateTime fMovimiento,
        String referencia,
        String observacion,
        BigDecimal valorUnitario
) {
}
