package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ClienteRequest(
        @NotNull Long idPersona,
        @Size(max = 20) String tipoCliente,
        BigDecimal limiteCredito,
        @Size(max = 200) String observacion
) {
}
