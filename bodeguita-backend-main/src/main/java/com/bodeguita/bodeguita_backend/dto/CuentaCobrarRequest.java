package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaCobrarRequest(
        @NotNull Long idVenta,
        @NotNull Long idCliente,
        @NotNull BigDecimal montoTotal,
        LocalDate fVencimiento
) {
}
