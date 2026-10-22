package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaDto(
        Long id,
        Long idCliente,
        String nombreCliente,
        String numeroDocumento,
        LocalDateTime fVenta,
        BigDecimal subtotal,
        BigDecimal igv,
        BigDecimal total,
        Long idMetodoPago,
        String nombreMetodoPago,
        String estado,
        String observacion,
        List<DetalleVentaDto> detalles
) {
}
