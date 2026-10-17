package com.bodeguita.bodeguita_backend.dto;
import jakarta.validation.constraints.*;
public record RolRequest(@NotBlank @Size(max=50) String nRol, @Size(max=100) String descripcion, @NotNull @Min(1) @Max(3) Integer nivel) {}
