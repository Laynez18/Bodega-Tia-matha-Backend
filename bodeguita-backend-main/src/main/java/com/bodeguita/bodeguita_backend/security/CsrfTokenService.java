package com.bodeguita.bodeguita_backend.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CsrfTokenService {

    public static final String HEADER = "X-CSRF-TOKEN";
    private static final String ATRIBUTO = "csrfToken";

    private static final Set<String> METODOS_SEGUROS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    public String obtener(HttpSession session) {
        String token = (String) session.getAttribute(ATRIBUTO);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(ATRIBUTO, token);
        }
        return token;
    }

    public boolean requiereValidacion(HttpServletRequest request) {
        return !METODOS_SEGUROS.contains(request.getMethod().toUpperCase());
    }

    public boolean valido(HttpServletRequest request, HttpSession session) {
        if (session == null) {
            return false;
        }
        String esperado = (String) session.getAttribute(ATRIBUTO);
        String recibido = request.getHeader(HEADER);
        if (esperado == null || recibido == null) {
            return false;
        }
        return MessageDigest.isEqual(
                esperado.getBytes(StandardCharsets.UTF_8),
                recibido.getBytes(StandardCharsets.UTF_8));
    }
}
