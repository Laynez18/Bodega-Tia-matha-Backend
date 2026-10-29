package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record VentaRequest(
        @NotNull Long idCliente,
        LocalDateTime fVenta,
        Long idMetodoPago,
        @Size(max = 20) String estado,
        List<DetalleVentaRequest> detalles
) {
}
