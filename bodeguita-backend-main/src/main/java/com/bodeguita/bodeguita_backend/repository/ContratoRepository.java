package com.bodeguita.bodeguita_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.Contrato;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    @Query("SELECT contrato FROM Contrato contrato WHERE contrato.estado = TRUE ORDER BY contrato.nContrato ASC")
    List<Contrato> findAllActivosOrdenados();

    java.util.Optional<Contrato> findByIdContratoAndEstadoTrue(Long idContrato);
}
