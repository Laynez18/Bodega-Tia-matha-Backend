package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioRequest(
        @NotNull Long idProducto,
        @NotNull Long idAlmacen,
        @NotNull Long idTipoMovimientoInv,
        @NotNull Long idUsuario,
        Long idLote,
        Long idVenta,
        Long idCompra,
        @NotNull Integer cantidad,
        @NotNull Integer stockAnterior,
        @NotNull Integer stockNuevo,
        LocalDateTime fMovimiento,
        @Size(max = 50) String documento,
        @Size(max = 200) String observacion,
        BigDecimal costoUnitario
) {
}
