package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InventarioRequest(
        @NotNull Long idProducto,
        @NotNull Long idAlmacen,
        BigDecimal stock,
        BigDecimal stockReservado,
        String ubicacionFisica
) {
}
