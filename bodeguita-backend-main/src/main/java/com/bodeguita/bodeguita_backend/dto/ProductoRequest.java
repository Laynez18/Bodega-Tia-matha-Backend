package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductoRequest(
        @NotNull Long idCategoria,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 300) String descripcion,
        @NotNull BigDecimal precioVenta,
        @NotNull Integer stock,
        Integer stockMinimo,
        @Size(max = 20) String unidadMedida
) {
}
