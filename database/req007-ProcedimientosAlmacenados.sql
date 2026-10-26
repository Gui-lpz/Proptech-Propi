/* =====================================================================
   PROYECTO PROPI - IF0010 BASES DE DATOS II
   REQ 007: Reportes mediante procedimientos almacenados
   Motor: SQL Server

   SUPUESTOS (ajustar a su modelo final):
     - Base de datos:  PropiFacturacion
     - Tablas:         dbo.Cliente, dbo.Factura, dbo.Bitacora_Auditoria
     - Cliente: num_identificacion, correo y telefono cifrados (REQ 006)
       con la llave simetrica sk_Propi y el certificado cert_Propi,
       cifradas sin authenticator (EncryptByKey(Key_GUID('sk_Propi'), valor))
     - Bitacora_Auditoria.des_datos:
         INSERT/DELETE -> JSON con la fila afectada
         UPDATE        -> JSON {"antes":{...},"despues":{...}}
     - Roles (REQ 004/008): rol_admin, rol_digitador, rol_consulta
   ===================================================================== */

/* ---------------------------------------------------------------------
   PASO 0. Nivel de aislamiento (ejecutar una sola vez)
   Se habilita versionado de filas para que los reportes lean una
   foto consistente sin bloquear a los digitadores (ver justificacion).
   --------------------------------------------------------------------- */
USE PropiFacturacion;
GO
ALTER DATABASE CURRENT SET ALLOW_SNAPSHOT_ISOLATION ON;
ALTER DATABASE CURRENT SET READ_COMMITTED_SNAPSHOT ON WITH ROLLBACK IMMEDIATE;
GO

/* =====================================================================
   1. REPORTE DE CLIENTES RESUMIDO
   ---------------------------------------------------------------------
   Proposito : Lista clientes registrados en un rango de fechas,
               filtrando por estado, con datos sensibles descifrados.
   Parametros: @fec_desde  DATE     Fecha de registro inicial (inclusive)
               @fec_hasta  DATE     Fecha de registro final (inclusive)
               @ind_estado CHAR(1)  'A' activo, 'I' inactivo, 'T' todos
   Salida    : nombre, num_identificacion, actividad_economica,
               correo, telefono
   Aislamiento: SNAPSHOT (lectura consistente, sin bloqueos)
   Seguridad : WITH EXECUTE AS OWNER para que el rol de consulta no
               necesite permisos directos sobre la llave y el certificado.
   Errores   : 50001 fechas nulas | 50002 rango invalido | 50003 estado
               invalido. Cualquier error hace ROLLBACK y se relanza.
   Soporte   : si falla al descifrar, verificar que exista sk_Propi,
               cert_Propi y que dbo tenga permiso sobre ellos.
   ===================================================================== */
CREATE OR ALTER PROCEDURE dbo.sp_rpt_clientes_resumido
    @fec_desde  DATE,
    @fec_hasta  DATE,
    @ind_estado CHAR(1) = 'T'
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL SNAPSHOT;

    BEGIN TRY
        IF @fec_desde IS NULL OR @fec_hasta IS NULL
            THROW 50001, N'Las fechas desde y hasta son obligatorias.', 1;
        IF @fec_desde > @fec_hasta
            THROW 50002, N'La fecha desde no puede ser mayor que la fecha hasta.', 1;
        IF @ind_estado IS NULL OR @ind_estado NOT IN ('A', 'I', 'T')
            THROW 50003, N'Estado invalido. Use A (activo), I (inactivo) o T (todos).', 1;

        BEGIN TRANSACTION;

        OPEN SYMMETRIC KEY sk_Propi DECRYPTION BY CERTIFICATE cert_Propi;

        SELECT  c.nombre,
                CONVERT(VARCHAR(30),  DECRYPTBYKEY(c.num_identificacion)) AS num_identificacion,
                c.actividad_economica,
                CONVERT(VARCHAR(100), DECRYPTBYKEY(c.correo))             AS correo,
                CONVERT(VARCHAR(20),  DECRYPTBYKEY(c.telefono))           AS telefono
        FROM    dbo.Cliente AS c
        WHERE   c.fec_registro >= @fec_desde
          AND   c.fec_registro <  DATEADD(DAY, 1, @fec_hasta)   -- rango SARGable
          AND   (@ind_estado = 'T' OR c.ind_estado = @ind_estado)
        ORDER BY c.nombre
        OPTION (RECOMPILE);   -- plan propio para cada combinacion de parametros

        CLOSE SYMMETRIC KEY sk_Propi;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        IF EXISTS (SELECT 1 FROM sys.openkeys WHERE key_name = 'sk_Propi')
            CLOSE SYMMETRIC KEY sk_Propi;
        THROW;
    END CATCH
END;
GO

/* =====================================================================
   2. REPORTE DE FACTURAS
   ---------------------------------------------------------------------
   Proposito : Facturas emitidas en un rango de fechas con los datos
               del cliente.
   Parametros: @fec_desde DATE, @fec_hasta DATE (ambas inclusive)
   Salida    : nombre cliente, num_identificacion, actividad_economica,
               numero de factura, fecha, subtotal, total_impuestos,
               total_factura
   Aislamiento: SNAPSHOT
   Errores   : 50001 fechas nulas | 50002 rango invalido
   ===================================================================== */
CREATE OR ALTER PROCEDURE dbo.sp_rpt_facturas
    @fec_desde DATE,
    @fec_hasta DATE
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL SNAPSHOT;

    BEGIN TRY
        IF @fec_desde IS NULL OR @fec_hasta IS NULL
            THROW 50001, N'Las fechas desde y hasta son obligatorias.', 1;
        IF @fec_desde > @fec_hasta
            THROW 50002, N'La fecha desde no puede ser mayor que la fecha hasta.', 1;

        BEGIN TRANSACTION;

        OPEN SYMMETRIC KEY sk_Propi DECRYPTION BY CERTIFICATE cert_Propi;

        SELECT  c.nombre                                                  AS nombre_cliente,
                CONVERT(VARCHAR(30), DECRYPTBYKEY(c.num_identificacion))  AS num_identificacion,
                c.actividad_economica,
                f.cod_factura                                             AS num_factura,
                f.fecha,
                f.subtotal,
                f.total_impuestos,
                f.monto_total                                             AS total_factura
        FROM    dbo.Factura AS f
        INNER JOIN dbo.Cliente AS c ON c.cod_cliente = f.cod_cliente
        WHERE   f.fecha >= @fec_desde
          AND   f.fecha <  DATEADD(DAY, 1, @fec_hasta)
        ORDER BY f.fecha, f.cod_factura
        OPTION (RECOMPILE);

        CLOSE SYMMETRIC KEY sk_Propi;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        IF EXISTS (SELECT 1 FROM sys.openkeys WHERE key_name = 'sk_Propi')
            CLOSE SYMMETRIC KEY sk_Propi;
        THROW;
    END CATCH
END;
GO

/* =====================================================================
   3. REPORTE DE PISTAS DE AUDITORIA
   ---------------------------------------------------------------------
   Proposito : Consulta la bitacora de cambios (REQ 005) por rango de
               fechas y usuario.
   Parametros: @fec_desde   DATE          Fecha de registro inicial
               @fec_hasta   DATE          Fecha de registro final
               @cod_usuario NVARCHAR(128) Usuario; NULL = todos
   Salida    : fec_registro, accion, nombre_usuario, tabla_afectada,
               cod_llave, datos_afectados (insert/delete),
               datos_antes y datos_despues (update)
   Aislamiento: SNAPSHOT (la bitacora recibe escrituras constantes)
   Nota      : si des_datos de un UPDATE no es JSON con "antes" y
               "despues", el texto completo se muestra en datos_afectados.
   Errores   : 50001 fechas nulas | 50002 rango invalido
   ===================================================================== */
CREATE OR ALTER PROCEDURE dbo.sp_rpt_pistas_auditoria
    @fec_desde   DATE,
    @fec_hasta   DATE,
    @cod_usuario NVARCHAR(128) = NULL
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL SNAPSHOT;

    BEGIN TRY
        IF @fec_desde IS NULL OR @fec_hasta IS NULL
            THROW 50001, N'Las fechas desde y hasta son obligatorias.', 1;
        IF @fec_desde > @fec_hasta
            THROW 50002, N'La fecha desde no puede ser mayor que la fecha hasta.', 1;

        BEGIN TRANSACTION;

        SELECT  b.fec_registro,
                CASE b.ind_accion WHEN 'I' THEN N'Inserción'
                                  WHEN 'U' THEN N'Modificación'
                                  WHEN 'D' THEN N'Borrado'
                                  ELSE b.ind_accion END AS accion,
                RTRIM(b.cod_usuario)                    AS nombre_usuario,
                b.cod_tabla                             AS tabla_afectada,
                b.cod_llave,
                CASE WHEN b.ind_accion = 'U' AND ISJSON(b.des_datos) = 1
                     THEN NULL ELSE b.des_datos END     AS datos_afectados,
                CASE WHEN b.ind_accion = 'U' AND ISJSON(b.des_datos) = 1
                     THEN JSON_QUERY(b.des_datos, '$.antes') END    AS datos_antes,
                CASE WHEN b.ind_accion = 'U' AND ISJSON(b.des_datos) = 1
                     THEN JSON_QUERY(b.des_datos, '$.despues') END  AS datos_despues
        FROM    dbo.Bitacora_Auditoria AS b
        WHERE   b.fec_registro >= @fec_desde
          AND   b.fec_registro <  DATEADD(DAY, 1, @fec_hasta)
          AND   (@cod_usuario IS NULL OR b.cod_usuario = @cod_usuario)  -- '=' ignora espacios finales del CHAR
        ORDER BY b.fec_registro DESC
        OPTION (RECOMPILE);

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* =====================================================================
   4. REPORTE DE CONFIGURACION DE SEGURIDAD
   ---------------------------------------------------------------------
   Proposito : Muestra los permisos efectivos de usuarios y roles sobre
               tablas, procedimientos, vistas y funciones, ya sea
               directos o heredados por rol. Incluye permisos otorgados
               sobre un esquema completo.
   Parametros: @usuario SYSNAME  Usuario; NULL = todos
   Salida    : usuario, rol ('(directo)' si no viene de un rol), objeto,
               tipo_objeto, tipo_permiso, estado_permiso (GRANT o DENY)
   Aislamiento: READ COMMITTED (los catalogos del sistema no se versionan)
   Limitacion: los roles fijos (db_owner, db_datareader, etc.) no tienen
               filas en sys.database_permissions; se muestran como rol
               pero sin objetos.
   ===================================================================== */
CREATE OR ALTER PROCEDURE dbo.sp_rpt_configuracion_seguridad
    @usuario SYSNAME = NULL
WITH EXECUTE AS OWNER
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

    BEGIN TRY
        BEGIN TRANSACTION;

        ;WITH usr AS (
            SELECT principal_id, name
            FROM   sys.database_principals
            WHERE  type IN ('S', 'U', 'G')
              AND  name NOT IN ('dbo', 'guest', 'INFORMATION_SCHEMA', 'sys')
              AND  (@usuario IS NULL OR name = @usuario)
        ),
        perm AS (
            -- Permisos otorgados directamente sobre el objeto
            SELECT p.grantee_principal_id, p.permission_name, p.state_desc,
                   o.name AS objeto, o.type AS tipo_o
            FROM   sys.database_permissions AS p
            INNER JOIN sys.objects AS o ON p.class = 1 AND o.object_id = p.major_id
            WHERE  p.permission_name IN ('SELECT', 'INSERT', 'DELETE', 'UPDATE', 'EXECUTE')
              AND  o.is_ms_shipped = 0
              AND  o.type IN ('U', 'P', 'V', 'FN', 'IF', 'TF')
            UNION ALL
            -- Permisos otorgados sobre el esquema, expandidos a sus objetos
            SELECT p.grantee_principal_id, p.permission_name, p.state_desc,
                   o.name, o.type
            FROM   sys.database_permissions AS p
            INNER JOIN sys.objects AS o ON p.class = 3 AND o.schema_id = p.major_id
            WHERE  p.permission_name IN ('SELECT', 'INSERT', 'DELETE', 'UPDATE', 'EXECUTE')
              AND  o.is_ms_shipped = 0
              AND  o.type IN ('U', 'P', 'V', 'FN', 'IF', 'TF')
        )
        SELECT  u.name                          AS usuario,
                ISNULL(r.name, N'(directo)')    AS rol,
                pm.objeto                       AS nombre_objeto,
                CASE pm.tipo_o WHEN 'U'  THEN N'Tabla'
                               WHEN 'P'  THEN N'Procedimiento'
                               WHEN 'V'  THEN N'Vista'
                               ELSE N'Función' END AS tipo_objeto,
                CASE pm.permission_name WHEN 'SELECT'  THEN N'Selección'
                                        WHEN 'INSERT'  THEN N'Inserción'
                                        WHEN 'DELETE'  THEN N'Borrado'
                                        WHEN 'UPDATE'  THEN N'Modificación'
                                        ELSE N'Execute' END AS tipo_permiso,
                pm.state_desc                   AS estado_permiso
        FROM    usr AS u
        CROSS APPLY (
            SELECT u.principal_id AS gid, CAST(NULL AS INT) AS rol_id
            UNION ALL
            SELECT rm.role_principal_id, rm.role_principal_id
            FROM   sys.database_role_members AS rm
            WHERE  rm.member_principal_id = u.principal_id
        ) AS g
        INNER JOIN perm AS pm ON pm.grantee_principal_id = g.gid
        LEFT  JOIN sys.database_principals AS r ON r.principal_id = g.rol_id
        ORDER BY u.name, rol, pm.objeto
        OPTION (RECOMPILE);

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* =====================================================================
   PERMISOS (se completan en el REQ 008)
   ===================================================================== */
GRANT EXECUTE ON dbo.sp_rpt_clientes_resumido      TO rol_consulta;
GRANT EXECUTE ON dbo.sp_rpt_facturas               TO rol_consulta;
GRANT EXECUTE ON dbo.sp_rpt_pistas_auditoria       TO rol_consulta;
GRANT EXECUTE ON dbo.sp_rpt_configuracion_seguridad TO rol_consulta;
GO

/* =====================================================================
   PRUEBAS
   ===================================================================== */
EXEC dbo.sp_rpt_clientes_resumido      @fec_desde = '2026-01-01', @fec_hasta = '2026-12-31', @ind_estado = 'T';
EXEC dbo.sp_rpt_clientes_resumido      @fec_desde = '2026-01-01', @fec_hasta = '2026-12-31', @ind_estado = 'A';
EXEC dbo.sp_rpt_facturas               @fec_desde = '2026-01-01', @fec_hasta = '2026-12-31';
EXEC dbo.sp_rpt_pistas_auditoria       @fec_desde = '2026-01-01', @fec_hasta = '2026-12-31', @cod_usuario = NULL;
EXEC dbo.sp_rpt_configuracion_seguridad @usuario = NULL;

-- Prueba de error (debe lanzar el error 50002 y no dejar transacciones abiertas)
EXEC dbo.sp_rpt_facturas @fec_desde = '2026-12-31', @fec_hasta = '2026-01-01';
SELECT @@TRANCOUNT AS transacciones_abiertas;   -- debe ser 0

-- Prueba como usuario de consulta (ajustar el nombre del usuario)
EXECUTE AS USER = 'usr_consulta';
    EXEC dbo.sp_rpt_clientes_resumido '2026-01-01', '2026-12-31', 'T';
REVERT;