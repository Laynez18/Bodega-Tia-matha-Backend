package com.bodeguita.bodeguita_backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.bodeguita.bodeguita_backend.entity.Permiso;
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
 @Query("SELECT permiso FROM Permiso permiso JOIN FETCH permiso.modulo WHERE permiso.estado = TRUE ORDER BY permiso.modulo.orden ASC, permiso.nPermiso ASC")
 List<Permiso> findAllActivosConModulo();
}
