package com.bodeguita.bodeguita_backend.security;

import java.util.Optional;

public final class ContextoUsuario {

    private static final ThreadLocal<SesionActual> CONTEXTO = new ThreadLocal<>();

    private ContextoUsuario() {
    }

    public static void set(SesionActual sesion) {
        CONTEXTO.set(sesion);
    }

    public static SesionActual get() {
        return CONTEXTO.get();
    }

    public static Optional<SesionActual> actual() {
        return Optional.ofNullable(CONTEXTO.get());
    }

    public static void clear() {
        CONTEXTO.remove();
    }

    public static Long idUsuario() {
        SesionActual s = CONTEXTO.get();
        return s == null ? null : s.idUsuario();
    }

    public static String logeo() {
        SesionActual s = CONTEXTO.get();
        return s == null ? null : s.logeo();
    }
}
