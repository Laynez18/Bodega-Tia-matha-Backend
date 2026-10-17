package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.TipoUsuario;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    @Query("SELECT tipo FROM TipoUsuario tipo WHERE tipo.estado = TRUE ORDER BY tipo.nTipoUsuario ASC")
    List<TipoUsuario> findAllActivosOrdenados();

    Optional<TipoUsuario> findByIdTipoUsuarioAndEstadoTrue(Long idTipoUsuario);
}
