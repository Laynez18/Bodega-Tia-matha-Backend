package com.bodeguita.bodeguita_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.RolDto;
import com.bodeguita.bodeguita_backend.dto.RolRequest;
import com.bodeguita.bodeguita_backend.entity.Rol;
import com.bodeguita.bodeguita_backend.repository.RolRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRolRepository;

@Service
@Transactional(readOnly = true)
public class RolService {

    private final RolRepository roles;
    private final UsuarioRolRepository usuarioRoles;
    private final AuditoriaService auditoriaService;

    public RolService(
            RolRepository roles,
            UsuarioRolRepository usuarioRoles,
            AuditoriaService auditoriaService) {
        this.roles = roles;
        this.usuarioRoles = usuarioRoles;
        this.auditoriaService = auditoriaService;
    }

    public List<RolDto> listar() {
        return roles.findAllOrdenados().stream().map(this::dto).toList();
    }

    public RolDto obtener(Long id) {
        return dto(buscar(id));
    }

    @Transactional
    public RolDto crear(RolRequest request) {
        String nombre = normalizar(request.nRol());
        if (roles.existeNombre(nombre)) {
            throw conflicto("El nombre del rol ya existe.");
        }
        Rol rol = new Rol();
        asignar(rol, request, nombre);
        rol.setEstado(true);
        rol.setFCreacion(LocalDateTime.now(ZoneId.of("America/Lima")));
        RolDto creado = dto(roles.save(rol));
        auditoriaService.registrar("rol", "INSERT", creado.idRol(), null, resumen(creado));
        return creado;
    }

    @Transactional
    public RolDto actualizar(Long id, RolRequest request) {
        Rol rol = buscar(id);
        RolDto anterior = dto(rol);
        String nombre = normalizar(request.nRol());
        if (roles.existeNombreEnOtroRol(nombre, id)) {
            throw conflicto("El nombre del rol ya existe.");
        }
        asignar(rol, request, nombre);
        RolDto actualizado = dto(rol);
        auditoriaService.registrar("rol", "UPDATE", id, resumen(anterior), resumen(actualizado));
        return actualizado;
    }

    @Transactional
    public RolDto cambiarEstado(Long id, EstadoRequest request) {
        Rol rol = buscar(id);
        if ("ADMINISTRADOR".equals(rol.getNRol()) && "0".equals(request.estado())) {
            throw conflicto("El rol ADMINISTRADOR no se puede desactivar.");
        }
        boolean nuevoEstado = "1".equals(request.estado());
        if (Boolean.TRUE.equals(rol.getEstado()) == nuevoEstado) {
            return dto(rol);
        }
        if (!nuevoEstado) {
            long asignados = usuarioRoles.countByRol_IdRolAndVigenteAndEstadoTrue(id, "1");
            if (asignados > 0) {
                throw conflicto("No se puede desactivar el rol porque tiene "
                        + asignados + " usuarios vigentes asignados.");
            }
        }
        RolDto anterior = dto(rol);
        rol.setEstado(nuevoEstado);
        RolDto actualizado = dto(rol);
        auditoriaService.registrar(
                "rol",
                nuevoEstado ? "ACTIVAR" : "DESACTIVAR",
                id,
                resumen(anterior),
                resumen(actualizado));
        return actualizado;
    }

    private Rol buscar(Long id) {
        return roles.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rol no encontrado: " + id + "."));
    }

    private void asignar(Rol rol, RolRequest request, String nombre) {
        rol.setNRol(nombre);
        rol.setDescripcion(request.descripcion() == null || request.descripcion().isBlank()
                ? null
                : request.descripcion().trim());
        rol.setNivel(request.nivel());
    }

    private String normalizar(String nombre) {
        return nombre.trim().toUpperCase(Locale.ROOT);
    }

    private RolDto dto(Rol rol) {
        return new RolDto(
                rol.getIdRol(),
                rol.getNRol(),
                rol.getDescripcion(),
                rol.getNivel(),
                rol.getFCreacion(),
                rol.getEstado());
    }

    private String resumen(RolDto rol) {
        return "nRol=" + texto(rol.nRol())
                + "; descripcion=" + texto(rol.descripcion())
                + "; nivel=" + texto(rol.nivel())
                + "; estado=" + (Boolean.TRUE.equals(rol.estado()) ? "1" : "0");
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }
}
