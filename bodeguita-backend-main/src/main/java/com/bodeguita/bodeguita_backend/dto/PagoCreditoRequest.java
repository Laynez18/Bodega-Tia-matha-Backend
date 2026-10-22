package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoCreditoRequest(
        @NotNull Long idCredito,
        @NotNull BigDecimal monto,
        LocalDateTime fPago,
        Long idMetodoPago,
        @Size(max = 30) String numeroOperacion,
        @Size(max = 200) String observacion
) {
}
