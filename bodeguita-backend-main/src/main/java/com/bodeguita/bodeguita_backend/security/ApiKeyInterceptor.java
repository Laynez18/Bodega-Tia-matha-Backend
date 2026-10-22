package com.bodeguita.bodeguita_backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-API-Key";

    private final String apiKey;

    public ApiKeyInterceptor(@Value("${app.api.key:}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (apiKey == null || apiKey.isBlank()) {
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String recibido = request.getHeader(HEADER);
        if (recibido == null || !recibido.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "API Key invalida o ausente. Use el header " + HEADER + ".");
        }

        return true;
    }
}
