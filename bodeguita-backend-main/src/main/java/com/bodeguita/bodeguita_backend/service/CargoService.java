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

    public CargoService(CargoRepository cargoRepository) {
        this.cargoRepository = cargoRepository;
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
        return aDto(cargoRepository.save(cargo));
    }

    @Transactional
    public CargoDto actualizar(Long idCargo, CargoRequest request) {
        Cargo cargo = buscarActivo(idCargo);
        cargo.setNCargo(request.nCargo().trim());
        return aDto(cargo);
    }

    @Transactional
    public void desactivar(Long idCargo) {
        buscarActivo(idCargo).setEstado(Boolean.FALSE);
    }

    private Cargo buscarActivo(Long idCargo) {
        return cargoRepository.findByIdCargoAndEstadoTrue(idCargo)
                .orElseThrow(() -> noEncontrado(idCargo));
    }

    private CargoDto aDto(Cargo cargo) {
        return new CargoDto(cargo.getIdCargo(), cargo.getNCargo());
    }

    private ResponseStatusException noEncontrado(Long idCargo) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Cargo no encontrado: " + idCargo + ".");
    }
}
