package com.bodeguita.bodeguita_backend.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bodeguita.bodeguita_backend.entity.RolPermiso;
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {
 @Query("SELECT rp FROM RolPermiso rp WHERE rp.rol.idRol IN :roles AND rp.permiso.idPermiso IN :permisos")
 List<RolPermiso> findAllParaMatriz(@Param("roles") List<Long> roles, @Param("permisos") List<Long> permisos);
 Optional<RolPermiso> findByRol_IdRolAndPermiso_IdPermiso(Long idRol, Long idPermiso);
}
