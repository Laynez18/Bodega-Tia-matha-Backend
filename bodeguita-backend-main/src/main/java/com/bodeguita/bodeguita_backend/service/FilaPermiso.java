package com.bodeguita.bodeguita_backend.service;

import java.sql.ResultSet;
import java.sql.SQLException;

public record FilaPermiso(
        String nModulo,
        String icono,
        Integer orden,
        String clave,
        String nPermiso
) {

    public static FilaPermiso mapRow(ResultSet rs, int numeroFila) throws SQLException {
        return new FilaPermiso(
                rs.getString("n_modulo"),
                rs.getString("icono"),
                rs.getObject("orden", Integer.class),
                rs.getString("clave"),
                rs.getString("n_permiso"));
    }
}
