package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CategoriaDto;
import com.bodeguita.bodeguita_backend.dto.CategoriaRequest;
import com.bodeguita.bodeguita_backend.entity.Categoria;
import com.bodeguita.bodeguita_backend.repository.CategoriaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDto> listar() {
        return categoriaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public CategoriaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public CategoriaDto crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "La categoria ya esta registrada.");
        }
        Categoria entidad = new Categoria();
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad = categoriaRepository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public CategoriaDto actualizar(Long id, CategoriaRequest request) {
        Categoria entidad = buscarActivo(id);
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(CONFLICT, "La categoria ya esta registrada.");
        }
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado(Boolean.FALSE);
    }

    private Categoria buscarActivo(Long id) {
        return categoriaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Categoria no encontrado: " + id + "."));
    }

    private CategoriaDto aDto(Categoria entidad) {
        return new CategoriaDto(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getDescripcion()
        );
    }
}
