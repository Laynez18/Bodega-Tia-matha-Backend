package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CajaRequest(
        @NotBlank @Size(max = 50) String nombre,
        @Size(max = 100) String descripcion,
        @Size(max = 100) String ubicacion,
        @Size(max = 30) String serieTerminal,
        String moneda,
        BigDecimal montoBase
) {
}
