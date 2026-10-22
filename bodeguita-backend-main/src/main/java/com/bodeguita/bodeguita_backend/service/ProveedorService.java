package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ProveedorDto;
import com.bodeguita.bodeguita_backend.dto.ProveedorRequest;
import com.bodeguita.bodeguita_backend.entity.Proveedor;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.ProveedorRepository;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final PersonaRepository personaRepository;

    public ProveedorService(ProveedorRepository proveedorRepository, PersonaRepository personaRepository) {
        this.proveedorRepository = proveedorRepository;
        this.personaRepository = personaRepository;
    }

    public List<ProveedorDto> listar() {
        return proveedorRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public ProveedorDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public ProveedorDto crear(ProveedorRequest request) {
        Persona persona = personaRepository.findByIdAndEstadoTrue(request.idPersona())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        if (proveedorRepository.existsByPersonaIdAndEstadoTrue(request.idPersona())) {
            throw new ResponseStatusException(CONFLICT, "La persona ya tiene un proveedor registrado.");
        }
        Proveedor entidad = new Proveedor();
        entidad.setPersona(persona);
        entidad.setRazonSocial(request.razonSocial());
        entidad.setTelefono(request.telefono());
        entidad.setEmail(request.email());
        entidad.setDireccion(request.direccion());
        entidad = proveedorRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public ProveedorDto actualizar(Long id, ProveedorRequest request) {
        Proveedor entidad = buscarActivo(id);
        Persona persona = personaRepository.findByIdAndEstadoTrue(request.idPersona())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        entidad.setPersona(persona);
        entidad.setRazonSocial(request.razonSocial());
        entidad.setTelefono(request.telefono());
        entidad.setEmail(request.email());
        entidad.setDireccion(request.direccion());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado(Boolean.FALSE);
    }

    private Proveedor buscarActivo(Long id) {
        return proveedorRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado: " + id + "."));
    }

    private ProveedorDto aDto(Proveedor entidad) {
        Persona p = entidad.getPersona();
        return new ProveedorDto(
                entidad.getId(),
                p.getIdPersona(),
                p.getNombre() + " " + (p.getApPaterno() == null ? "" : p.getApPaterno()) + " " + (p.getApMaterno() == null ? "" : p.getApMaterno()),
                p.getNDocumento(),
                entidad.getRazonSocial(),
                entidad.getTelefono(),
                entidad.getEmail(),
                entidad.getDireccion()
        );
    }
}
