package com.bodeguita.bodeguita_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodeguita.bodeguita_backend.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    @Query("SELECT rol FROM Rol rol WHERE rol.estado = TRUE ORDER BY rol.nRol ASC")
    List<Rol> findAllActivosOrdenados();

    @Query("SELECT rol FROM Rol rol ORDER BY rol.nRol ASC")
    List<Rol> findAllOrdenados();

    @Query("SELECT rol FROM Rol rol WHERE rol.idRol = :idRol AND rol.estado = TRUE")
    Optional<Rol> findActivoPorId(Long idRol);

    @Query("SELECT COUNT(rol) > 0 FROM Rol rol WHERE rol.nRol = :nRol")
    boolean existeNombre(String nRol);

    @Query("SELECT COUNT(rol) > 0 FROM Rol rol WHERE rol.nRol = :nRol AND rol.idRol <> :idRol")
    boolean existeNombreEnOtroRol(String nRol, Long idRol);
}
