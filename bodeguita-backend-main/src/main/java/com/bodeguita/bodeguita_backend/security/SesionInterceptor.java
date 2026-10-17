package com.bodeguita.bodeguita_backend.security;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SesionInterceptor implements HandlerInterceptor {

    public static final String CLAVE_SESION = "com.bodeguita.bodeguita_backend.security.SesionActual";

    private final CsrfTokenService csrf;

    public SesionInterceptor(CsrfTokenService csrf) {
        this.csrf = csrf;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!(handler instanceof HandlerMethod metodo)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        SesionActual actual = leerSesion(session);
        ContextoUsuario.set(actual);

        if (esPublico(metodo)) {
            exigirCsrfSiCorresponde(metodo, request, session);
            return true;
        }

        if (actual == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado.");
        }

        exigirCsrfSiCorresponde(metodo, request, session);

        String permiso = permisoRequerido(metodo);
        if (permiso != null && !actual.puede(permiso)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tiene permiso para esta operacion (" + permiso + ").");
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        ContextoUsuario.clear();
    }

    private void exigirCsrfSiCorresponde(HandlerMethod metodo, HttpServletRequest request, HttpSession session) {
        if (eximeCsrf(metodo)) {
            return;
        }
        if (csrf.requiereValidacion(request) && !csrf.valido(request, session)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Token CSRF invalido o ausente.");
        }
    }

    private boolean eximeCsrf(HandlerMethod metodo) {
        return metodo.getMethodAnnotation(SinCsrf.class) != null
                || AnnotationUtils.findAnnotation(metodo.getBeanType(), SinCsrf.class) != null;
    }

    private SesionActual leerSesion(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object valor = session.getAttribute(CLAVE_SESION);
        return valor instanceof SesionActual s ? s : null;
    }

    private boolean esPublico(HandlerMethod metodo) {
        return metodo.getMethodAnnotation(Publico.class) != null
                || AnnotationUtils.findAnnotation(metodo.getBeanType(), Publico.class) != null;
    }

    private String permisoRequerido(HandlerMethod metodo) {
        RequierePermiso enMetodo = metodo.getMethodAnnotation(RequierePermiso.class);
        if (enMetodo != null) {
            return enMetodo.value();
        }
        RequierePermiso enClase = AnnotationUtils.findAnnotation(metodo.getBeanType(), RequierePermiso.class);
        return enClase != null ? enClase.value() : null;
    }
}
