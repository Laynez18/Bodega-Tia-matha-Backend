package com.bodeguita.bodeguita_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.DistritoDto;
import com.bodeguita.bodeguita_backend.dto.DistritoRequest;
import com.bodeguita.bodeguita_backend.entity.Distrito;
import com.bodeguita.bodeguita_backend.entity.Provincia;
import com.bodeguita.bodeguita_backend.repository.DistritoRepository;
import com.bodeguita.bodeguita_backend.repository.ProvinciaRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class DistritoService {

    private final DistritoRepository distritoRepository;
    private final ProvinciaRepository provinciaRepository;

    public DistritoService(DistritoRepository distritoRepository, ProvinciaRepository provinciaRepository) {
        this.distritoRepository = distritoRepository;
        this.provinciaRepository = provinciaRepository;
    }

    public List<DistritoDto> listar() {
        return distritoRepository.findAll().stream().map(this::aDto).collect(Collectors.toList());
    }

    public DistritoDto obtener(Long id) {
        return aDto(buscarActivo(id));
    }

    @Transactional
    public DistritoDto crear(DistritoRequest request) {
        Distrito entidad = new Distrito();
        Provincia provincia = provinciaRepository.findByIdAndEstado(request.idProvincia(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provincia no encontrada: " + request.idProvincia() + "."));
        entidad.setProvincia(provincia);
        entidad.setdDistrito(request.dDistrito());
        return aDto(distritoRepository.save(entidad));
    }

    @Transactional
    public DistritoDto actualizar(Long id, DistritoRequest request) {
        Distrito existente = buscarActivo(id);
        Provincia provincia = provinciaRepository.findByIdAndEstado(request.idProvincia(), "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Provincia no encontrada: " + request.idProvincia() + "."));
        existente.setProvincia(provincia);
        existente.setdDistrito(request.dDistrito());
        return aDto(distritoRepository.save(existente));
    }

    @Transactional
    public void desactivar(Long id) {
        buscarActivo(id).setEstado("0");
    }

    private Distrito buscarActivo(Long id) {
        return distritoRepository.findByIdAndEstado(id, "1")
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Distrito no encontrado: " + id + "."));
    }

    private DistritoDto aDto(Distrito entidad) {
        return new DistritoDto(
                entidad.getIdDistrito(),
                entidad.getProvincia() != null ? entidad.getProvincia().getId() : null,
                entidad.getdDistrito(),
                entidad.getEstado()
        );
    }
}
