package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Provincia;

public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
    Optional<Provincia> findByIdAndEstado(Long id, String estado);
}


