package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.MarcaDto;
import com.bodeguita.bodeguita_backend.entity.Marca;
import com.bodeguita.bodeguita_backend.repository.MarcaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public List<MarcaDto> listar() {
        return marcaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public MarcaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public MarcaDto crear(Marca entidad) {
        return aDto(marcaRepository.save(entidad));
    }

    @Transactional
    public MarcaDto actualizar(Long id, Marca entidad) {
        Marca existente = buscarActivo(id);
        existente.setNombre(entidad.getNombre());
        existente.setDescripcion(entidad.getDescripcion());
        return aDto(marcaRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Marca buscarActivo(Long id) {
        return marcaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Marca no encontrada: " + id + "."));
    }

    private MarcaDto aDto(Marca entidad) {
        return new MarcaDto(entidad.getId(), entidad.getNombre(), entidad.getDescripcion(), entidad.getEstado());
    }
}
