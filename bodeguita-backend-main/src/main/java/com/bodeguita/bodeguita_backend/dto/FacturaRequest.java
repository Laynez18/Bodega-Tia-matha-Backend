package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FacturaRequest(
        @NotNull Long idVenta,
        @Size(max = 20) String numero,
        @Size(max = 10) String serie
) {
}
