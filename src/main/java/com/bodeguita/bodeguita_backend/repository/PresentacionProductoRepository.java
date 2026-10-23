package com.bodeguita.bodeguita_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodeguita.bodeguita_backend.entity.PresentacionProducto;

public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {

    Optional<PresentacionProducto> findFirstByProducto_IdProductoAndEsUnidadBaseTrueAndEstadoTrue(Long idProducto);

    Optional<PresentacionProducto> findFirstByProducto_IdProductoAndUnidadMedida_IdUnidadMedida(
            Long idProducto, Long idUnidadMedida);

    boolean existsByProducto_IdProductoAndUnidadMedida_IdUnidadMedidaAndIdPresentacionProductoNot(
            Long idProducto, Long idUnidadMedida, Long idPresentacionProducto);
}
