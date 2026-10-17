package com.bodeguita.bodeguita_backend.dto;
import jakarta.validation.constraints.NotNull;
public record CambioPermisoRequest(@NotNull Long idRol, @NotNull Long idPermiso, @NotNull Boolean concedido) {}
