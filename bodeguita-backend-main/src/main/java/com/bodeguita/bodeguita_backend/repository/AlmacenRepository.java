package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {
    Optional<Almacen> findByIdAndEstado(Long id, String estado);
}


