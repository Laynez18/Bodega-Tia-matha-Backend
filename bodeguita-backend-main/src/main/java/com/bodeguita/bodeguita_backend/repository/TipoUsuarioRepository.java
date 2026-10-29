package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.TipoUsuario;

public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    @Query("SELECT t FROM TipoUsuario t WHERE t.idTipoUsuario = :id AND t.estado = :estado")
    Optional<TipoUsuario> findByIdAndEstado(Long id, String estado);

    @Query("SELECT tipo FROM TipoUsuario tipo WHERE tipo.estado = '1' ORDER BY tipo.nTipoUsuario ASC")
    List<TipoUsuario> findAllActivosOrdenados();

    @Query("SELECT t FROM TipoUsuario t WHERE t.idTipoUsuario = :idTipoUsuario AND t.estado = '1'")
    Optional<TipoUsuario> findByIdTipoUsuarioAndEstadoTrue(Long idTipoUsuario);
}

