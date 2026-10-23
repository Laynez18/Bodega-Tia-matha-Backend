package com.bodeguita.bodeguita_backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.bodeguita.bodeguita_backend.dto.CargoDto;
import com.bodeguita.bodeguita_backend.dto.CargoRequest;
import com.bodeguita.bodeguita_backend.entity.Cargo;
import com.bodeguita.bodeguita_backend.repository.CargoRepository;

@Service
@Transactional(readOnly = true)
public class CargoService {

    private final CargoRepository cargoRepository;
    private final AuditoriaService auditoriaService;

    public CargoService(CargoRepository cargoRepository, AuditoriaService auditoriaService) {
        this.cargoRepository = cargoRepository;
        this.auditoriaService = auditoriaService;
    }

    public List<CargoDto> listar() {
        return cargoRepository.findAllActivosOrdenados().stream()
                .map(this::aDto)
                .toList();
    }

    public CargoDto obtener(Long idCargo) {
        return aDto(buscarActivo(idCargo));
    }

    @Transactional
    public CargoDto crear(CargoRequest request) {
        Cargo cargo = new Cargo();
        cargo.setNCargo(request.nCargo().trim());
        cargo.setEstado(Boolean.TRUE);
        Cargo guardado = cargoRepository.save(cargo);
        auditoriaService.registrar("cargo", "INSERT", guardado.getIdCargo(), null, resumen(guardado));
        return aDto(guardado);
    }

    @Transactional
    public CargoDto actualizar(Long idCargo, CargoRequest request) {
        Cargo cargo = buscarActivo(idCargo);
        String anterior = resumen(cargo);
        cargo.setNCargo(request.nCargo().trim());
        auditoriaService.registrar("cargo", "UPDATE", idCargo, anterior, resumen(cargo));
        return aDto(cargo);
    }

    @Transactional
    public void desactivar(Long idCargo) {
        Cargo cargo = buscarActivo(idCargo);
        String anterior = resumen(cargo);
        cargo.setEstado(Boolean.FALSE);
        auditoriaService.registrar("cargo", "DESACTIVAR", idCargo, anterior, resumen(cargo));
    }

    private Cargo buscarActivo(Long idCargo) {
        return cargoRepository.findByIdCargoAndEstadoTrue(idCargo)
                .orElseThrow(() -> noEncontrado(idCargo));
    }

    private CargoDto aDto(Cargo cargo) {
        return new CargoDto(cargo.getIdCargo(), cargo.getNCargo());
    }

    private String resumen(Cargo cargo) {
        return "nCargo=" + cargo.getNCargo()
                + "; estado=" + (Boolean.TRUE.equals(cargo.getEstado()) ? "1" : "0");
    }

    private ResponseStatusException noEncontrado(Long idCargo) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + idCargo + ".");
    }
}
