package com.bodeguita.bodeguita_backend.dto;

public record ConceptoCajaDto(
        Long id,
        Long idTipoMovimiento,
        String nombreTipoMovimiento,
        String nombre,
        String descripcion,
        String afectaEfectivo
) {
}
