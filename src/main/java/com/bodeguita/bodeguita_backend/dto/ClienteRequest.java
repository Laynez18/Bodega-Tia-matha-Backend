package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ClienteRequest(
        @NotNull @Valid PersonaRequest persona,
        Long idEmpresa,
        @Size(max = 20) String codigoCliente,
        @NotBlank @Pattern(regexp = "[NJ]") String tipoCliente,
        @PositiveOrZero @Digits(integer = 10, fraction = 2) BigDecimal limiteCredito,
        @PositiveOrZero @Digits(integer = 10, fraction = 2) BigDecimal saldoDeuda,
        Integer puntos) {
}
