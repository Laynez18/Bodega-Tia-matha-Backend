package com.bodeguita.bodeguita_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CambioPermisoRequest;
import com.bodeguita.bodeguita_backend.dto.MatrizPermisosDto;
import com.bodeguita.bodeguita_backend.dto.MatrizPermisosRequest;
import com.bodeguita.bodeguita_backend.dto.ModuloPermisosDto;
import com.bodeguita.bodeguita_backend.dto.PermisoFilaDto;
import com.bodeguita.bodeguita_backend.dto.RolResumenDto;
import com.bodeguita.bodeguita_backend.entity.Modulo;
import com.bodeguita.bodeguita_backend.entity.Permiso;
import com.bodeguita.bodeguita_backend.entity.Rol;
import com.bodeguita.bodeguita_backend.entity.RolPermiso;
import com.bodeguita.bodeguita_backend.repository.PermisoRepository;
import com.bodeguita.bodeguita_backend.repository.RolPermisoRepository;
import com.bodeguita.bodeguita_backend.repository.RolRepository;

@Service
@Transactional(readOnly = true)
public class PermisoService {

    private static final Set<String> PERMISOS_ADMIN = Set.of("SEG_USUARIO", "SEG_ROL", "SEG_AUDITORIA");

    private final RolRepository roles;
    private final PermisoRepository permisos;
    private final RolPermisoRepository rolPermisos;
    private final AuditoriaService auditoriaService;

    public PermisoService(
            RolRepository roles,
            PermisoRepository permisos,
            RolPermisoRepository rolPermisos,
            AuditoriaService auditoriaService) {
        this.roles = roles;
        this.permisos = permisos;
        this.rolPermisos = rolPermisos;
        this.auditoriaService = auditoriaService;
    }

    public MatrizPermisosDto matriz() {
        List<Rol> rs = roles.findAllActivosOrdenados();
        List<Permiso> ps = permisos.findAllActivosConModulo();
        List<Long> idsR = rs.stream().map(Rol::getIdRol).toList();
        List<Long> idsP = ps.stream().map(Permiso::getIdPermiso).toList();

        Map<String, Boolean> valores = new HashMap<>();
        if (!idsR.isEmpty() && !idsP.isEmpty()) {
            for (RolPermiso rp : rolPermisos.findAllParaMatriz(idsR, idsP)) {
                valores.put(
                        rp.getRol().getIdRol() + ":" + rp.getPermiso().getIdPermiso(),
                        Boolean.TRUE.equals(rp.getEstado()) && "1".equals(rp.getConcedido()));
            }
        }

        Map<Long, List<PermisoFilaDto>> porModulo = new LinkedHashMap<>();
        Map<Long, Modulo> modulos = new LinkedHashMap<>();
        for (Permiso p : ps) {
            Map<String, Boolean> porRol = new LinkedHashMap<>();
            for (Rol r : rs) {
                porRol.put(
                        String.valueOf(r.getIdRol()),
                        valores.getOrDefault(r.getIdRol() + ":" + p.getIdPermiso(), false));
            }
            modulos.putIfAbsent(p.getModulo().getIdModulo(), p.getModulo());
            porModulo.computeIfAbsent(p.getModulo().getIdModulo(), x -> new ArrayList<>())
                    .add(new PermisoFilaDto(p.getIdPermiso(), p.getClave(), p.getNPermiso(), porRol));
        }

        return new MatrizPermisosDto(
                rs.stream().map(r -> new RolResumenDto(r.getIdRol(), r.getNRol())).toList(),
                modulos.entrySet().stream()
                        .map(e -> new ModuloPermisosDto(e.getKey(), e.getValue().getNModulo(), porModulo.get(e.getKey())))
                        .toList());
    }

    @Transactional
    public void guardar(MatrizPermisosRequest request) {
        for (CambioPermisoRequest cambio : request.cambios()) {
            Rol rol = roles.findActivoPorId(cambio.idRol())
                    .orElseThrow(() -> bad("Rol inactivo o inexistente."));
            Permiso permiso = permisos.findById(cambio.idPermiso())
                    .filter(x -> Boolean.TRUE.equals(x.getEstado()))
                    .orElseThrow(() -> bad("Permiso inexistente."));
            if ("ADMINISTRADOR".equals(rol.getNRol())
                    && PERMISOS_ADMIN.contains(permiso.getClave())
                    && !cambio.concedido()) {
                throw bad("No se puede revocar " + permiso.getClave() + " al ADMINISTRADOR.");
            }

            RolPermiso rolPermiso = rolPermisos
                    .findByRol_IdRolAndPermiso_IdPermiso(cambio.idRol(), cambio.idPermiso())
                    .orElseGet(() -> {
                        RolPermiso nuevo = new RolPermiso();
                        nuevo.setRol(rol);
                        nuevo.setPermiso(permiso);
                        return nuevo;
                    });

            boolean nuevo = rolPermiso.getIdRolPermiso() == null;
            String anterior = nuevo ? null : resumen(rolPermiso);
            boolean concedido = cambio.concedido();
            if (!nuevo
                    && Boolean.TRUE.equals(rolPermiso.getEstado())
                    && ("1".equals(rolPermiso.getConcedido())) == concedido) {
                continue;
            }

            rolPermiso.setConcedido(concedido ? "1" : "0");
            rolPermiso.setEstado(true);
            RolPermiso guardado = rolPermisos.save(rolPermiso);
            auditoriaService.registrar(
                    "rol_permiso",
                    concedido ? "PERMISO_CONCEDER" : "PERMISO_REVOCAR",
                    guardado.getIdRolPermiso(),
                    anterior,
                    resumen(guardado));
        }
    }

    private String resumen(RolPermiso rolPermiso) {
        return "rol=" + rolPermiso.getRol().getIdRol() + ":" + rolPermiso.getRol().getNRol()
                + "; permiso=" + rolPermiso.getPermiso().getIdPermiso() + ":" + rolPermiso.getPermiso().getClave()
                + "; concedido=" + rolPermiso.getConcedido()
                + "; estado=" + (Boolean.TRUE.equals(rolPermiso.getEstado()) ? "1" : "0");
    }

    private ResponseStatusException bad(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}
