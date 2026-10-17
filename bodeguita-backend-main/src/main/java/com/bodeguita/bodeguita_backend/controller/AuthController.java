package com.bodeguita.bodeguita_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bodeguita.bodeguita_backend.dto.LoginRequest;
import com.bodeguita.bodeguita_backend.dto.LoginResponse;
import com.bodeguita.bodeguita_backend.security.Publico;
import com.bodeguita.bodeguita_backend.security.SinCsrf;
import com.bodeguita.bodeguita_backend.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Publico
    @SinCsrf
    public LoginResponse login(@Valid @RequestBody LoginRequest body, HttpServletRequest request) {
        return authService.login(body.logeo(), body.clave(), request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public LoginResponse me(HttpServletRequest request) {
        return authService.actual(request);
    }
}
