package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.MetodoPagoDto;
import com.bodeguita.bodeguita_backend.dto.MetodoPagoRequest;
import com.bodeguita.bodeguita_backend.entity.MetodoPago;
import com.bodeguita.bodeguita_backend.repository.MetodoPagoRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class MetodoPagoService {

    private final MetodoPagoRepository repository;

    public MetodoPagoService(MetodoPagoRepository repository) {
        this.repository = repository;
    }

    public List<MetodoPagoDto> listar() {
        return repository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public MetodoPagoDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public MetodoPagoDto crear(MetodoPagoRequest request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new ResponseStatusException(CONFLICT, "El metodo de pago ya esta registrado.");
        }
        MetodoPago entidad = new MetodoPago();
        entidad.setNombre(request.nombre());
        entidad.setEstado(request.estado());
        entidad = repository.saveAndFlush(entidad);
        return obtener(entidad.getId());
    }

    @Transactional
    public MetodoPagoDto actualizar(Long id, MetodoPagoRequest request) {
        MetodoPago entidad = buscarActivo(id);
        if (repository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new ResponseStatusException(CONFLICT, "El metodo de pago ya esta registrado.");
        }
        entidad.setNombre(request.nombre());
        entidad.setEstado(request.estado());
        return obtener(entidad.getId());
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado(Boolean.FALSE);
    }

    private MetodoPago buscarActivo(Long id) {
        return repository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Metodo de pago no encontrado: " + id + "."));
    }

    private MetodoPagoDto aDto(MetodoPago entidad) {
        return new MetodoPagoDto(
                entidad.getId(),
                entidad.getNombre(),
                entidad.getEstado()
        );
    }
}
