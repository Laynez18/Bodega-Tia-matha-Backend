package com.bodeguita.bodeguita_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query("SELECT a FROM Auditoria a WHERE a.estado = :estado ORDER BY a.fEvento DESC")
    List<Auditoria> listarActivos(@Param("estado") String estado);

    @Query("SELECT a FROM Auditoria a WHERE a.estado = :estado AND a.usuario.idUsuario = :idUsuario ORDER BY a.fEvento DESC")
    List<Auditoria> listarActivosPorUsuario(@Param("estado") String estado, @Param("idUsuario") Long idUsuario);
}
