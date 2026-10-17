package com.bodeguita.bodeguita_backend.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.bodeguita.bodeguita_backend.entity.Modulo;
public interface ModuloRepository extends JpaRepository<Modulo, Long> {
 @Query("SELECT modulo FROM Modulo modulo WHERE modulo.estado = TRUE ORDER BY modulo.orden ASC, modulo.nModulo ASC")
 List<Modulo> findAllActivosOrdenados();
}
