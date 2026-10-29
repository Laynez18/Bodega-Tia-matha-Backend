package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioDto(
        Long id,
        Long idProducto,
        String nombreProducto,
        String nombreTipoMovimientoInv,
        Integer cantidad,
        Integer stockAnterior,
        Integer stockNuevo,
        LocalDateTime fMovimiento,
        String documento,
        String observacion,
        BigDecimal costoUnitario
) {
}
