package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductoRequest(
        @NotNull Long idCategoria,
        @NotBlank @Size(max = 50) String codigoBarras,
        @NotBlank @Size(max = 120) String nombre,
        @Size(max = 300) String descripcion,
        @NotNull BigDecimal precioCompra,
        @NotNull BigDecimal precioVenta,
        BigDecimal precioMayoreo,
        @NotNull Integer stock,
        Integer stockMinimo,
        Integer stockMaximo,
        @Size(max = 1) String afectoIgv,
        @Size(max = 1) String esPerecible,
        @Size(max = 200) String imagen
) {
}
