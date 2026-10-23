package com.bodeguita.bodeguita_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodeguita.bodeguita_backend.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @Query("SELECT COUNT(p) > 0 FROM Persona p WHERE p.tipoIdentidad.idTipoIdentidad = :idTipoIdentidad AND p.nDocumento = :nDocumento")
    boolean existsByTipoIdentidadAndDocumento(@Param("idTipoIdentidad") Long idTipoIdentidad, @Param("nDocumento") String nDocumento);

    @Query("SELECT COUNT(p) > 0 FROM Persona p WHERE p.tipoIdentidad.idTipoIdentidad = :idTipoIdentidad AND p.nDocumento = :nDocumento AND p.idPersona <> :idPersona")
    boolean existsByTipoIdentidadAndDocumentoExceptId(@Param("idTipoIdentidad") Long idTipoIdentidad, @Param("nDocumento") String nDocumento, @Param("idPersona") Long idPersona);
}
