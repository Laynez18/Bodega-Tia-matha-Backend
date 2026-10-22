package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AperturaCajaRequest(
        @NotNull Long idCaja,
        @NotBlank @Size(max = 20) String numeroTurno,
        LocalDateTime fApertura,
        @NotNull BigDecimal montoInicial,
        @Size(max = 200) String observacion
) {
}
