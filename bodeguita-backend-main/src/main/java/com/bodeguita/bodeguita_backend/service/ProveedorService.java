package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ProveedorDto;
import com.bodeguita.bodeguita_backend.dto.ProveedorRequest;
import com.bodeguita.bodeguita_backend.entity.Empresa;
import com.bodeguita.bodeguita_backend.entity.Proveedor;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.EmpresaRepository;
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
    private final EmpresaRepository empresaRepository;

    public ProveedorService(ProveedorRepository proveedorRepository, PersonaRepository personaRepository,
            EmpresaRepository empresaRepository) {
        this.proveedorRepository = proveedorRepository;
        this.personaRepository = personaRepository;
        this.empresaRepository = empresaRepository;
    }

    public List<ProveedorDto> listar() {
        return proveedorRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public ProveedorDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public ProveedorDto crear(ProveedorRequest request) {
        Persona persona = personaRepository.findByIdAndEstado(request.idPersona(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        if (proveedorRepository.existsByPersona_IdAndEstadoTrue(request.idPersona())) {
            throw new ResponseStatusException(CONFLICT, "La persona ya tiene un proveedor registrado.");
        }
        Empresa empresa = null;
        if (request.idEmpresa() != null) {
            empresa = empresaRepository.findByIdAndEstado(request.idEmpresa(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empresa no encontrada: " + request.idEmpresa() + "."));
        }
        Proveedor entidad = new Proveedor();
        entidad.setEmpresa(empresa);
        entidad.setPersona(persona);
        entidad.setCodigoProveedor(request.codigoProveedor());
        entidad.setContacto(request.contacto());
        entidad.setTelefonoContacto(request.telefonoContacto());
        entidad.setEmailContacto(request.emailContacto());
        entidad.setDiasCredito(request.diasCredito());
        entidad = proveedorRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public ProveedorDto actualizar(Long id, ProveedorRequest request) {
        Proveedor entidad = buscarActivo(id);
        Persona persona = personaRepository.findByIdAndEstado(request.idPersona(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Persona no encontrada: " + request.idPersona() + "."));
        Empresa empresa = null;
        if (request.idEmpresa() != null) {
            empresa = empresaRepository.findByIdAndEstado(request.idEmpresa(), "1")
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empresa no encontrada: " + request.idEmpresa() + "."));
        }
        entidad.setEmpresa(empresa);
        entidad.setPersona(persona);
        entidad.setCodigoProveedor(request.codigoProveedor());
        entidad.setContacto(request.contacto());
        entidad.setTelefonoContacto(request.telefonoContacto());
        entidad.setEmailContacto(request.emailContacto());
        entidad.setDiasCredito(request.diasCredito());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Proveedor buscarActivo(Long id) {
        return proveedorRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Proveedor no encontrado: " + id + "."));
    }

    private ProveedorDto aDto(Proveedor entidad) {
        Persona p = entidad.getPersona();
        return new ProveedorDto(
                entidad.getId(),
                p.getIdPersona(),
                p.getNombre() + " " + (p.getApPaterno() == null ? "" : p.getApPaterno()) + " " + (p.getApMaterno() == null ? "" : p.getApMaterno()),
                p.getNDocumento(),
                entidad.getEmpresa() != null ? entidad.getEmpresa().getId() : null,
                entidad.getCodigoProveedor(),
                entidad.getContacto(),
                entidad.getTelefonoContacto(),
                entidad.getEmailContacto(),
                entidad.getDiasCredito()
        );
    }
}



