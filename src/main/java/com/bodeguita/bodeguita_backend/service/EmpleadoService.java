package com.bodeguita.bodeguita_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.EmpleadoDto;
import com.bodeguita.bodeguita_backend.dto.EmpleadoRequest;
import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.dto.PersonaRequest;
import com.bodeguita.bodeguita_backend.entity.Cargo;
import com.bodeguita.bodeguita_backend.entity.Contrato;
import com.bodeguita.bodeguita_backend.entity.Distrito;
import com.bodeguita.bodeguita_backend.entity.Empleado;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;
import com.bodeguita.bodeguita_backend.repository.CargoRepository;
import com.bodeguita.bodeguita_backend.repository.ContratoRepository;
import com.bodeguita.bodeguita_backend.repository.DistritoRepository;
import com.bodeguita.bodeguita_backend.repository.EmpleadoRepository;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;
import com.bodeguita.bodeguita_backend.repository.TipoIdentidadRepository;
import com.bodeguita.bodeguita_backend.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final TipoIdentidadRepository tipoIdentidadRepository;
    private final DistritoRepository distritoRepository;
    private final ContratoRepository contratoRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public EmpleadoService(
            EmpleadoRepository empleadoRepository,
            PersonaRepository personaRepository,
            TipoIdentidadRepository tipoIdentidadRepository,
            DistritoRepository distritoRepository,
            ContratoRepository contratoRepository,
            CargoRepository cargoRepository,
            UsuarioRepository usuarioRepository,
            AuditoriaService auditoriaService) {
        this.empleadoRepository = empleadoRepository;
        this.personaRepository = personaRepository;
        this.tipoIdentidadRepository = tipoIdentidadRepository;
        this.distritoRepository = distritoRepository;
        this.contratoRepository = contratoRepository;
        this.cargoRepository = cargoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<EmpleadoDto> listar() {
        return empleadoRepository.findAllConRelaciones().stream().map(this::aDto).toList();
    }

    public EmpleadoDto obtener(Long idEmpleado) {
        return aDto(buscarActivo(idEmpleado));
    }

    @Transactional
    public EmpleadoDto crear(EmpleadoRequest request) {
        validarDocumento(request.persona(), null);
        validarDocumentoLongitud(request.persona());
        Persona persona = new Persona();
        actualizarPersona(persona, request.persona());
        persona.setEstado(Boolean.TRUE);
        Persona personaGuardada = personaRepository.saveAndFlush(persona);
        auditoriaService.registrar(
                "persona", "INSERT", personaGuardada.getIdPersona(), null, resumen(personaGuardada));

        Empleado empleado = new Empleado();
        empleado.setPersona(personaGuardada);
        actualizarEmpleado(empleado, request);
        empleado.setEstado(Boolean.TRUE);
        Empleado empleadoGuardado = empleadoRepository.saveAndFlush(empleado);
        auditoriaService.registrar(
                "empleado", "INSERT", empleadoGuardado.getIdEmpleado(), null, resumen(empleadoGuardado));
        return aDto(empleadoGuardado);
    }

    @Transactional
    public EmpleadoDto actualizar(Long idEmpleado, EmpleadoRequest request) {
        Empleado empleado = buscarActivo(idEmpleado);
        Persona persona = empleado.getPersona();
        validarDocumento(request.persona(), persona == null ? null : persona.getIdPersona());
        validarDocumentoLongitud(request.persona());
        String personaAnterior = resumen(persona);
        String empleadoAnterior = resumen(empleado);
        actualizarPersona(persona, request.persona());
        actualizarEmpleado(empleado, request);
        personaRepository.saveAndFlush(persona);
        empleadoRepository.saveAndFlush(empleado);
        auditoriaService.registrar(
                "persona",
                "UPDATE",
                persona.getIdPersona(),
                personaAnterior,
                resumen(persona));
        auditoriaService.registrar(
                "empleado", "UPDATE", idEmpleado, empleadoAnterior, resumen(empleado));
        return aDto(empleado);
    }

    @Transactional
    public EmpleadoDto cambiarEstado(Long idEmpleado, EstadoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }
        Empleado empleado = empleadoRepository.findByIdConRelaciones(idEmpleado)
                .orElseThrow(() -> noEncontrado("Empleado", idEmpleado));
        String anterior = resumen(empleado);
        boolean nuevoEstado = "1".equals(request.estado());
        if (Boolean.TRUE.equals(empleado.getEstado()) == nuevoEstado) {
            return aDto(empleado);
        }
        if (!nuevoEstado && usuarioRepository.existsByEmpleado_IdEmpleadoAndEstadoTrue(idEmpleado)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede desactivar el empleado porque tiene un usuario activo asociado.");
        }
        empleado.setEstado(nuevoEstado);
        empleadoRepository.saveAndFlush(empleado);
        auditoriaService.registrar(
                "empleado", nuevoEstado ? "ACTIVAR" : "DESACTIVAR", idEmpleado, anterior, resumen(empleado));
        return aDto(empleado);
    }

    @Transactional
    public void desactivar(Long idEmpleado) {
        Empleado empleado = buscarActivo(idEmpleado);
        String anterior = resumen(empleado);
        empleado.setEstado(Boolean.FALSE);
        empleadoRepository.saveAndFlush(empleado);
        auditoriaService.registrar(
                "empleado", "DESACTIVAR", idEmpleado, anterior, resumen(empleado));
    }

    private Empleado buscarActivo(Long idEmpleado) {
        return empleadoRepository.findActivoConRelaciones(idEmpleado)
                .orElseThrow(() -> noEncontrado("Empleado", idEmpleado));
    }

    private void validarDocumento(PersonaRequest request, Long idPersonaExcluir) {
        if (request == null || request.idTipoIdentidad() == null || request.nDocumento() == null) {
            return;
        }
        String documento = request.nDocumento().trim();
        boolean existe = idPersonaExcluir == null
                ? personaRepository.existsByTipoIdentidadAndDocumento(request.idTipoIdentidad(), documento)
                : personaRepository.existsByTipoIdentidadAndDocumentoExceptId(request.idTipoIdentidad(), documento, idPersonaExcluir);
        if (existe) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El numero de documento ya esta registrado.");
        }
    }

    private void validarDocumentoLongitud(PersonaRequest request) {
        if (request == null || request.idTipoIdentidad() == null || request.nDocumento() == null) {
            return;
        }
        String documento = request.nDocumento().trim();
        TipoIdentidad tipoIdentidad = tipoIdentidadRepository
                .findByIdTipoIdentidadAndEstadoTrue(request.idTipoIdentidad())
                .orElseThrow(() -> noEncontrado("Tipo de identidad", request.idTipoIdentidad()));
        Integer longitud = tipoIdentidad.getLongitud();
        if (longitud != null && documento.length() != longitud) {
            String nombreTipo = tipoIdentidad.getNTipoIdentidad() == null ? "" : tipoIdentidad.getNTipoIdentidad();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El numero de documento debe tener " + longitud + " caracteres" + (nombreTipo.isBlank() ? "." : " para " + nombreTipo + "."));
        }
    }

    private void actualizarEmpleado(Empleado empleado, EmpleadoRequest request) {
        empleado.setContrato(buscarContrato(request.idContrato()));
        empleado.setCargo(buscarCargo(request.idCargo()));
        empleado.setSalario(request.salario());
        empleado.setTurno(normalizarOpcional(request.turno()));
        empleado.setFondoPension(normalizarOpcional(request.fondoPension()));
        empleado.setNHps(normalizarOpcional(request.nHps()));
        empleado.setEssalud(normalizarOpcional(request.essalud()));
    }

    private void actualizarPersona(Persona persona, PersonaRequest request) {
        persona.setTipoIdentidad(tipoIdentidadRepository.findByIdTipoIdentidadAndEstadoTrue(request.idTipoIdentidad())
                .orElseThrow(() -> noEncontrado("Tipo de identidad", request.idTipoIdentidad())));
        persona.setDistrito(buscarDistrito(request.idDistrito()));
        persona.setNDocumento(request.nDocumento().trim());
        persona.setNombre(request.nombre().trim());
        persona.setApPaterno(normalizarOpcional(request.apPaterno()));
        persona.setApMaterno(normalizarOpcional(request.apMaterno()));
        persona.setFNacimiento(request.fNacimiento());
        persona.setEmail(normalizarOpcional(request.email()));
        persona.setCelular(normalizarOpcional(request.celular()));
        persona.setGenero(normalizarOpcional(request.genero()));
        persona.setDireccion(normalizarOpcional(request.direccion()));
    }

    private Contrato buscarContrato(Long idContrato) {
        if (idContrato == null) {
            return null;
        }
        return contratoRepository.findByIdContratoAndEstadoTrue(idContrato)
                .orElseThrow(() -> noEncontrado("Contrato", idContrato));
    }

    private Cargo buscarCargo(Long idCargo) {
        if (idCargo == null) {
            return null;
        }
        return cargoRepository.findByIdCargoAndEstadoTrue(idCargo)
                .orElseThrow(() -> noEncontrado("Cargo", idCargo));
    }

    private Distrito buscarDistrito(Long idDistrito) {
        if (idDistrito == null) {
            return null;
        }
        return distritoRepository.findById(idDistrito)
                .filter(distrito -> Boolean.TRUE.equals(distrito.getEstado()))
                .orElseThrow(() -> noEncontrado("Distrito", idDistrito));
    }

    private EmpleadoDto aDto(Empleado empleado) {
        Persona persona = empleado.getPersona();
        return new EmpleadoDto(
                empleado.getIdEmpleado(),
                new PersonaDto(
                        persona.getIdPersona(),
                        persona.getDistrito() == null ? null : persona.getDistrito().getIdDistrito(),
                        persona.getTipoIdentidad().getIdTipoIdentidad(),
                        persona.getNDocumento(),
                        persona.getNombre(),
                        persona.getApPaterno(),
                        persona.getApMaterno(),
                        persona.getFNacimiento(),
                        persona.getEmail(),
                        persona.getCelular(),
                        persona.getGenero(),
                        persona.getDireccion()),
                empleado.getContrato() == null ? null : empleado.getContrato().getIdContrato(),
                empleado.getContrato() == null ? null : empleado.getContrato().getNContrato(),
                empleado.getCargo() == null ? null : empleado.getCargo().getIdCargo(),
                empleado.getCargo() == null ? null : empleado.getCargo().getNCargo(),
                empleado.getSalario(),
                empleado.getTurno(),
                empleado.getFondoPension(),
                empleado.getNHps(),
                empleado.getEssalud(),
                empleado.getEstado());
    }

    private String resumen(Persona persona) {
        return "documento=" + texto(persona.getNDocumento())
                + "; nombre=" + nombreCompleto(persona)
                + "; tipoIdentidad=" + (persona.getTipoIdentidad() == null
                        ? ""
                        : texto(persona.getTipoIdentidad().getNTipoIdentidad()))
                + "; email=" + texto(persona.getEmail())
                + "; celular=" + texto(persona.getCelular())
                + "; estado=" + estado(persona.getEstado());
    }

    private String resumen(Empleado empleado) {
        return "persona=" + (empleado.getPersona() == null ? "" : texto(empleado.getPersona().getIdPersona()))
                + "; contrato=" + (empleado.getContrato() == null
                        ? ""
                        : texto(empleado.getContrato().getIdContrato()))
                + "; cargo=" + (empleado.getCargo() == null
                        ? ""
                        : texto(empleado.getCargo().getIdCargo()))
                + "; salario=" + texto(empleado.getSalario())
                + "; turno=" + texto(empleado.getTurno())
                + "; fondoPension=" + texto(empleado.getFondoPension())
                + "; nHps=" + texto(empleado.getNHps())
                + "; essalud=" + texto(empleado.getEssalud())
                + "; estado=" + estado(empleado.getEstado());
    }

    private String nombreCompleto(Persona persona) {
        return java.util.Arrays.asList(persona.getNombre(), persona.getApPaterno(), persona.getApMaterno())
                .stream()
                .filter(valor -> valor != null && !valor.isBlank())
                .reduce((izquierda, derecha) -> izquierda + " " + derecha)
                .orElse("");
    }

    private String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor);
    }

    private String estado(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "1" : "0";
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private ResponseStatusException noEncontrado(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " no encontrado: " + id + ".");
    }
}
