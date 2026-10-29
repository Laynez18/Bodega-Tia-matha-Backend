package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.EmpresaDto;
import com.bodeguita.bodeguita_backend.dto.EmpresaRequest;
import com.bodeguita.bodeguita_backend.dto.RucResponse;
import com.bodeguita.bodeguita_backend.entity.Empresa;
import com.bodeguita.bodeguita_backend.repository.EmpresaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final ConsultaSunatService consultaSunatService;

    public EmpresaService(EmpresaRepository empresaRepository, ConsultaSunatService consultaSunatService) {
        this.empresaRepository = empresaRepository;
        this.consultaSunatService = consultaSunatService;
    }

    public List<EmpresaDto> listar() {
        return empresaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public EmpresaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public EmpresaDto crear(EmpresaRequest request) {
        Empresa entidad = new Empresa();
        entidad.setRuc(request.ruc());
        entidad.setRazonSocial(request.razonSocial());
        entidad.setDireccion(request.direccion());
        entidad.setTelefono(request.telefono());
        return aDto(empresaRepository.save(entidad));
    }

    @Transactional
    public EmpresaDto crearDesdeSunat(String ruc) {
        if (empresaRepository.existsByRuc(ruc)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "La empresa con RUC " + ruc + " ya existe.");
        }
        RucResponse r = consultaSunatService.consultarRuc(ruc);
        Empresa entidad = new Empresa();
        entidad.setRuc(r.ruc());
        entidad.setRazonSocial(r.razonSocial() != null ? r.razonSocial() : r.nombreComercial());
        entidad.setDireccion(r.direccion());
        entidad.setTelefono(null);
        return aDto(empresaRepository.save(entidad));
    }

    @Transactional
    public EmpresaDto actualizar(Long id, EmpresaRequest request) {
        Empresa existente = buscarActivo(id);
        existente.setRuc(request.ruc());
        existente.setRazonSocial(request.razonSocial());
        existente.setDireccion(request.direccion());
        existente.setTelefono(request.telefono());
        return aDto(empresaRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Empresa buscarActivo(Long id) {
        return empresaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Empresa no encontrada: " + id + "."));
    }

    private EmpresaDto aDto(Empresa entidad) {
        return new EmpresaDto(entidad.getId(), entidad.getRuc(), entidad.getRazonSocial(),
                entidad.getDireccion(), entidad.getTelefono(), entidad.getEstado());
    }
}
