package com.bodeguita.bodeguita_backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.RolResumenDto;
import com.bodeguita.bodeguita_backend.dto.TipoUsuarioDto;
import com.bodeguita.bodeguita_backend.dto.UsuarioDto;
import com.bodeguita.bodeguita_backend.dto.UsuarioRequest;
import com.bodeguita.bodeguita_backend.entity.Empleado;
import com.bodeguita.bodeguita_backend.entity.Rol;
import com.bodeguita.bodeguita_backend.entity.TipoUsuario;
import com.bodeguita.bodeguita_backend.entity.Usuario;
import com.bodeguita.bodeguita_backend.entity.UsuarioRol;
import com.bodeguita.bodeguita_backend.repository.EmpleadoRepository;
import com.bodeguita.bodeguita_backend.repository.RolRepository;
import com.bodeguita.bodeguita_backend.repository.TipoUsuarioRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRolRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            TipoUsuarioRepository tipoUsuarioRepository,
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder,
            AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    public List<UsuarioDto> listar() {
        return usuarioRepository.findAllConRelaciones().stream().map(this::aDto).toList();
    }

    public UsuarioDto obtener(Long idUsuario) {
        return aDto(buscarActivo(idUsuario));
    }

    public List<TipoUsuarioDto> listarTiposUsuario() {
        return tipoUsuarioRepository.findAllActivosOrdenados().stream()
                .map(tipo -> new TipoUsuarioDto(tipo.getIdTipoUsuario(), tipo.getNTipoUsuario()))
                .toList();
    }

    public List<RolResumenDto> listarRoles() {
        return rolRepository.findAllActivosOrdenados().stream()
                .map(rol -> new RolResumenDto(rol.getIdRol(), rol.getNRol()))
                .toList();
    }

    @Transactional
    public UsuarioDto crear(UsuarioRequest request) {
        String logeo = normalizarLogeo(request.logeo());
        if (usuarioRepository.existsByLogeo(logeo)) {
            throw conflicto("El logeo ya esta registrado.");
        }
        if (request.clave() == null || request.clave().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave es obligatoria al crear un usuario.");
        }

        Empleado empleado = buscarEmpleado(request.idEmpleado());
        validarEmpleadoSinOtroUsuarioActivo(
                empleado == null ? null : empleado.getIdEmpleado(),
                null,
                "El empleado ya tiene un usuario activo.");

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(buscarTipoUsuario(request.idTipoUsuario()));
        usuario.setEmpleado(empleado);
        usuario.setLogeo(logeo);
        usuario.setClave(passwordEncoder.encode(request.clave()));
        usuario.setEstado(Boolean.TRUE);
        usuarioRepository.saveAndFlush(usuario);
        actualizarRoles(usuario, request.idsRol());
        UsuarioDto creado = obtener(usuario.getId());
        auditoriaService.registrar("usuario", "INSERT", creado.idUsuario(), null, resumen(creado));
        return creado;
    }

    @Transactional
    public UsuarioDto actualizar(Long idUsuario, UsuarioRequest request) {
        Usuario usuario = buscarActivo(idUsuario);
        UsuarioDto anterior = aDto(usuario);
        String logeo = normalizarLogeo(request.logeo());
        if (usuarioRepository.existsByLogeoAndIdNot(logeo, idUsuario)) {
            throw conflicto("El logeo ya esta registrado.");
        }

        Empleado empleado = buscarEmpleado(request.idEmpleado());
        validarEmpleadoSinOtroUsuarioActivo(
                empleado == null ? null : empleado.getIdEmpleado(),
                idUsuario,
                "El empleado ya tiene un usuario activo.");

        usuario.setTipoUsuario(buscarTipoUsuario(request.idTipoUsuario()));
        usuario.setEmpleado(empleado);
        usuario.setLogeo(logeo);
        if (request.clave() != null && !request.clave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(request.clave()));
        }
        actualizarRoles(usuario, request.idsRol());
        UsuarioDto actualizado = obtener(idUsuario);
        auditoriaService.registrar("usuario", "UPDATE", idUsuario, resumen(anterior), resumen(actualizado));
        return actualizado;
    }

    @Transactional
    public UsuarioDto cambiarEstado(Long idUsuario, EstadoRequest request) {
        Usuario usuario = usuarioRepository.findConRelaciones(idUsuario)
                .orElseThrow(() -> noEncontrado("Usuario", idUsuario));
        UsuarioDto anterior = aDto(usuario);
        boolean nuevoEstado = "1".equals(request.estado());
        if ("1".equals(request.estado()) && usuario.getEmpleado() != null) {
            validarEmpleadoSinOtroUsuarioActivo(
                    usuario.getEmpleado().getIdEmpleado(),
                    idUsuario,
                    "No se puede activar el usuario porque su empleado ya tiene otro usuario activo.");
        }
        if (Boolean.TRUE.equals(usuario.getEstado()) == nuevoEstado) {
            return anterior;
        }
        usuario.setEstado(nuevoEstado);
        UsuarioDto actualizado = aDto(usuario);
        auditoriaService.registrar(
                "usuario",
                nuevoEstado ? "ACTIVAR" : "DESACTIVAR",
                idUsuario,
                resumen(anterior),
                resumen(actualizado));
        return actualizado;
    }

    @Transactional
    public void desactivar(Long idUsuario) {
        Usuario usuario = buscarActivo(idUsuario);
        String anterior = resumen(aDto(usuario));
        usuario.setEstado(Boolean.FALSE);
        auditoriaService.registrar(
                "usuario", "DESACTIVAR", idUsuario, anterior, resumen(aDto(usuario)));
    }

    private Usuario buscarActivo(Long idUsuario) {
        return usuarioRepository.findActivoConRelaciones(idUsuario)
                .orElseThrow(() -> noEncontrado("Usuario", idUsuario));
    }

    private String normalizarLogeo(String logeo) {
        return logeo.trim().toLowerCase(Locale.ROOT);
    }

    private void validarEmpleadoSinOtroUsuarioActivo(
            Long idEmpleado,
            Long idUsuarioExcluir,
            String mensaje) {
        if (idEmpleado == null) {
            return;
        }
        boolean ocupado = idUsuarioExcluir == null
                ? usuarioRepository.existsByEmpleado_IdEmpleadoAndEstadoTrue(idEmpleado)
                : usuarioRepository.existsByEmpleado_IdEmpleadoAndEstadoTrueAndIdNot(idEmpleado, idUsuarioExcluir);
        if (ocupado) {
            throw conflicto(mensaje);
        }
    }

    private TipoUsuario buscarTipoUsuario(Long idTipoUsuario) {
        return tipoUsuarioRepository.findByIdTipoUsuarioAndEstadoTrue(idTipoUsuario)
                .orElseThrow(() -> noEncontrado("Tipo de usuario", idTipoUsuario));
    }

    private Empleado buscarEmpleado(Long idEmpleado) {
        if (idEmpleado == null) {
            return null;
        }
        return empleadoRepository.findActivoConRelaciones(idEmpleado)
                .orElseThrow(() -> noEncontrado("Empleado", idEmpleado));
    }

    private void actualizarRoles(Usuario usuario, List<Long> idsRol) {
        Set<Long> idsSolicitados = idsRol == null ? Set.of() : Set.copyOf(idsRol);
        Map<Long, UsuarioRol> asignaciones = new HashMap<>();
        for (UsuarioRol usuarioRol : usuarioRolRepository.findAllPorUsuario(usuario.getId())) {
            asignaciones.put(usuarioRol.getRol().getIdRol(), usuarioRol);
            if (!idsSolicitados.contains(usuarioRol.getRol().getIdRol()) && "1".equals(usuarioRol.getVigente())) {
                String anterior = resumen(usuarioRol);
                usuarioRol.setVigente("0");
                auditoriaService.registrar(
                        "usuario_rol", "UPDATE", usuarioRol.getIdUsuarioRol(), anterior, resumen(usuarioRol));
            }
        }

        Map<Long, Rol> rolesActivos = new HashMap<>();
        for (Rol rol : rolRepository.findAllById(idsSolicitados)) {
            if (Boolean.TRUE.equals(rol.getEstado())) {
                rolesActivos.put(rol.getIdRol(), rol);
            }
        }
        if (rolesActivos.size() != idsSolicitados.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o mas roles no estan disponibles.");
        }

        for (Long idRol : idsSolicitados) {
            UsuarioRol existente = asignaciones.get(idRol);
            if (existente != null) {
                boolean estabaInactivo = !"1".equals(existente.getVigente())
                        || !Boolean.TRUE.equals(existente.getEstado());
                if (!estabaInactivo) {
                    continue;
                }
                String anterior = resumen(existente);
                existente.setVigente("1");
                existente.setEstado(Boolean.TRUE);
                auditoriaService.registrar(
                        "usuario_rol", "UPDATE", existente.getIdUsuarioRol(), anterior, resumen(existente));
                continue;
            }
            UsuarioRol nuevo = new UsuarioRol();
            nuevo.setUsuario(usuario);
            nuevo.setRol(rolesActivos.get(idRol));
            nuevo.setFAsignacion(LocalDateTime.now());
            nuevo.setVigente("1");
            nuevo.setEstado(Boolean.TRUE);
            UsuarioRol guardado = usuarioRolRepository.save(nuevo);
            usuario.getRoles().add(guardado);
            auditoriaService.registrar(
                    "usuario_rol", "INSERT", guardado.getIdUsuarioRol(), null, resumen(guardado));
        }
    }

    private String resumen(UsuarioDto usuario) {
        String roles = usuario.roles() == null
                ? ""
                : String.join(",", usuario.roles().stream()
                        .map(rol -> rol.idRol() + ":" + rol.nRol())
                        .toList());
        return "logeo=" + texto(usuario.logeo())
                + "; tipo=" + texto(usuario.nTipoUsuario())
                + "; empleado=" + texto(usuario.idEmpleado())
                + "; roles=" + roles
                + "; estado=" + estado(usuario.estado());
    }

    private String resumen(UsuarioRol usuarioRol) {
        return "usuario=" + usuarioRol.getUsuario().getId()
                + "; rol=" + usuarioRol.getRol().getIdRol() + ":" + usuarioRol.getRol().getNRol()
                + "; vigente=" + texto(usuarioRol.getVigente())
                + "; estado=" + estado(usuarioRol.getEstado());
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private String estado(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "1" : "0";
    }

    private UsuarioDto aDto(Usuario usuario) {
        List<RolResumenDto> roles = usuario.getRoles().stream()
                .filter(usuarioRol -> Boolean.TRUE.equals(usuarioRol.getEstado()) && "1".equals(usuarioRol.getVigente()))
                .map(usuarioRol -> new RolResumenDto(usuarioRol.getRol().getIdRol(), usuarioRol.getRol().getNRol()))
                .toList();
        Empleado empleado = usuario.getEmpleado();
        String nombreEmpleado = empleado == null ? null : nombreCompleto(empleado);
        return new UsuarioDto(
                usuario.getId(),
                usuario.getTipoUsuario().getIdTipoUsuario(),
                usuario.getTipoUsuario().getNTipoUsuario(),
                empleado == null ? null : empleado.getIdEmpleado(),
                nombreEmpleado,
                usuario.getLogeo(),
                usuario.getEstado(),
                roles,
                usuario.getFecCreador());
    }

    private String nombreCompleto(Empleado empleado) {
        return Arrays.asList(
                        empleado.getPersona().getNombre(),
                        empleado.getPersona().getApPaterno(),
                        empleado.getPersona().getApMaterno())
                .stream()
                .filter(valor -> valor != null && !valor.isBlank())
                .reduce((izquierda, derecha) -> izquierda + " " + derecha)
                .orElse("");
    }

    private ResponseStatusException noEncontrado(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " no encontrado: " + id + ".");
    }

    private ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }
}
