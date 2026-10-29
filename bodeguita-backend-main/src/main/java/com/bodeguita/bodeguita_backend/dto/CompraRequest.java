package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraRequest(
        @NotNull Long idProveedor,
        @Size(max = 50) String documento,
        LocalDateTime fCompra,
        @Size(max = 20) String estado,
        List<DetalleCompraRequest> detalles
) {
}
