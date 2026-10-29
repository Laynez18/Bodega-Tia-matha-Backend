package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ClienteDto;
import com.bodeguita.bodeguita_backend.dto.ClienteRequest;
import com.bodeguita.bodeguita_backend.entity.Cliente;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.ClienteRepository;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;

    public ClienteService(ClienteRepository clienteRepository, PersonaRepository personaRepository) {
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
    }

    public List<ClienteDto> listar() {
        return clienteRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public ClienteDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public ClienteDto crear(ClienteRequest request) {
        Persona persona = personaRepository.findByIdAndEstado(request.idPersona(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        if (clienteRepository.existsByPersona_IdAndEstadoTrue(request.idPersona())) {
            throw new ResponseStatusException(CONFLICT, "La persona ya tiene un cliente registrado.");
        }
        Cliente entidad = new Cliente();
        entidad.setPersona(persona);
        entidad.setTipoCliente(request.tipoCliente());
        entidad.setLimiteCredito(request.limiteCredito());
        entidad.setObservacion(request.observacion());
        entidad = clienteRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public ClienteDto actualizar(Long id, ClienteRequest request) {
        Cliente entidad = buscarActivo(id);
        Persona persona = personaRepository.findByIdAndEstado(request.idPersona(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        entidad.setPersona(persona);
        entidad.setTipoCliente(request.tipoCliente());
        entidad.setLimiteCredito(request.limiteCredito());
        entidad.setObservacion(request.observacion());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Cliente buscarActivo(Long id) {
        return clienteRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente no encontrado: " + id + "."));
    }

    private ClienteDto aDto(Cliente entidad) {
        Persona p = entidad.getPersona();
        return new ClienteDto(
                entidad.getId(),
                p.getIdPersona(),
                p.getNombre() + " " + (p.getApPaterno() == null ? "" : p.getApPaterno()) + " " + (p.getApMaterno() == null ? "" : p.getApMaterno()),
                p.getNDocumento(),
                entidad.getTipoCliente(),
                entidad.getLimiteCredito(),
                entidad.getObservacion()
        );
    }
}



