package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Marca;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    Optional<Marca> findByIdAndEstado(Long id, String estado);
}


