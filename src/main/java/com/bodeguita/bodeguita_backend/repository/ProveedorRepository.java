package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    @Query("""
            SELECT proveedor FROM Proveedor proveedor
            LEFT JOIN FETCH proveedor.persona persona
            LEFT JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH proveedor.empresa
            WHERE proveedor.estado = TRUE
            ORDER BY COALESQUE(proveedor.empresa.razonSocial, persona.nombre) ASC
            """)
    List<Proveedor> findAllActivosConRelaciones();

    @Query("""
            SELECT proveedor FROM Proveedor proveedor
            LEFT JOIN FETCH proveedor.persona persona
            LEFT JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH proveedor.empresa
            ORDER BY COALESCE(proveedor.empresa.razonSocial, persona.nombre) ASC
            """)
    List<Proveedor> findAllConRelaciones();

    @Query("""
            SELECT proveedor FROM Proveedor proveedor
            LEFT JOIN FETCH proveedor.persona persona
            LEFT JOIN FETCH persona.tipoIdentidad
            LEFT JOIN FETCH persona.distrito
            LEFT JOIN FETCH proveedor.empresa
            WHERE proveedor.idProveedor = :idProveedor AND proveedor.estado = TRUE
            """)
    Optional<Proveedor> findActivoConRelaciones(@Param("idProveedor") Long idProveedor);
}
