package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CuentaCobrarDto(
        Long id,
        Long idVenta,
        Long idCliente,
        BigDecimal montoTotal,
        BigDecimal saldo,
        LocalDateTime fEmision,
        LocalDate fVencimiento,
        String situacion,
        String estado
) {
}
