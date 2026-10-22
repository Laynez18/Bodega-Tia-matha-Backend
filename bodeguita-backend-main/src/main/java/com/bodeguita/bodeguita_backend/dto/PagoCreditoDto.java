package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoCreditoDto(
        Long id,
        Long idCredito,
        BigDecimal monto,
        LocalDateTime fPago,
        Long idMetodoPago,
        String nombreMetodoPago,
        String numeroOperacion,
        String observacion
) {
}
