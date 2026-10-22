package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoCajaRequest(
        @NotNull Long idAperturaCaja,
        @NotNull Long idTipoMovimiento,
        @NotNull Long idConcepto,
        @NotNull Long idMetodoPago,
        Long idCompra,
        Long idVenta,
        @Size(max = 30) String numeroOperacion,
        @Size(max = 50) String documento,
        @Size(max = 200) String descripcion,
        @NotNull BigDecimal monto,
        Boolean afectaEfectivo,
        LocalDateTime fMovimiento
) {
}
