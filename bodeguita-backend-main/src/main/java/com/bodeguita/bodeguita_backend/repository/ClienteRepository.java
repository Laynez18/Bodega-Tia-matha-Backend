package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByIdAndEstado(Long id, String estado);
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.persona.idPersona = :idPersona AND c.estado = '1'")
    boolean existsByPersona_IdAndEstadoTrue(Long idPersona);
}


