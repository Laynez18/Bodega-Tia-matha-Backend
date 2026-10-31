package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AperturaCajaCierreRequest(
        @NotNull BigDecimal montoDeclarado,
        @NotBlank @Size(max = 200) String observacion,
        @NotBlank @Size(max = 1) String situacion
) {
}
