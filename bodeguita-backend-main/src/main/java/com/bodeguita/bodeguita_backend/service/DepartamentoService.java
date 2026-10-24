package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.DepartamentoDto;
import com.bodeguita.bodeguita_backend.dto.DepartamentoRequest;
import com.bodeguita.bodeguita_backend.entity.Departamento;
import com.bodeguita.bodeguita_backend.repository.DepartamentoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    public List<DepartamentoDto> listar() {
        return departamentoRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public DepartamentoDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public DepartamentoDto crear(DepartamentoRequest request) {
        Departamento entidad = new Departamento();
        entidad.setNombre(request.nombre());
        return aDto(departamentoRepository.save(entidad));
    }

    @Transactional
    public DepartamentoDto actualizar(Long id, DepartamentoRequest request) {
        Departamento existente = buscarActivo(id);
        existente.setNombre(request.nombre());
        return aDto(departamentoRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Departamento buscarActivo(Long id) {
        return departamentoRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Departamento no encontrado: " + id + "."));
    }

    private DepartamentoDto aDto(Departamento entidad) {
        return new DepartamentoDto(entidad.getId(), entidad.getNombre(), entidad.getEstado());
    }
}
