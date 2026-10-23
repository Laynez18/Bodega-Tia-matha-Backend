package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("""
            SELECT DISTINCT usuario FROM Usuario usuario
            JOIN FETCH usuario.tipoUsuario
            LEFT JOIN FETCH usuario.empleado empleado
            LEFT JOIN FETCH empleado.persona
            LEFT JOIN FETCH usuario.roles usuarioRol
            LEFT JOIN FETCH usuarioRol.rol
            ORDER BY usuario.id ASC
            """)
    List<Usuario> findAllConRelaciones();

    @Query("""
            SELECT usuario FROM Usuario usuario
            LEFT JOIN FETCH usuario.empleado empleado
            LEFT JOIN FETCH empleado.persona
            ORDER BY usuario.logeo
            """)
    List<Usuario> findAllParaFiltro();

    @Query("""
            SELECT DISTINCT usuario FROM Usuario usuario
            JOIN FETCH usuario.tipoUsuario
            LEFT JOIN FETCH usuario.empleado empleado
            LEFT JOIN FETCH empleado.persona
            LEFT JOIN FETCH usuario.roles usuarioRol
            LEFT JOIN FETCH usuarioRol.rol
            WHERE usuario.id = :idUsuario
            """)
    Optional<Usuario> findConRelaciones(@Param("idUsuario") Long idUsuario);

    @Query("""
            SELECT DISTINCT usuario FROM Usuario usuario
            JOIN FETCH usuario.tipoUsuario
            LEFT JOIN FETCH usuario.empleado empleado
            LEFT JOIN FETCH empleado.persona
            LEFT JOIN FETCH usuario.roles usuarioRol
            LEFT JOIN FETCH usuarioRol.rol
            WHERE usuario.id = :idUsuario AND usuario.estado = TRUE
            """)
    Optional<Usuario> findActivoConRelaciones(@Param("idUsuario") Long idUsuario);

    boolean existsByLogeo(String logeo);

    boolean existsByLogeoAndIdNot(String logeo, Long id);

    boolean existsByEmpleado_IdEmpleadoAndEstadoTrue(Long idEmpleado);

    boolean existsByEmpleado_IdEmpleadoAndEstadoTrueAndIdNot(Long idEmpleado, Long id);
}
