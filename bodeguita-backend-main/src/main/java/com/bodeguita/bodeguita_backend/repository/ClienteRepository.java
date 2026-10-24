package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.bodeguita.bodeguita_backend.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByIdAndEstado(Long id, String estado);
    boolean existsByPersona_IdAndEstadoTrue(Long idPersona);
}


