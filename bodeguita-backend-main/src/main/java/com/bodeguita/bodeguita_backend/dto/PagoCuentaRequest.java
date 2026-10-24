package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PagoCuentaRequest(
        @NotNull Long idCuenta,
        @NotNull Long idMetodoPago,
        @NotNull Long idUsuario,
        Long idAperturaCaja,
        @NotNull BigDecimal monto,
        String documento,
        String observacion
) {
}
