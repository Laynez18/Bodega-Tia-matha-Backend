package com.bodeguita.bodeguita_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    long countByRol_IdRolAndVigenteAndEstadoTrue(Long idRol, String vigente);

    @Query("""
            SELECT usuarioRol FROM UsuarioRol usuarioRol
            JOIN FETCH usuarioRol.rol
            WHERE usuarioRol.usuario.id = :idUsuario
            """)
    List<UsuarioRol> findAllPorUsuario(@Param("idUsuario") Long idUsuario);
}
