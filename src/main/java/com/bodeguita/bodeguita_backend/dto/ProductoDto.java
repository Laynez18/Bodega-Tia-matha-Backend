package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoDto(
        Long idProducto,
        String codigoBarras,
        String nProducto,
        String detalle,
        Long idCategoriaProducto,
        String nCategoriaProducto,
        Long idMarca,
        String nMarca,
        BigDecimal pCompra,
        BigDecimal pVenta,
        BigDecimal pMayoreo,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        BigDecimal stockMaximo,
        Boolean afectoIgv,
        Boolean esPerecible,
        String imagen,
        Long idUnidadMedidaBase,
        String nUnidadMedidaBase,
        String abreviaturaUnidadBase,
        Boolean estado,
        LocalDateTime fecCre) {
}
