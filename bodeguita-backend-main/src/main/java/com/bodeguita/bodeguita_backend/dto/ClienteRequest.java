package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteRequest(
        @NotNull Long idPersona,
        Long idEmpresa,
        @Size(max = 20) String codigoCliente,
        @Size(max = 20) String tipoCliente,
        BigDecimal limiteCredito,
        BigDecimal saldoDeuda,
        Integer puntos,
        LocalDateTime fRegistro
) {
}
