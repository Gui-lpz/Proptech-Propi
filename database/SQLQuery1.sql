USE PropiFacturacion_CG;
GO


IF OBJECT_ID('dbo.UsuarioPrueba', 'U') IS NOT NULL
    DROP TABLE dbo.UsuarioPrueba;
GO


CREATE TABLE dbo.UsuarioPrueba
(
    UsuarioID INT IDENTITY(1,1)
        PRIMARY KEY,

    Username VARCHAR(50)
        NOT NULL
        UNIQUE,

    PasswordHash VARCHAR(100)
        NOT NULL,

    Rol VARCHAR(30)
        NOT NULL,

    Activo BIT
        NOT NULL
        DEFAULT 1
);
GO


/* USUARIOS PARA PROBAR LOS ROLES
 */

INSERT INTO dbo.UsuarioPrueba
(
    Username,
    PasswordHash,
    Rol,
    Activo
)
VALUES
(
    'propi_admin',
    'Admin123*',
    'ROLE_ADMINISTRADOR',
    1
),
(
    'propi_digitador',
    'Digitador123*',
    'ROLE_DIGITADOR',
    1
),
(
    'propi_consulta',
    'Consulta123*',
    'ROLE_CONSULTA',
    1
);
GO


/*TABLA BITACORA DE ACCESO
   */

IF OBJECT_ID('dbo.BitacoraAcceso', 'U') IS NOT NULL
    DROP TABLE dbo.BitacoraAcceso;
GO


CREATE TABLE dbo.BitacoraAcceso
(
    BitacoraAccesoID INT IDENTITY(1,1)
        PRIMARY KEY,

    NombreUsuario VARCHAR(50)
        NOT NULL,

    NombreTerminal VARCHAR(500)
        NULL,

    DireccionIP VARCHAR(50)
        NULL,

    FechaHoraIngreso DATETIME2
        NOT NULL
        DEFAULT SYSDATETIME(),

    FechaHoraSalida DATETIME2
        NULL,

    FechaRegistro DATETIME2
        NOT NULL
        DEFAULT SYSDATETIME()
);
GO


/* prueba*/

SELECT
    UsuarioID,
    Username,
    Rol,
    Activo
FROM dbo.UsuarioPrueba;
GO