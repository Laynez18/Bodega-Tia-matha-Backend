package com.bodeguita.bodeguita_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaProductoRequest(
        @NotBlank(message = "El nombre de la categoria es obligatorio.")
        @Size(max = 50, message = "El nombre no debe superar los 50 caracteres.")
        String nCategoriaProducto,
        @Size(max = 100, message = "La descripcion no debe superar los 100 caracteres.")
        String descripcion) {
}