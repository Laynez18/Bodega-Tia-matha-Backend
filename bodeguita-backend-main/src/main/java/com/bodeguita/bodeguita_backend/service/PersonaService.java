package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public List<PersonaDto> listar() {
        return personaRepository.findByEstadoOrderByNombreAsc("1").stream()
                .map(this::aDto)
                .collect(Collectors.toList());
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
