package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("SELECT p FROM Producto p WHERE p.id = :id AND p.estado = :estado")
    Optional<Producto> findByIdAndEstado(Long id, String estado);
    @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE UPPER(p.codigoBarras) = UPPER(:codigo)")
    boolean existsByCodigoIgnoreCase(String codigo);
    @Query("SELECT COUNT(p) > 0 FROM Producto p WHERE UPPER(p.codigoBarras) = UPPER(:codigo) AND p.id <> :id")
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}


