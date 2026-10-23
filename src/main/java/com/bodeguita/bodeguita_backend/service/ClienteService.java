package com.bodeguita.bodeguita_backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ClienteDto;
import com.bodeguita.bodeguita_backend.dto.ClienteRequest;
import com.bodeguita.bodeguita_backend.dto.EstadoRequest;
import com.bodeguita.bodeguita_backend.dto.PersonaDto;
import com.bodeguita.bodeguita_backend.dto.PersonaRequest;
import com.bodeguita.bodeguita_backend.entity.Cliente;
import com.bodeguita.bodeguita_backend.entity.Distrito;
import com.bodeguita.bodeguita_backend.entity.TipoIdentidad;
import com.bodeguita.bodeguita_backend.entity.Empresa;
import com.bodeguita.bodeguita_backend.entity.Persona;
import com.bodeguita.bodeguita_backend.repository.ClienteRepository;
import com.bodeguita.bodeguita_backend.repository.DistritoRepository;
import com.bodeguita.bodeguita_backend.repository.EmpresaRepository;
import com.bodeguita.bodeguita_backend.repository.PersonaRepository;
import com.bodeguita.bodeguita_backend.repository.TipoIdentidadRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final TipoIdentidadRepository tipoIdentidadRepository;
    private final DistritoRepository distritoRepository;
    private final EmpresaRepository empresaRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(
            ClienteRepository clienteRepository,
            PersonaRepository personaRepository,
            TipoIdentidadRepository tipoIdentidadRepository,
            DistritoRepository distritoRepository,
            EmpresaRepository empresaRepository,
            AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.personaRepository = personaRepository;
        this.tipoIdentidadRepository = tipoIdentidadRepository;
        this.distritoRepository = distritoRepository;
        this.empresaRepository = empresaRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<ClienteDto> listar() {
        return clienteRepository.findAllConRelaciones().stream().map(this::aDto).toList();
    }

    public ClienteDto obtener(Long idCliente) {
        return aDto(buscarActivo(idCliente));
    }

    @Transactional
    public ClienteDto crear(ClienteRequest request) {
        PersonaRequest pReq = request.persona();
        validarDocumento(pReq, null);
        validarDocumentoLongitud(pReq);
        Persona persona = crearPersona(pReq);
        Cliente cliente = new Cliente();
        cliente.setPersona(persona);
        cliente.setEmpresa(buscarEmpresa(request.idEmpresa()));
        cliente.setCodigoCliente(normalizarOpcional(request.codigoCliente()));
        cliente.setTipoCliente(request.tipoCliente() == null ? "N" : request.tipoCliente().trim());
        cliente.setLimiteCredito(request.limiteCredito() == null ? BigDecimal.ZERO : request.limiteCredito());
        cliente.setSaldoDeuda(request.saldoDeuda() == null ? BigDecimal.ZERO : request.saldoDeuda());
        cliente.setPuntos(request.puntos() == null ? 0 : request.puntos());
        cliente.setFRegistro(LocalDateTime.now());
        cliente.setEstado(Boolean.TRUE);
        clienteRepository.saveAndFlush(cliente);
        ClienteDto creado = aDto(cliente);
        try {
            auditoriaService.registrar("persona", "INSERT", persona.getIdPersona(), null, resumen(persona));
            auditoriaService.registrar("cliente", "INSERT", cliente.getIdCliente(), null, resumen(cliente));
        } catch (Exception ex) {
            // No bloquear la operación por fallo de auditoría
        }
        return creado;
    }

    @Transactional
    public ClienteDto actualizar(Long idCliente, ClienteRequest request) {
        Cliente cliente = buscarActivo(idCliente);
        Persona persona = cliente.getPersona();
        PersonaRequest pReq = request.persona();
        validarDocumento(pReq, persona == null ? null : persona.getIdPersona());
        validarDocumentoLongitud(pReq);
        String personaAnterior = persona == null ? null : resumen(persona);
        String clienteAnterior = resumen(cliente);

        if (persona == null) {
            persona = crearPersona(pReq);
            cliente.setPersona(persona);
        } else {
            actualizarPersona(persona, pReq);
        }
        cliente.setEmpresa(buscarEmpresa(request.idEmpresa()));
        cliente.setCodigoCliente(normalizarOpcional(request.codigoCliente()));
        if (request.tipoCliente() != null && !request.tipoCliente().isBlank()) {
            cliente.setTipoCliente(request.tipoCliente().trim());
        }
        if (request.limiteCredito() != null) {
            cliente.setLimiteCredito(request.limiteCredito());
        }
        if (request.saldoDeuda() != null) {
            cliente.setSaldoDeuda(request.saldoDeuda());
        }
        if (request.puntos() != null) {
            cliente.setPuntos(request.puntos());
        }

        clienteRepository.saveAndFlush(cliente);
        if (personaAnterior != null) {
            auditoriaService.registrar("persona", "UPDATE", persona.getIdPersona(), personaAnterior, resumen(persona));
        }
        auditoriaService.registrar("cliente", "UPDATE", idCliente, clienteAnterior, resumen(cliente));
        return aDto(cliente);
    }

    @Transactional
    public ClienteDto cambiarEstado(Long idCliente, EstadoRequest request) {
        if (request == null || request.estado() == null || request.estado().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> noEncontrado("Cliente", idCliente));
        String anterior = resumen(cliente);
        boolean nuevoEstado = "1".equals(request.estado());
        if (Boolean.TRUE.equals(cliente.getEstado()) == nuevoEstado) {
            return aDto(cliente);
        }
        cliente.setEstado(nuevoEstado);
        clienteRepository.saveAndFlush(cliente);
        auditoriaService.registrar("cliente", nuevoEstado ? "ACTIVAR" : "DESACTIVAR", idCliente, anterior, resumen(cliente));
        return aDto(cliente);
    }

    @Transactional
    public void desactivar(Long idCliente) {
        Cliente cliente = buscarActivo(idCliente);
        String anterior = resumen(cliente);
        cliente.setEstado(Boolean.FALSE);
        clienteRepository.saveAndFlush(cliente);
        auditoriaService.registrar("cliente", "DESACTIVAR", idCliente, anterior, resumen(cliente));
    }

    private Cliente buscarActivo(Long idCliente) {
        return clienteRepository.findActivoConRelaciones(idCliente)
                .orElseThrow(() -> noEncontrado("Cliente", idCliente));
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
        TipoIdentidad tipoIdentidad = tipoIdentidadRepository.findByIdTipoIdentidadAndEstadoTrue(request.idTipoIdentidad())
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

    private ClienteDto aDto(Cliente cliente) {
        Persona persona = cliente.getPersona();
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
        return new ClienteDto(
                cliente.getIdCliente(),
                personaDto,
                cliente.getEmpresa() == null ? null : cliente.getEmpresa().getIdEmpresa(),
                cliente.getCodigoCliente(),
                cliente.getTipoCliente(),
                cliente.getLimiteCredito(),
                cliente.getSaldoDeuda(),
                cliente.getPuntos(),
                cliente.getFRegistro(),
                cliente.getEstado());
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

    private String resumen(Cliente cliente) {
        return "tipoCliente=" + texto(cliente.getTipoCliente())
                + "; codigo=" + texto(cliente.getCodigoCliente())
                + "; limiteCredito=" + texto(cliente.getLimiteCredito())
                + "; puntos=" + texto(cliente.getPuntos())
                + "; estado=" + estado(cliente.getEstado());
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
