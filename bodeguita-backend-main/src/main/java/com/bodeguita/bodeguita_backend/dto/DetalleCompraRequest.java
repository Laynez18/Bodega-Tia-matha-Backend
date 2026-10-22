package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DetalleCompraRequest(
        @NotNull Long idProducto,
        @NotNull Integer cantidad,
        @NotNull BigDecimal precioUnitario
) {
}
