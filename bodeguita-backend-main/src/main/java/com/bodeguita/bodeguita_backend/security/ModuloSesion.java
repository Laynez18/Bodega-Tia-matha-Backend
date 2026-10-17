package com.bodeguita.bodeguita_backend.security;

import java.util.List;
import java.util.Set;

public record ModuloSesion(
        String nombre,
        String icono,
        Integer orden,
        Set<String> permisos
) {

    public List<String> listaPermisos() {
        return permisos == null ? List.of() : List.copyOf(permisos);
    }
}
