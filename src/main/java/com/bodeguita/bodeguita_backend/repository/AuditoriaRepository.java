package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import com.bodeguita.bodeguita_backend.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {

    @Override
    @EntityGraph(attributePaths = { "usuario", "usuario.empleado", "usuario.empleado.persona" })
    List<Auditoria> findAll(@Nullable Specification<Auditoria> spec);

    @Query("""
            SELECT DISTINCT auditoria.nTabla FROM Auditoria auditoria
            WHERE auditoria.estado = TRUE
            ORDER BY auditoria.nTabla
            """)
    List<String> findTablas();

    @Query("""
            SELECT DISTINCT auditoria.accion FROM Auditoria auditoria
            WHERE auditoria.estado = TRUE
            ORDER BY auditoria.accion
            """)
    List<String> findAcciones();

    @Query("""
            SELECT auditoria FROM Auditoria auditoria
            LEFT JOIN FETCH auditoria.usuario usuario
            LEFT JOIN FETCH usuario.empleado empleado
            LEFT JOIN FETCH empleado.persona
            WHERE auditoria.idAuditoria = :idAuditoria
            """)
    Optional<Auditoria> findDetalle(@Param("idAuditoria") Long idAuditoria);
}
