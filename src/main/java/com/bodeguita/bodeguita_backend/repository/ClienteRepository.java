package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    @Query("""
            SELECT cliente FROM Cliente cliente
            JOIN FETCH cliente.persona persona
            JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH cliente.empresa
            WHERE cliente.estado = TRUE
            ORDER BY COALESCE(persona.nombre, cliente.empresa.razonSocial) ASC
            """)
    List<Cliente> findAllActivosConRelaciones();

    @Query("""
            SELECT cliente FROM Cliente cliente
            JOIN FETCH cliente.persona persona
            JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH cliente.empresa
            ORDER BY COALESCE(persona.nombre, cliente.empresa.razonSocial) ASC
            """)
    List<Cliente> findAllConRelaciones();

    @Query("""
            SELECT cliente FROM Cliente cliente
            JOIN FETCH cliente.persona persona
            JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH cliente.empresa
            WHERE cliente.idCliente = :idCliente AND cliente.estado = TRUE
            """)
    Optional<Cliente> findActivoConRelaciones(@Param("idCliente") Long idCliente);
}
