package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.ProvinciaDto;
import com.bodeguita.bodeguita_backend.dto.ProvinciaRequest;
import com.bodeguita.bodeguita_backend.entity.Departamento;
import com.bodeguita.bodeguita_backend.entity.Provincia;
import com.bodeguita.bodeguita_backend.repository.DepartamentoRepository;
import com.bodeguita.bodeguita_backend.repository.ProvinciaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;

    public ProvinciaService(ProvinciaRepository provinciaRepository, DepartamentoRepository departamentoRepository) {
        this.provinciaRepository = provinciaRepository;
        this.departamentoRepository = departamentoRepository;
    }

    public List<ProvinciaDto> listar() {
        return provinciaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public ProvinciaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public ProvinciaDto crear(ProvinciaRequest request) {
        Provincia entidad = new Provincia();
        Departamento departamento = departamentoRepository.findByIdAndEstado(request.idDepartamento(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Departamento no encontrado: " + request.idDepartamento() + "."));
        entidad.setDepartamento(departamento);
        entidad.setNombre(request.nombre());
        return aDto(provinciaRepository.save(entidad));
    }

    @Transactional
    public ProvinciaDto actualizar(Long id, ProvinciaRequest request) {
        Provincia existente = buscarActivo(id);
        Departamento departamento = departamentoRepository.findByIdAndEstado(request.idDepartamento(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Departamento no encontrado: " + request.idDepartamento() + "."));
        existente.setDepartamento(departamento);
        existente.setNombre(request.nombre());
        return aDto(provinciaRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Provincia buscarActivo(Long id) {
        return provinciaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provincia no encontrada: " + id + "."));
    }

    private ProvinciaDto aDto(Provincia entidad) {
        return new ProvinciaDto(entidad.getId(),
                entidad.getDepartamento() != null ? entidad.getDepartamento().getId() : null,
                entidad.getNombre(), entidad.getEstado());
    }
}
