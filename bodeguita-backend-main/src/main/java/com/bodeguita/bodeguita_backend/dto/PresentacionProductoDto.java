package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record PresentacionProductoDto(
        Long id,
        Long idProducto,
        Long idUnidadMedida,
        BigDecimal factorConversion,
        String esUnidadBase,
        String estado
) {
}
