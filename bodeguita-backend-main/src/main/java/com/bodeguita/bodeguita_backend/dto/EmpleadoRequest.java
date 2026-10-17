package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record EmpleadoRequest(
        @Valid @NotNull PersonaRequest persona,
        Long idContrato,
        Long idCargo,
        @PositiveOrZero @Digits(integer = 6, fraction = 2) BigDecimal salario,
        @Size(max = 18) String turno,
        @Size(max = 3) String fondoPension,
        @Size(max = 11) String nHps,
        @Size(max = 6) String essalud) {
}
