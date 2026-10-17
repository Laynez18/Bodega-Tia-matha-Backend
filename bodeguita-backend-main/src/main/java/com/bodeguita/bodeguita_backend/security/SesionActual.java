package com.bodeguita.bodeguita_backend.security;

import java.util.List;
import java.util.Set;

public record SesionActual(
        Long idUsuario,
        String logeo,
        String nombreCompleto,
        Long idTipoUsuario,
        List<ModuloSesion> modulos,
        Set<String> permisos
) {

    public boolean puede(String clave) {
        return permisos != null && permisos.contains(clave);
    }

    public boolean puedeAlguno(String... claves) {
        for (String clave : claves) {
            if (puede(clave)) {
                return true;
            }
        }
        return false;
    }

    public boolean puedeModulo(String nombreModulo) {
        return modulos != null && modulos.stream()
                .anyMatch(m -> m.nombre().equalsIgnoreCase(nombreModulo));
    }
}
