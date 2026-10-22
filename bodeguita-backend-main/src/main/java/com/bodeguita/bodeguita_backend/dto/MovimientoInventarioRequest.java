package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioRequest(
        @NotNull Long idProducto,
        @NotBlank @Size(max = 20) String tipoMovimiento,
        @NotNull Integer cantidad,
        @NotNull Integer stockAnterior,
        @NotNull Integer stockNuevo,
        LocalDateTime fMovimiento,
        @Size(max = 50) String referencia,
        @Size(max = 200) String observacion,
        BigDecimal valorUnitario
) {
}
