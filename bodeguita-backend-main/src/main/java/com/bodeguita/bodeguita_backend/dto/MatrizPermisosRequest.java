package com.bodeguita.bodeguita_backend.dto;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
public record MatrizPermisosRequest(@NotNull List<@Valid CambioPermisoRequest> cambios) {}
