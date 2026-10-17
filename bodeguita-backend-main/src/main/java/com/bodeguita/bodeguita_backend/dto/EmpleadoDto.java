package com.bodeguita.bodeguita_backend.dto;

import java.math.BigDecimal;

public record EmpleadoDto(
        Long idEmpleado,
        PersonaDto persona,
        Long idContrato,
        String nContrato,
        Long idCargo,
        String nCargo,
        BigDecimal salario,
        String turno,
        String fondoPension,
        String nHps,
        String essalud) {
}
