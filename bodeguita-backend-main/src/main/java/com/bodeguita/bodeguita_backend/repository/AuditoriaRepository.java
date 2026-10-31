package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.bodeguita.bodeguita_backend.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findByEstadoOrderByFEventoDesc(String estado);
    List<Auditoria> findByEstadoAndUsuario_IdUsuarioOrderByFEventoDesc(String estado, Long idUsuario);
}
