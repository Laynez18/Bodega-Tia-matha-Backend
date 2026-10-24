package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record PresentacionProductoRequest(
        @NotNull Long idProducto,
        @NotNull Long idUnidadMedida,
        BigDecimal factorConversion,
        String esUnidadBase
) {
}

