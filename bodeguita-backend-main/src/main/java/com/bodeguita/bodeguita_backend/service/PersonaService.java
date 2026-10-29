package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.DniResponse;
import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.dto.PersonaRequest;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final ConsultaSunatService consultaSunatService;

    public PersonaService(PersonaRepository personaRepository, ConsultaSunatService consultaSunatService) {
        this.personaRepository = personaRepository;
        this.consultaSunatService = consultaSunatService;
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

    @Transactional
    public PersonaDto crear(PersonaRequest request) {
        if (personaRepository.existsByNDocumento(request.nDocumento())) {
            throw new ResponseStatusException(CONFLICT, "Ya existe una persona con el documento " + request.nDocumento() + ".");
        }
        Persona entidad = new Persona();
        entidad.setDistrito(null); // se asigna por id si se quiere
        entidad.setTipoIdentidad(null);
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
        entidad.setNDocumento(dni);
        entidad.setNombre(d.nombre());
        entidad.setApPaterno(d.apellidoPaterno());
        entidad.setApMaterno(d.apellidoMaterno());
        return aDto(personaRepository.save(entidad));
    }

    @Transactional
    public PersonaDto actualizar(Long id, PersonaRequest request) {
        Persona entidad = buscarActivo(id);
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
