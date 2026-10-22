package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoCajaDto(
        Long id,
        Long idAperturaCaja,
        Long idTipoMovimiento,
        String nombreTipoMovimiento,
        Long idConcepto,
        String nombreConcepto,
        Long idMetodoPago,
        String nombreMetodoPago,
        Long idUsuario,
        String nombreUsuario,
        Long idCompra,
        Long idVenta,
        String numeroOperacion,
        String documento,
        String descripcion,
        BigDecimal monto,
        Boolean afectaEfectivo,
        LocalDateTime fMovimiento
) {
}
