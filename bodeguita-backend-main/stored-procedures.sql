-- =============================================================================
-- STORED PROCEDURES PARA AUTENTICACIÓN Y PERMISOS
-- Ejecutar en Supabase/PostgreSQL ANTES de que la app intente hacer login
-- =============================================================================

-- -----------------------------------------------------------------------------
-- usp_login: Autentica usuario por logeo y retorna datos para validar password
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION usp_login(p_logeo VARCHAR)
RETURNS TABLE (
    id_usuario BIGINT,
    logeo VARCHAR,
    clave VARCHAR,
    id_tipo_usuario BIGINT,
    nombre VARCHAR,
    ap_paterno VARCHAR,
    ap_materno VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id_usuario,
        u.logeo,
        u.clave,
        u.id_tipo_usuario,
        p.nombre,
        p.ap_paterno,
        p.ap_materno
    FROM usuario u
    INNER JOIN empleado e ON u.id_empleado = e.id_empleado
    INNER JOIN persona p ON e.id_persona = p.id_persona
    WHERE u.logeo = p_logeo
      AND u.estado = '1'
      AND e.estado = '1'
      AND p.estado = '1';
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- -----------------------------------------------------------------------------
-- usp_permisos_usuario: Retorna permisos del usuario agrupados por módulo
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION usp_permisos_usuario(p_id_usuario BIGINT)
RETURNS TABLE (
    n_modulo VARCHAR,
    icono VARCHAR,
    orden INTEGER,
    clave VARCHAR,
    n_permiso VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        m.n_modulo,
        m.icono,
        m.orden,
        pe.clave,
        pe.n_permiso
    FROM usuario_rol ur
    INNER JOIN rol r ON ur.id_rol = r.id_rol
    INNER JOIN rol_permiso rp ON r.id_rol = rp.id_rol
    INNER JOIN permiso pe ON rp.id_permiso = pe.id_permiso
    INNER JOIN modulo m ON pe.id_modulo = m.id_modulo
    WHERE ur.id_usuario = p_id_usuario
      AND ur.vigente = '1'
      AND ur.estado = '1'
      AND r.estado = '1'
      AND rp.estado = '1'
      AND rp.concedido = '1'
      AND pe.estado = '1'
      AND m.estado = '1'
    ORDER BY m.orden, m.n_modulo, pe.n_permiso;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- -----------------------------------------------------------------------------
-- GRANTS (ajustar según el usuario de conexión de la app)
-- -----------------------------------------------------------------------------
-- GRANT EXECUTE ON FUNCTION usp_login(VARCHAR) TO tu_usuario_app;
-- GRANT EXECUTE ON FUNCTION usp_permisos_usuario(BIGINT) TO tu_usuario_app;