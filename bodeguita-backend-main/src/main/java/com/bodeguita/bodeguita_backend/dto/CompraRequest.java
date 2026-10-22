package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraRequest(
        @NotNull Long idProveedor,
        @Size(max = 30) String numeroDocumento,
        LocalDateTime fCompra,
        @Size(max = 20) String estado,
        @Size(max = 200) String observacion,
        List<DetalleCompraRequest> detalles
) {
}
