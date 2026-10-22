package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record DetalleVentaDto(
        Long id,
        Long idProducto,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
