package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.MovimientoInventario;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    Optional<MovimientoInventario> findByIdAndEstado(Long id, String estado);
}


