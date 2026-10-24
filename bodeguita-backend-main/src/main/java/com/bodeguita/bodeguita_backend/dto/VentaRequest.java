package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record VentaRequest(
        @NotNull Long idCliente,
        @Size(max = 30) String numeroDocumento,
        LocalDateTime fVenta,
        Long idMetodoPago,
        @Size(max = 20) String estado,
        @Size(max = 200) String observacion,
        List<DetalleVentaRequest> detalles
) {
}
