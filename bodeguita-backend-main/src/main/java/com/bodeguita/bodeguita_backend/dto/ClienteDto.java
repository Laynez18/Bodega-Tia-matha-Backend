package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record ClienteDto(
        Long id,
        Long idPersona,
        String nombrePersona,
        String nDocumento,
        String tipoCliente,
        BigDecimal limiteCredito,
        String observacion
) {
}
