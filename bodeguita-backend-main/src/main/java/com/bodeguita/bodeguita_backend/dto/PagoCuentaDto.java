package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoCuentaDto(
        Long id,
        Long idCuenta,
        Long idMetodoPago,
        Long idUsuario,
        Long idAperturaCaja,
        BigDecimal monto,
        LocalDateTime fPago,
        String documento,
        String observacion,
        String estado
) {
}
