package com.bodeguita.bodeguita_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.EmpleadoDto;
import com.bodeguita.bodeguita_backend.dto.EmpleadoRequest;
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

@Service
@Transactional(readOnly = true)
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final TipoIdentidadRepository tipoIdentidadRepository;
    private final DistritoRepository distritoRepository;
    private final ContratoRepository contratoRepository;
    private final CargoRepository cargoRepository;

    public EmpleadoService(
            EmpleadoRepository empleadoRepository,
            PersonaRepository personaRepository,
            TipoIdentidadRepository tipoIdentidadRepository,
            DistritoRepository distritoRepository,
            ContratoRepository contratoRepository,
            CargoRepository cargoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.personaRepository = personaRepository;
        this.tipoIdentidadRepository = tipoIdentidadRepository;
        this.distritoRepository = distritoRepository;
        this.contratoRepository = contratoRepository;
        this.cargoRepository = cargoRepository;
    }

    public List<EmpleadoDto> listar() {
        return empleadoRepository.findAllActivosConRelaciones().stream().map(this::aDto).toList();
    }

    public EmpleadoDto obtener(Long idEmpleado) {
        return aDto(buscarActivo(idEmpleado));
    }

    @Transactional
    public EmpleadoDto crear(EmpleadoRequest request) {
        Persona persona = new Persona();
        actualizarPersona(persona, request.persona());
        persona.setEstado(Boolean.TRUE);
        personaRepository.save(persona);

        Empleado empleado = new Empleado();
        empleado.setPersona(persona);
        actualizarEmpleado(empleado, request);
        empleado.setEstado(Boolean.TRUE);
        return aDto(empleadoRepository.save(empleado));
    }

    @Transactional
    public EmpleadoDto actualizar(Long idEmpleado, EmpleadoRequest request) {
        Empleado empleado = buscarActivo(idEmpleado);
        actualizarPersona(empleado.getPersona(), request.persona());
        actualizarEmpleado(empleado, request);
        return aDto(empleado);
    }

    @Transactional
    public void desactivar(Long idEmpleado) {
        buscarActivo(idEmpleado).setEstado(Boolean.FALSE);
    }

    private Empleado buscarActivo(Long idEmpleado) {
        return empleadoRepository.findActivoConRelaciones(idEmpleado)
                .orElseThrow(() -> noEncontrado("Empleado", idEmpleado));
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
                empleado.getEssalud());
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private ResponseStatusException noEncontrado(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " no encontrado: " + id + ".");
    }
}
