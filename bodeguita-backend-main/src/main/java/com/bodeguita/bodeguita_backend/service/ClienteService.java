package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ClienteDto;
import com.bodeguita.bodeguita_backend.dto.ClienteRequest;
import com.bodeguita.bodeguita_backend.entity.Cliente;
import com.bodeguita.bodeguita_backend.entity.Empresa;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.ClienteRepository;
import com.bodeguita.bodeguita_backend.repository.EmpresaRepository;
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
    private final EmpresaRepository empresaRepository;

    public ClienteService(ClienteRepository clienteRepository, PersonaRepository personaRepository,
            EmpresaRepository empresaRepository) {
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
        this.empresaRepository = empresaRepository;
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
        Empresa empresa = null;
        if (request.idEmpresa() != null) {
            empresa = empresaRepository.findByIdAndEstado(request.idEmpresa(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empresa no encontrada: " + request.idEmpresa() + "."));
        }
        Cliente entidad = new Cliente();
        entidad.setPersona(persona);
        entidad.setEmpresa(empresa);
        entidad.setCodigoCliente(request.codigoCliente());
        entidad.setTipoCliente(request.tipoCliente());
        entidad.setLimiteCredito(request.limiteCredito());
        entidad.setSaldoDeuda(request.saldoDeuda());
        entidad.setPuntos(request.puntos());
        entidad.setFRegistro(request.fRegistro());
        entidad = clienteRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public ClienteDto actualizar(Long id, ClienteRequest request) {
        Cliente entidad = buscarActivo(id);
        Persona persona = personaRepository.findByIdAndEstado(request.idPersona(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        Empresa empresa = null;
        if (request.idEmpresa() != null) {
            empresa = empresaRepository.findByIdAndEstado(request.idEmpresa(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empresa no encontrada: " + request.idEmpresa() + "."));
        }
        entidad.setPersona(persona);
        entidad.setEmpresa(empresa);
        entidad.setCodigoCliente(request.codigoCliente());
        entidad.setTipoCliente(request.tipoCliente());
        entidad.setLimiteCredito(request.limiteCredito());
        entidad.setSaldoDeuda(request.saldoDeuda());
        entidad.setPuntos(request.puntos());
        entidad.setFRegistro(request.fRegistro());
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
                entidad.getEmpresa() != null ? entidad.getEmpresa().getId() : null,
                entidad.getCodigoCliente(),
                entidad.getTipoCliente(),
                entidad.getLimiteCredito(),
                entidad.getSaldoDeuda(),
                entidad.getPuntos(),
                entidad.getFRegistro()
        );
    }
}



