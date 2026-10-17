package com.bodeguita.bodeguita_backend.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException ex) {
        HttpStatusCode code = ex.getStatusCode();
        if (code.is5xxServerError()) {
            log.error("Error en la API", ex);
        }
        return ResponseEntity.status(code)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(cuerpo(code.value(), mensajeDe(ex)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> argumentos(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(cuerpo(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(cuerpo(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Ocurrio un error inesperado. Intente nuevamente."));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(cuerpo(HttpStatus.BAD_REQUEST.value(),
                        detalle.isEmpty() ? "Los datos enviados no son validos." : detalle));
    }

    private String mensajeDe(ResponseStatusException ex) {
        return ex.getReason() == null ? "La operacion no pudo completarse." : ex.getReason();
    }

    private Map<String, Object> cuerpo(int codigo, String mensaje) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("codigo", codigo);
        mapa.put("mensaje", mensaje);
        return mapa;
    }
}
