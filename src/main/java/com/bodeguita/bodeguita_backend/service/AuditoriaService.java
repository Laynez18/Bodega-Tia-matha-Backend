package com.bodeguita.bodeguita_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.AuditoriaDto;
import com.bodeguita.bodeguita_backend.dto.AuditoriaFiltrosDto;
import com.bodeguita.bodeguita_backend.dto.UsuarioFiltroDto;
import com.bodeguita.bodeguita_backend.entity.Auditoria;
import com.bodeguita.bodeguita_backend.entity.Empleado;
import com.bodeguita.bodeguita_backend.entity.Usuario;
import com.bodeguita.bodeguita_backend.repository.AuditoriaRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;
import com.bodeguita.bodeguita_backend.security.ContextoUsuario;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional(readOnly = true)
public class AuditoriaService {

    private static final ZoneId ZONA_HORARIA_LIMA = ZoneId.of("America/Lima");
    private static final int LONGITUD_MAXIMA_VALOR = 500;

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<AuditoriaDto> listar(
            Long idUsuario,
            String nTabla,
            String accion,
            LocalDate desde,
            LocalDate hasta,
            String texto) {

        validarFechas(desde, hasta);

        Specification<Auditoria> filtros = (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            predicados.add(cb.isTrue(root.get("estado")));

            if (idUsuario != null) {
                predicados.add(cb.equal(root.get("usuario").get("id"), idUsuario));
            }
            if (nTabla != null && !nTabla.isBlank()) {
                predicados.add(cb.equal(cb.lower(root.get("nTabla")), nTabla.trim().toLowerCase(Locale.ROOT)));
            }
            if (accion != null && !accion.isBlank()) {
                predicados.add(cb.equal(cb.upper(root.get("accion")), accion.trim().toUpperCase(Locale.ROOT)));
            }
            if (desde != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("fEvento"), desde.atStartOfDay()));
            }
            if (hasta != null) {
                predicados.add(cb.lessThan(root.get("fEvento"), hasta.plusDays(1).atStartOfDay()));
            }
            if (texto != null && !texto.isBlank()) {
                Join<Object, Object> usuario = root.join("usuario", JoinType.LEFT);
                String patron = "%" + texto.trim().toLowerCase(Locale.ROOT) + "%";
                predicados.add(cb.or(
                        cb.like(cb.lower(root.get("nTabla")), patron),
                        cb.like(cb.lower(root.get("accion")), patron),
                        cb.like(cb.lower(root.get("valorAnterior")), patron),
                        cb.like(cb.lower(root.get("valorNuevo")), patron),
                        cb.like(cb.lower(usuario.get("logeo")), patron)));
            }

            query.orderBy(cb.desc(root.get("fEvento")), cb.desc(root.get("idAuditoria")));
            return cb.and(predicados.toArray(new Predicate[0]));
        };

        return auditoriaRepository.findAll(filtros).stream().map(this::aDto).toList();
    }

    public AuditoriaFiltrosDto filtros() {
        List<UsuarioFiltroDto> usuarios = usuarioRepository.findAllParaFiltro().stream()
                .map(usuario -> new UsuarioFiltroDto(
                        usuario.getId(),
                        usuario.getLogeo(),
                        nombreCompleto(usuario.getEmpleado())))
                .toList();
        return new AuditoriaFiltrosDto(
                usuarios,
                auditoriaRepository.findTablas(),
                auditoriaRepository.findAcciones());
    }

    public AuditoriaDto obtener(Long idAuditoria) {
        return auditoriaRepository.findDetalle(idAuditoria)
                .map(this::aDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Auditoria no encontrada: " + idAuditoria + "."));
    }

    @Transactional
    public void registrar(
            String nTabla,
            String accion,
            Long idRegistro,
            String valorAnterior,
            String valorNuevo) {

        if (nTabla == null || nTabla.isBlank() || accion == null || accion.isBlank()) {
            return;
        }

        Auditoria auditoria = new Auditoria();
        Long idUsuario = ContextoUsuario.idUsuario();
        if (idUsuario != null) {
            auditoria.setUsuario(usuarioRepository.getReferenceById(idUsuario));
        }
        auditoria.setNTabla(limitar(nTabla.trim().toLowerCase(Locale.ROOT), 50));
        auditoria.setAccion(limitar(accion.trim().toUpperCase(Locale.ROOT), 20));
        auditoria.setIdRegistro(idRegistro);
        auditoria.setValorAnterior(limitar(valorAnterior, LONGITUD_MAXIMA_VALOR));
        auditoria.setValorNuevo(limitar(valorNuevo, LONGITUD_MAXIMA_VALOR));
        auditoria.setFEvento(LocalDateTime.now(ZONA_HORARIA_LIMA));
        DatosPeticion datos = datosPeticion();
        auditoria.setIp(limitar(datos.ip(), 20));
        auditoria.setTerminal(limitar(datos.terminal(), 30));
        auditoria.setEstado(Boolean.TRUE);
        auditoriaRepository.save(auditoria);
    }

    private AuditoriaDto aDto(Auditoria auditoria) {
        Usuario usuario = auditoria.getUsuario();
        return new AuditoriaDto(
                auditoria.getIdAuditoria(),
                usuario == null ? null : usuario.getId(),
                usuario == null ? null : usuario.getLogeo(),
                usuario == null ? null : nombreCompleto(usuario.getEmpleado()),
                auditoria.getNTabla(),
                auditoria.getAccion(),
                auditoria.getIdRegistro(),
                auditoria.getValorAnterior(),
                auditoria.getValorNuevo(),
                auditoria.getFEvento(),
                auditoria.getIp(),
                auditoria.getTerminal(),
                auditoria.getEstado());
    }

    private void validarFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha desde no puede ser mayor que la fecha hasta.");
        }
    }

    private DatosPeticion datosPeticion() {
        try {
            RequestAttributes atributos = RequestContextHolder.getRequestAttributes();
            if (atributos instanceof ServletRequestAttributes servlet) {
                HttpServletRequest request = servlet.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isBlank()) {
                    ip = request.getRemoteAddr();
                } else {
                    ip = ip.split(",")[0].trim();
                }
                String terminal = request.getHeader("X-Terminal");
                if (terminal == null || terminal.isBlank()) {
                    terminal = request.getHeader("User-Agent");
                }
                if (terminal == null || terminal.isBlank()) {
                    terminal = request.getRemoteHost();
                }
                return new DatosPeticion(ip, terminal);
            }
        } catch (IllegalStateException ignored) {
            // Fuera de una peticion HTTP no hay datos de conexion que registrar.
        }
        return new DatosPeticion(null, null);
    }

    private String nombreCompleto(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) {
            return null;
        }
        return Arrays.asList(
                        empleado.getPersona().getNombre(),
                        empleado.getPersona().getApPaterno(),
                        empleado.getPersona().getApMaterno())
                .stream()
                .filter(valor -> valor != null && !valor.isBlank())
                .reduce((izquierda, derecha) -> izquierda + " " + derecha)
                .orElse("");
    }

    private String limitar(String valor, int longitudMaxima) {
        if (valor == null) {
            return null;
        }
        return valor.length() <= longitudMaxima ? valor : valor.substring(0, longitudMaxima);
    }

    private record DatosPeticion(String ip, String terminal) {
    }
}
