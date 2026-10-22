package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreditoDto(
        Long id,
        Long idCliente,
        String nombreCliente,
        String numeroDocumento,
        BigDecimal monto,
        BigDecimal saldo,
        LocalDateTime fEmision,
        LocalDateTime fVencimiento,
        String estado,
        String observacion,
        List<PagoCreditoDto> pagos
) {
}
