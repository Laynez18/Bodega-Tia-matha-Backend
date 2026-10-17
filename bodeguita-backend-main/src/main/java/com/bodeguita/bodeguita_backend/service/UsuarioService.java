package com.bodeguita.bodeguita_backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            TipoUsuarioRepository tipoUsuarioRepository,
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioDto> listar() {
        return usuarioRepository.findAllActivosConRelaciones().stream().map(this::aDto).toList();
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
        String logeo = request.logeo().trim();
        if (usuarioRepository.existsByLogeo(logeo)) {
            throw conflicto("El logeo ya esta registrado.");
        }
        if (request.clave() == null || request.clave().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave es obligatoria al crear un usuario.");
        }

        Usuario usuario = new Usuario();
        usuario.setTipoUsuario(buscarTipoUsuario(request.idTipoUsuario()));
        usuario.setEmpleado(buscarEmpleado(request.idEmpleado()));
        usuario.setLogeo(logeo);
        usuario.setClave(passwordEncoder.encode(request.clave()));
        usuario.setEstado(Boolean.TRUE);
        usuarioRepository.saveAndFlush(usuario);
        actualizarRoles(usuario, request.idsRol());
        return obtener(usuario.getId());
    }

    @Transactional
    public UsuarioDto actualizar(Long idUsuario, UsuarioRequest request) {
        Usuario usuario = buscarActivo(idUsuario);
        String logeo = request.logeo().trim();
        if (usuarioRepository.existsByLogeoAndIdNot(logeo, idUsuario)) {
            throw conflicto("El logeo ya esta registrado.");
        }

        usuario.setTipoUsuario(buscarTipoUsuario(request.idTipoUsuario()));
        usuario.setEmpleado(buscarEmpleado(request.idEmpleado()));
        usuario.setLogeo(logeo);
        if (request.clave() != null && !request.clave().isBlank()) {
            usuario.setClave(passwordEncoder.encode(request.clave()));
        }
        actualizarRoles(usuario, request.idsRol());
        return obtener(idUsuario);
    }

    @Transactional
    public void desactivar(Long idUsuario) {
        buscarActivo(idUsuario).setEstado(Boolean.FALSE);
    }

    private Usuario buscarActivo(Long idUsuario) {
        return usuarioRepository.findActivoConRelaciones(idUsuario)
                .orElseThrow(() -> noEncontrado("Usuario", idUsuario));
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
                usuarioRol.setVigente("0");
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
                existente.setVigente("1");
                existente.setEstado(Boolean.TRUE);
                continue;
            }
            UsuarioRol nuevo = new UsuarioRol();
            nuevo.setUsuario(usuario);
            nuevo.setRol(rolesActivos.get(idRol));
            nuevo.setFAsignacion(LocalDateTime.now());
            nuevo.setVigente("1");
            nuevo.setEstado(Boolean.TRUE);
            usuarioRolRepository.save(nuevo);
            usuario.getRoles().add(nuevo);
        }
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
