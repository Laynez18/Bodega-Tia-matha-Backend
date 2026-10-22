package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraDto(
        Long id,
        Long idProveedor,
        String nombreProveedor,
        String numeroDocumento,
        LocalDateTime fCompra,
        BigDecimal subtotal,
        BigDecimal igv,
        BigDecimal total,
        String estado,
        String observacion,
        List<DetalleCompraDto> detalles
) {
}
