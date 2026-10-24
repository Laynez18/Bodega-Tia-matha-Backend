package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByIdAndEstado(Long id, String estado);
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}


