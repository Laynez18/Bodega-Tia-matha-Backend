package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AperturaCajaDto(
        Long id,
        Long idCaja,
        String nombreCaja,
        Long idUsuario,
        String nombreUsuario,
        String numeroTurno,
        LocalDateTime fApertura,
        LocalDateTime fCierre,
        BigDecimal montoInicial,
        BigDecimal totalIngresos,
        BigDecimal totalEgresos,
        BigDecimal montoSistema,
        BigDecimal montoDeclarado,
        BigDecimal diferencia,
        String situacion,
        String observacion
) {
}
