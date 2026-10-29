package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByIdAndEstado(Long id, String estado);
    @Query("SELECT COUNT(p) > 0 FROM Proveedor p WHERE p.persona.idPersona = :idPersona AND p.estado = '1'")
    boolean existsByPersona_IdAndEstadoTrue(Long idPersona);
}


