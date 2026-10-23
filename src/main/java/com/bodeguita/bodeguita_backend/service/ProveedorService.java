package com.bodeguita.bodeguita_backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.dto.PersonaRequest;
import com.bodeguita.bodeguita_backend.dto.ProveedorDto;
import com.bodeguita.bodeguita_backend.dto.ProveedorRequest;
import com.bodeguita.bodeguita_backend.entity.Distrito;
import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;
import com.bodeguita.bodeguita_backend.entity.Empresa;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.entity.Proveedor;
import com.bodeguita.bodeguita_backend.repository.DistritoRepository;
import com.bodeguita.bodeguita_backend.repository.EmpresaRepository;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;
import com.bodeguita.bodeguita_backend.repository.ProveedorRepository;
import com.bodeguita.bodeguita_backend.repository.TipoIdentidadRepository;

@Service
@Transactional(readOnly = true)
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final PersonaRepository personaRepository;
    private final TipoIdentidadRepository tipoIdentidadRepository;
    private final DistritoRepository distritoRepository;
    private final EmpresaRepository empresaRepository;
    private final AuditoriaService auditoriaService;

    public ProveedorService(
            ProveedorRepository proveedorRepository,
            PersonaRepository personaRepository,
            TipoIdentidadRepository tipoIdentidadRepository,
            DistritoRepository distritoRepository,
            EmpresaRepository empresaRepository,
            AuditoriaService auditoriaService) {
        this.proveedorRepository = proveedorRepository;
        this.personaRepository = personaRepository;
        this.tipoIdentidadRepository = tipoIdentidadRepository;
        this.distritoRepository = distritoRepository;
        this.empresaRepository = empresaRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<ProveedorDto> listar() {
        return proveedorRepository.findAllConRelaciones().stream().map(this::aDto).toList();
    }

    public ProveedorDto obtener(Long idProveedor) {
        return aDto(buscarActivo(idProveedor));
    }

    @Transactional
    public ProveedorDto crear(ProveedorRequest request) {
        PersonaRequest pReq = request.persona();
        validarDocumento(pReq, null);
        validarDocumentoLongitud(pReq);
        Persona persona = crearPersona(pReq);
        Proveedor proveedor = new Proveedor();
        proveedor.setPersona(persona);
        proveedor.setEmpresa(buscarEmpresa(request.idEmpresa()));
        proveedor.setCodigoProveedor(normalizarOpcional(request.codigoProveedor()));
        proveedor.setContacto(normalizarOpcional(request.contacto()));
        proveedor.setTelefonoContacto(normalizarOpcional(request.telefonoContacto()));
        proveedor.setEmailContacto(normalizarOpcional(request.emailContacto()));
        proveedor.setDiasCredito(request.diasCredito() == null ? 0 : request.diasCredito());
        proveedor.setFRegistro(LocalDateTime.now());
        proveedor.setEstado(Boolean.TRUE);
        proveedorRepository.saveAndFlush(proveedor);
        try {
            auditoriaService.registrar("persona", "INSERT", persona.getIdPersona(), null, resumen(persona));
            auditoriaService.registrar("proveedor", "INSERT", proveedor.getIdProveedor(), null, resumen(proveedor));
        } catch (Exception ex) {
            // No bloquear la operación por fallo de auditoría
        }
        return aDto(proveedor);
    }

    @Transactional
    public ProveedorDto actualizar(Long idProveedor, ProveedorRequest request) {
        Proveedor proveedor = buscarActivo(idProveedor);
        Persona persona = proveedor.getPersona();
        PersonaRequest pReq = request.persona();
        validarDocumento(pReq, persona == null ? null : persona.getIdPersona());
        validarDocumentoLongitud(pReq);
        String personaAnterior = persona == null ? null : resumen(persona);
        String proveedorAnterior = resumen(proveedor);

        if (persona == null) {
            persona = crearPersona(pReq);
            proveedor.setPersona(persona);
        } else {
            actualizarPersona(persona, pReq);
        }
        proveedor.setEmpresa(buscarEmpresa(request.idEmpresa()));
        proveedor.setCodigoProveedor(normalizarOpcional(request.codigoProveedor()));
        proveedor.setContacto(normalizarOpcional(request.contacto()));
        proveedor.setTelefonoContacto(normalizarOpcional(request.telefonoContacto()));
        proveedor.setEmailContacto(normalizarOpcional(request.emailContacto()));
        if (request.diasCredito() != null) {
            proveedor.setDiasCredito(request.diasCredito());
        }

        proveedorRepository.saveAndFlush(proveedor);
        if (personaAnterior != null) {
            auditoriaService.registrar("persona", "UPDATE", persona.getIdPersona(), personaAnterior, resumen(persona));
        }
        auditoriaService.registrar("proveedor", "UPDATE", idProveedor, proveedorAnterior, resumen(proveedor));
        return aDto(proveedor);
    }

    @Transactional
    public ProveedorDto cambiarEstado(Long idProveedor, EstadoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> noEncontrado("Proveedor", idProveedor));
        String anterior = resumen(proveedor);
        boolean nuevoEstado = "1".equals(request.estado());
        if (Boolean.TRUE.equals(proveedor.getEstado()) == nuevoEstado) {
            return aDto(proveedor);
        }
        proveedor.setEstado(nuevoEstado);
        proveedorRepository.saveAndFlush(proveedor);
        auditoriaService.registrar("proveedor", nuevoEstado ? "ACTIVAR" : "DESACTIVAR", idProveedor, anterior, resumen(proveedor));
        return aDto(proveedor);
    }

    @Transactional
    public void desactivar(Long idProveedor) {
        Proveedor proveedor = buscarActivo(idProveedor);
        String anterior = resumen(proveedor);
        proveedor.setEstado(Boolean.FALSE);
        proveedorRepository.saveAndFlush(proveedor);
        auditoriaService.registrar("proveedor", "DESACTIVAR", idProveedor, anterior, resumen(proveedor));
    }

    private Proveedor buscarActivo(Long idProveedor) {
        return proveedorRepository.findActivoConRelaciones(idProveedor)
                .orElseThrow(() -> noEncontrado("Proveedor", idProveedor));
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

    private Persona crearPersona(PersonaRequest request) {
        Persona persona = new Persona();
        actualizarPersona(persona, request);
        persona.setEstado(Boolean.TRUE);
        personaRepository.saveAndFlush(persona);
        return persona;
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

    private Distrito buscarDistrito(Long idDistrito) {
        if (idDistrito == null) {
            return null;
        }
        return distritoRepository.findById(idDistrito)
                .filter(distrito -> Boolean.TRUE.equals(distrito.getEstado()))
                .orElseThrow(() -> noEncontrado("Distrito", idDistrito));
    }

    private Empresa buscarEmpresa(Long idEmpresa) {
        if (idEmpresa == null) {
            return null;
        }
        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> noEncontrado("Empresa", idEmpresa));
    }

    private ProveedorDto aDto(Proveedor proveedor) {
        Persona persona = proveedor.getPersona();
        PersonaDto personaDto = null;
        if (persona != null) {
            personaDto = new PersonaDto(
                    persona.getIdPersona(),
                    persona.getDistrito() == null ? null : persona.getDistrito().getIdDistrito(),
                    persona.getTipoIdentidad() == null ? null : persona.getTipoIdentidad().getIdTipoIdentidad(),
                    persona.getNDocumento(),
                    persona.getNombre(),
                    persona.getApPaterno(),
                    persona.getApMaterno(),
                    persona.getFNacimiento(),
                    persona.getEmail(),
                    persona.getCelular(),
                    persona.getGenero(),
                    persona.getDireccion());
        }
        return new ProveedorDto(
                proveedor.getIdProveedor(),
                personaDto,
                proveedor.getEmpresa() == null ? null : proveedor.getEmpresa().getIdEmpresa(),
                proveedor.getCodigoProveedor(),
                proveedor.getContacto(),
                proveedor.getTelefonoContacto(),
                proveedor.getEmailContacto(),
                proveedor.getDiasCredito(),
                proveedor.getFRegistro(),
                proveedor.getEstado());
    }

    private String resumen(Persona persona) {
        return "documento=" + texto(persona.getNDocumento())
                + "; nombre=" + nombreCompleto(persona)
                + "; tipoIdentidad=" + (persona.getTipoIdentidad() == null
                        ? ""
                        : texto(persona.getTipoIdentidad().getNTipoIdentidad()))
                + "; email=" + texto(persona.getEmail())
                + "; celular=" + texto(persona.getCelular());
    }

    private String resumen(Proveedor proveedor) {
        return "codigo=" + texto(proveedor.getCodigoProveedor())
                + "; contacto=" + texto(proveedor.getContacto())
                + "; diasCredito=" + texto(proveedor.getDiasCredito())
                + "; estado=" + estado(proveedor.getEstado());
    }

    private String nombreCompleto(Persona persona) {
        return Arrays.asList(persona.getNombre(), persona.getApPaterno(), persona.getApMaterno())
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
