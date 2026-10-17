package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("""
            SELECT empleado FROM Empleado empleado
            JOIN FETCH empleado.persona persona
            JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH empleado.contrato
            LEFT JOIN FETCH empleado.cargo
            WHERE empleado.estado = TRUE
            ORDER BY persona.nombre ASC, persona.apPaterno ASC, persona.apMaterno ASC
            """)
    List<Empleado> findAllActivosConRelaciones();

    @Query("""
            SELECT empleado FROM Empleado empleado
            JOIN FETCH empleado.persona persona
            JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH empleado.contrato
            LEFT JOIN FETCH empleado.cargo
            WHERE empleado.idEmpleado = :idEmpleado AND empleado.estado = TRUE
            """)
    Optional<Empleado> findActivoConRelaciones(@Param("idEmpleado") Long idEmpleado);
}
