package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteRequest(
        @NotNull Long idProducto,
        @NotNull Long idAlmacen,
        @NotBlank @Size(max = 30) String nLote,
        LocalDate fProduccion,
        @NotNull LocalDate fVencimiento,
        @NotNull BigDecimal cantidadInicial,
        @NotNull BigDecimal cantidadActual,
        BigDecimal costoUnitario
) {
}
