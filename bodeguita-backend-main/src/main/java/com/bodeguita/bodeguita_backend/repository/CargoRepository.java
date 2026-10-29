package com.bodeguita.bodeguita_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.Cargo;

public interface CargoRepository extends JpaRepository<Cargo, Long> {

    @Query("SELECT cargo FROM Cargo cargo WHERE cargo.estado = '1' ORDER BY cargo.nCargo ASC")
    List<Cargo> findAllActivosOrdenados();

    @Query("SELECT c FROM Cargo c WHERE c.idCargo = :idCargo AND c.estado = '1'")
    java.util.Optional<Cargo> findByIdCargoAndEstadoTrue(Long idCargo);
}
