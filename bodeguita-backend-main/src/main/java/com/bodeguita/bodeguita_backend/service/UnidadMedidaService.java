package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.UnidadMedidaDto;
import com.bodeguita.bodeguita_backend.entity.UnidadMedida;
import com.bodeguita.bodeguita_backend.repository.UnidadMedidaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;

    public UnidadMedidaService(UnidadMedidaRepository unidadMedidaRepository) {
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    public List<UnidadMedidaDto> listar() {
        return unidadMedidaRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public UnidadMedidaDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public UnidadMedidaDto crear(UnidadMedida entidad) {
        return aDto(unidadMedidaRepository.save(entidad));
    }

    @Transactional
    public UnidadMedidaDto actualizar(Long id, UnidadMedida entidad) {
        UnidadMedida existente = buscarActivo(id);
        existente.setNombre(entidad.getNombre());
        existente.setAbreviatura(entidad.getAbreviatura());
        return aDto(unidadMedidaRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private UnidadMedida buscarActivo(Long id) {
        return unidadMedidaRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Unidad de medida no encontrada: " + id + "."));
    }

    private UnidadMedidaDto aDto(UnidadMedida entidad) {
        return new UnidadMedidaDto(entidad.getId(), entidad.getNombre(), entidad.getAbreviatura(), entidad.getEstado());
    }
}
