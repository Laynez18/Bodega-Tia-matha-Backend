package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.dto.PersonaRequest;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;
import com.bodeguita.bodeguita_backend.entity.Distrito;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;
import com.bodeguita.bodeguita_backend.repository.TipoIdentidadRepository;
import com.bodeguita.bodeguita_backend.repository.DistritoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final ConsultaSunatService consultaSunatService;
    private final TipoIdentidadRepository tipoIdentidadRepository;
    private final DistritoRepository distritoRepository;

    public PersonaService(PersonaRepository personaRepository, ConsultaSunatService consultaSunatService,
            TipoIdentidadRepository tipoIdentidadRepository, DistritoRepository distritoRepository) {
        this.personaRepository = personaRepository;
        this.consultaSunatService = consultaSunatService;
        this.tipoIdentidadRepository = tipoIdentidadRepository;
        this.distritoRepository = distritoRepository;
    }

    public List<PersonaDto> listar() {
        return personaRepository.findByEstadoOrderByNombreAsc("1").stream()
                .map(this::aDto)
                .collect(Collectors.toList());
    }

    public PersonaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    public DniResponse consultarDni(String dni) {
        return consultaSunatService.consultarDni(dni);
    }

    public RucResponse consultarRuc(String ruc) {
        return consultaSunatService.consultarRuc(ruc);
    }

    @Transactional
    public PersonaDto crear(PersonaRequest request) {
        if (personaRepository.existsByNDocumento(request.nDocumento())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una persona con el documento " + request.nDocumento() + ".");
        }
        Persona entidad = new Persona();
        if (request.idTipoIdentidad() != null) {
            entidad.setTipoIdentidad(tipoIdentidadRepository.findByIdAndEstado(request.idTipoIdentidad(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de identidad no encontrado: " + request.idTipoIdentidad() + ".")));
        }
        if (request.idDistrito() != null) {
            entidad.setDistrito(distritoRepository.findByIdAndEstado(request.idDistrito(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Distrito no encontrado: " + request.idDistrito() + ".")));
        }
        entidad.setNDocumento(request.nDocumento());
        entidad.setNombre(request.nombre());
        entidad.setApPaterno(request.apPaterno());
        entidad.setApMaterno(request.apMaterno());
        entidad.setFNacimiento(request.fNacimiento());
        entidad.setEmail(request.email());
        entidad.setCelular(request.celular());
        entidad.setGenero(request.genero());
        entidad.setDireccion(request.direccion());
        return aDto(personaRepository.save(entidad));
    }

    @Transactional
    public PersonaDto crearDesdeDni(String dni, Long idTipoIdentidad, Long idDistrito) {
        if (personaRepository.existsByNDocumento(dni)) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una persona con el DNI " + dni + ".");
        }
        DniResponse d = consultaSunatService.consultarDni(dni);
        Persona entidad = new Persona();
        // DNI = tipo_identidad = 1 (DNI), buscar por abreviatura o id fijo
        if (idTipoIdentidad != null) {
            entidad.setTipoIdentidad(tipoIdentidadRepository.findByIdAndEstado(idTipoIdentidad, "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de identidad no encontrado: " + idTipoIdentidad + ".")));
        } else {
            entidad.setTipoIdentidad(tipoIdentidadRepository.findAllActivosOrdenados().stream()
                    .filter(t -> "DNI".equalsIgnoreCase(t.getNTipoIdentidad()) || "1".equals(t.getAbreviatura()))
                    .findFirst()
                    .orElse(null));
        }
        if (idDistrito != null) {
            entidad.setDistrito(distritoRepository.findByIdAndEstado(idDistrito, "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Distrito no encontrado: " + idDistrito + ".")));
        }
        entidad.setNDocumento(dni);
        entidad.setNombre(d.data().nombres());
        entidad.setApPaterno(d.data().apellidoPaterno());
        entidad.setApMaterno(d.data().apellidoMaterno());
        return aDto(personaRepository.save(entidad));
    }

    @Transactional
    public PersonaDto crearDesdeRuc(String ruc, Long idTipoIdentidad, Long idDistrito) {
        if (personaRepository.existsByNDocumento(ruc)) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una persona con el RUC " + ruc + ".");
        }
        RucResponse r = consultaSunatService.consultarRuc(ruc);
        Persona entidad = new Persona();
        // RUC = tipo_identidad = 6 (RUC), buscar por abreviatura
        if (idTipoIdentidad != null) {
            entidad.setTipoIdentidad(tipoIdentidadRepository.findByIdAndEstado(idTipoIdentidad, "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de identidad no encontrado: " + idTipoIdentidad + ".")));
        } else {
            entidad.setTipoIdentidad(tipoIdentidadRepository.findAllActivosOrdenados().stream()
                    .filter(t -> "RUC".equalsIgnoreCase(t.getNTipoIdentidad()) || "6".equals(t.getAbreviatura()))
                    .findFirst()
                    .orElse(null));
        }
        if (idDistrito != null) {
            entidad.setDistrito(distritoRepository.findByIdAndEstado(idDistrito, "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Distrito no encontrado: " + idDistrito + ".")));
        }
        entidad.setNDocumento(ruc);
        entidad.setNombre(r.data().razonSocial());
        return aDto(personaRepository.save(entidad));
    }

    @Transactional
    public PersonaDto actualizar(Long id, PersonaRequest request) {
        Persona entidad = buscarActivo(id);
        if (request.idTipoIdentidad() != null) {
            entidad.setTipoIdentidad(tipoIdentidadRepository.findByIdAndEstado(request.idTipoIdentidad(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Tipo de identidad no encontrado: " + request.idTipoIdentidad() + ".")));
        }
        if (request.idDistrito() != null) {
            entidad.setDistrito(distritoRepository.findByIdAndEstado(request.idDistrito(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Distrito no encontrado: " + request.idDistrito() + ".")));
        }
        entidad.setNDocumento(request.nDocumento());
        entidad.setNombre(request.nombre());
        entidad.setApPaterno(request.apPaterno());
        entidad.setApMaterno(request.apMaterno());
        entidad.setFNacimiento(request.fNacimiento());
        entidad.setEmail(request.email());
        entidad.setCelular(request.celular());
        entidad.setGenero(request.genero());
        entidad.setDireccion(request.direccion());
        return aDto(personaRepository.save(entidad));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Persona buscarActivo(Long id) {
        return personaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + id + "."));
    }

    private PersonaDto aDto(Persona p) {
        return new PersonaDto(
                p.getIdPersona(),
                p.getDistrito() != null ? p.getDistrito().getIdDistrito() : null,
                p.getTipoIdentidad() != null ? p.getTipoIdentidad().getIdTipoIdentidad() : null,
                p.getNDocumento(),
                p.getNombre(),
                p.getApPaterno(),
                p.getApMaterno(),
                p.getFNacimiento(),
                p.getEmail(),
                p.getCelular(),
                p.getGenero(),
                p.getDireccion()
        );
    }
}
