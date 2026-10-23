package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @NotBlank @Size(max = 50) String nProducto,
        @Size(max = 30) String codigoBarras,
        @Size(max = 100) String detalle,
        @NotNull Long idCategoriaProducto,
        Long idMarca,
        @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal pCompra,
        @NotNull @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal pVenta,
        @PositiveOrZero @Digits(integer = 8, fraction = 2) BigDecimal pMayoreo,
        @PositiveOrZero @Digits(integer = 9, fraction = 3) BigDecimal stockActual,
        @PositiveOrZero @Digits(integer = 9, fraction = 3) BigDecimal stockMinimo,
        @PositiveOrZero @Digits(integer = 9, fraction = 3) BigDecimal stockMaximo,
        @Pattern(regexp = "[01]") String afectoIgv,
        @Pattern(regexp = "[01]") String esPerecible,
        @Size(max = 200) String imagen,
        @NotNull Long idUnidadMedidaBase,
        @DecimalMin("0.001") @Digits(integer = 9, fraction = 3) BigDecimal factorConversionBase,
        @Pattern(regexp = "[01]") String esUnidadBase) {
}
