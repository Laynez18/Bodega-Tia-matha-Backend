package com.bodeguita.bodeguita_backend.service;

import java.sql.ResultSet;
import java.sql.SQLException;

public record FilaLogin(
        Long idUsuario,
        String logeo,
        String clave,
        Long idTipoUsuario,
        String nombre,
        String apPaterno,
        String apMaterno
) {

    public static FilaLogin mapRow(ResultSet rs, int numeroFila) throws SQLException {
        return new FilaLogin(
                rs.getLong("id_usuario"),
                rs.getString("logeo"),
                rs.getString("clave"),
                rs.getObject("id_tipo_usuario", Long.class),
                rs.getString("nombre"),
                rs.getString("ap_paterno"),
                rs.getString("ap_materno"));
    }

    public String nombreCompleto() {
        String completo = ((nombre == null ? "" : nombre) + " "
                + (apPaterno == null ? "" : apPaterno) + " "
                + (apMaterno == null ? "" : apMaterno)).trim();
        return completo.isEmpty() ? logeo : completo;
    }
}
