package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record InventarioDto(
        Long id,
        Long idProducto,
        Long idAlmacen,
        BigDecimal stock,
        BigDecimal stockReservado,
        String ubicacionFisica,
        String estado
) {
}
