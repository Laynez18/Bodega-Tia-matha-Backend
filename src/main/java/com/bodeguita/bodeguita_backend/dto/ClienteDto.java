package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ClienteDto(
        Long idCliente,
        PersonaDto persona,
        Long idEmpresa,
        String codigoCliente,
        String tipoCliente,
        BigDecimal limiteCredito,
        BigDecimal saldoDeuda,
        Integer puntos,
        LocalDateTime fRegistro,
        Boolean estado) {
}
