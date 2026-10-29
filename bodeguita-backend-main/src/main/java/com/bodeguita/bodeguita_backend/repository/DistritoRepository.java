package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Distrito;

public interface DistritoRepository extends JpaRepository<Distrito, Long> {
    @Query("SELECT d FROM Distrito d WHERE d.idDistrito = :id AND d.estado = :estado")
    Optional<Distrito> findByIdAndEstado(Long id, String estado);
}


