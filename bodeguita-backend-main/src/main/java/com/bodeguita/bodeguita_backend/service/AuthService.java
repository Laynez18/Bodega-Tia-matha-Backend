package com.bodeguita.bodeguita_backend.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.LoginResponse;
import com.bodeguita.bodeguita_backend.dto.ModuloDto;
import com.bodeguita.bodeguita_backend.security.CsrfTokenService;
import com.bodeguita.bodeguita_backend.security.ModuloSesion;
import com.bodeguita.bodeguita_backend.security.SesionActual;
import com.bodeguita.bodeguita_backend.security.SesionInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    public static final String CLAVE_SESION = SesionInterceptor.CLAVE_SESION;

    private static final String SQL_LOGIN = "SELECT * FROM usp_login(?)";
    private static final String SQL_PERMISOS = "SELECT * FROM usp_permisos_usuario(?)";

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;
    private final CsrfTokenService csrf;

    public AuthService(JdbcTemplate jdbc, PasswordEncoder passwordEncoder, CsrfTokenService csrf) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
        this.csrf = csrf;
    }

    public LoginResponse login(String logeo, String clave, HttpServletRequest request) {

        FilaLogin fila = jdbc.query(SQL_LOGIN, FilaLogin::mapRow, new Object[] { logeo })
                .stream()
                .findFirst()
                .orElseThrow(this::credencialesInvalidas);

        if (!passwordEncoder.matches(clave, fila.clave())) {
            throw credencialesInvalidas();
        }

        List<FilaPermiso> permisos = jdbc.query(SQL_PERMISOS, FilaPermiso::mapRow,
                new Object[] { fila.idUsuario() });

        SesionActual sesion = new SesionActual(
                fila.idUsuario(),
                fila.logeo(),
                fila.nombreCompleto(),
                fila.idTipoUsuario(),
                agruparModulos(permisos),
                new LinkedHashSet<>(permisos.stream().map(FilaPermiso::clave).toList()));

        HttpSession session = rotarSesion(request);
        session.setAttribute(CLAVE_SESION, sesion);

        return new LoginResponse(
                sesion.idUsuario(),
                sesion.logeo(),
                sesion.nombreCompleto(),
                sesion.idTipoUsuario(),
                sesion.modulos().stream().map(this::aDto).toList(),
                List.copyOf(sesion.permisos()),
                csrf.obtener(session),
                CsrfTokenService.HEADER);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public LoginResponse actual(HttpServletRequest request) {
        SesionActual sesion = leerSesion(request);
        HttpSession session = request.getSession(false);
        return new LoginResponse(
                sesion.idUsuario(),
                sesion.logeo(),
                sesion.nombreCompleto(),
                sesion.idTipoUsuario(),
                sesion.modulos().stream().map(this::aDto).toList(),
                List.copyOf(sesion.permisos()),
                csrf.obtener(session),
                CsrfTokenService.HEADER);
    }

    public SesionActual leerSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object valor = session == null ? null : session.getAttribute(CLAVE_SESION);
        if (valor instanceof SesionActual s) {
            return s;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No hay sesion activa.");
    }

    public ModuloDto aDto(ModuloSesion modulo) {
        return new ModuloDto(
                modulo.nombre(),
                modulo.icono(),
                modulo.orden(),
                modulo.listaPermisos());
    }

    private List<ModuloSesion> agruparModulos(List<FilaPermiso> filas) {
        Map<String, List<FilaPermiso>> agrupados = new LinkedHashMap<>();

        for (FilaPermiso fila : filas) {
            agrupados.computeIfAbsent(fila.nModulo(), k -> new ArrayList<>()).add(fila);
        }

        return agrupados.values().stream()
                .map(delGrupo -> new ModuloSesion(
                        delGrupo.get(0).nModulo(),
                        delGrupo.get(0).icono(),
                        delGrupo.get(0).orden(),
                        new LinkedHashSet<>(delGrupo.stream().map(FilaPermiso::clave).toList())))
                .toList();
    }

    private HttpSession rotarSesion(HttpServletRequest request) {
        HttpSession anterior = request.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        return request.getSession(true);
    }

    private ResponseStatusException credencialesInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos.");
    }
}
