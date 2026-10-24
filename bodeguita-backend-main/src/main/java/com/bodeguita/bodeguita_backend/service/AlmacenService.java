package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.AlmacenDto;
import com.bodeguita.bodeguita_backend.dto.AlmacenRequest;
import com.bodeguita.bodeguita_backend.entity.Almacen;
import com.bodeguita.bodeguita_backend.repository.AlmacenRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class AlmacenService {

    private final AlmacenRepository almacenRepository;

    public AlmacenService(AlmacenRepository almacenRepository) {
        this.almacenRepository = almacenRepository;
    }

    public List<AlmacenDto> listar() {
        return almacenRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public AlmacenDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public AlmacenDto crear(AlmacenRequest request) {
        Almacen entidad = new Almacen();
        entidad.setNombre(request.nombre());
        entidad.setDescripcion(request.descripcion());
        entidad.setUbicacion(request.ubicacion());
        entidad.setEsPrincipal(request.esPrincipal());
        return aDto(almacenRepository.save(entidad));
    }

    @Transactional
    public AlmacenDto actualizar(Long id, AlmacenRequest request) {
        Almacen existente = buscarActivo(id);
        existente.setNombre(request.nombre());
        existente.setDescripcion(request.descripcion());
        existente.setUbicacion(request.ubicacion());
        existente.setEsPrincipal(request.esPrincipal());
        return aDto(almacenRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Almacen buscarActivo(Long id) {
        return almacenRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Almacen no encontrado: " + id + "."));
    }

    private AlmacenDto aDto(Almacen entidad) {
        return new AlmacenDto(entidad.getId(), entidad.getNombre(), entidad.getDescripcion(),
                entidad.getUbicacion(), entidad.getEsPrincipal(), entidad.getEstado());
    }
}
