
-- REQUERIMIENTO 009: OPTIMIZACIÓN Y PLANES DE EJECUCIÓN

USE PropiTest; -- BASE DE DATOS DE PRUEBA
GO

SET NOCOUNT ON;
GO


-- PARTE 0. PREPARACIÓN DE LA BASE Y DATOS

-- Limpiamos por si ya existían
DROP TABLE IF EXISTS dbo.FacturaDetalle;
DROP TABLE IF EXISTS dbo.Factura;
DROP TABLE IF EXISTS dbo.Cliente;
DROP TABLE IF EXISTS dbo.BitacoraAuditoria;
DROP TABLE IF EXISTS dbo.Metricas_REQ009;
GO

-- Creamos las tablas base (Simplificadas)
CREATE TABLE dbo.Cliente (
    ClienteID INT IDENTITY PRIMARY KEY, -- PK Clustered automático
    Nombre VARCHAR(100),
    Estado CHAR(1),
    FechaRegistro DATE
);

CREATE TABLE dbo.Factura (
    FacturaID INT IDENTITY PRIMARY KEY, -- PK Clustered automático
    ClienteID INT,
    Fecha DATE,
    MontoTotal DECIMAL(18,2)
);

CREATE TABLE dbo.BitacoraAuditoria (
    LogID INT IDENTITY PRIMARY KEY, -- PK Clustered automático
    FechaEvento DATETIME,
    Usuario VARCHAR(50),
    Accion VARCHAR(10)
);
GO

-- Generamos 100,000 registros por tabla usando tabla temporal
IF OBJECT_ID('tempdb..#Numeros') IS NOT NULL DROP TABLE #Numeros;
CREATE TABLE #Numeros (n INT PRIMARY KEY);
INSERT INTO #Numeros (n)
SELECT TOP 100000 ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) 
FROM sys.all_objects a CROSS JOIN sys.all_objects b;

INSERT INTO dbo.Cliente (Nombre, Estado, FechaRegistro)
SELECT 'Cliente ' + CAST(n AS VARCHAR), CASE WHEN n % 10 = 0 THEN 'I' ELSE 'A' END, DATEADD(DAY, n, '2023-01-01') 
FROM #Numeros;

INSERT INTO dbo.Factura (ClienteID, Fecha, MontoTotal)
SELECT (n % 100000) + 1, DATEADD(DAY, n % 365, '2023-01-01'), (n * 10.50) 
FROM #Numeros;

INSERT INTO dbo.BitacoraAuditoria (FechaEvento, Usuario, Accion)
SELECT DATEADD(MINUTE, n, '2024-01-01'), 'Admin', 'INSERT' 
FROM #Numeros;

DROP TABLE #Numeros;

-- Actualizamos estadísticas (para el optimizador)
UPDATE STATISTICS dbo.Cliente;
UPDATE STATISTICS dbo.Factura;
UPDATE STATISTICS dbo.BitacoraAuditoria;
GO

-- =====================================================================
-- PARTE 1. CREACIÓN DE PROCEDIMIENTOS ALMACENADOS
-- =====================================================================
CREATE OR ALTER PROCEDURE dbo.sp_ReporteClientes @Estado CHAR(1) AS 
BEGIN 
    SELECT ClienteID, Nombre, FechaRegistro FROM dbo.Cliente WHERE Estado = @Estado; 
END
GO
CREATE OR ALTER PROCEDURE dbo.sp_ReporteFacturas @FechaInicio DATE, @FechaFin DATE AS 
BEGIN 
    SELECT f.FacturaID, f.Fecha, f.MontoTotal, c.Nombre 
    FROM dbo.Factura f 
    INNER JOIN dbo.Cliente c ON f.ClienteID = c.ClienteID 
    WHERE f.Fecha BETWEEN @FechaInicio AND @FechaFin; 
END
GO
CREATE OR ALTER PROCEDURE dbo.sp_ReporteAuditoria @FechaInicio DATETIME, @FechaFin DATETIME AS 
BEGIN 
    SELECT LogID, FechaEvento, Usuario, Accion 
    FROM dbo.BitacoraAuditoria 
    WHERE FechaEvento BETWEEN @FechaInicio AND @FechaFin 
    ORDER BY FechaEvento; 
END
GO

-- =====================================================================
-- PARTE 2. MEDICIÓN "ANTES" (Sin Índices)
-- =====================================================================
-- aquí Activar Ctrl+M para ver el plan real.
-- Observa la pestaña "Mensajes" para ver los tiempos y lecturas.
SET STATISTICS TIME ON;
SET STATISTICS IO ON;
GO
PRINT '   EJECUTANDO sp_ReporteClientes (SIN ÍNDICES)'
EXEC dbo.sp_ReporteClientes @Estado = 'A';
GO


PRINT '   EJECUTANDO sp_ReporteFacturas (SIN ÍNDICES)'

EXEC dbo.sp_ReporteFacturas '2024-06-01', '2024-08-31';
GO

PRINT '   EJECUTANDO sp_ReporteAuditoria (SIN ÍNDICES)'
EXEC dbo.sp_ReporteAuditoria '2024-02-01', '2024-02-28';
GO

SET STATISTICS TIME OFF;
SET STATISTICS IO OFF;
GO

-- =====================================================================
-- PARTE 3. AFINAMIENTO: CREACIÓN DE ÍNDICES
-- =====================================================================
-- 1. NONCLUSTERED para Clientes (Con INCLUDE para evitar Key Lookup)
CREATE NONCLUSTERED INDEX IX_Cliente_Estado 
ON dbo.Cliente (Estado) 
INCLUDE (Nombre, FechaRegistro);
GO

-- 2. NONCLUSTERED para Facturas (Filtro por fecha)
CREATE NONCLUSTERED INDEX IX_Factura_Fecha 
ON dbo.Factura (Fecha) 
INCLUDE (ClienteID, MontoTotal);
GO

-- 3. CLUSTERED para Bitacora (Cambiamos la PK para que ordene físicamente por fecha)
-- El profesor valora que demuestres criterio para cambiar un Clustered.
DECLARE @ConstraintName NVARCHAR(200);
SELECT @ConstraintName = name FROM sys.key_constraints 
WHERE parent_object_id = OBJECT_ID('dbo.BitacoraAuditoria') AND type = 'PK';
IF @ConstraintName IS NOT NULL EXEC('ALTER TABLE dbo.BitacoraAuditoria DROP CONSTRAINT ' + @ConstraintName);

CREATE CLUSTERED INDEX CIX_Bitacora_Fecha ON dbo.BitacoraAuditoria (FechaEvento);
GO

-- Validación
SELECT Caso, Descripcion, Esperado, Obtenido,
       CASE WHEN Esperado = Obtenido THEN 'PASA' ELSE 'FALLA' END AS Resultado
FROM (
    SELECT 'CP-03.1', 'Existe el indice IX_Cliente_Estado', 1,
           (SELECT COUNT(*) FROM sys.indexes WHERE name = 'IX_Cliente_Estado' AND object_id = OBJECT_ID('dbo.Cliente'))
    UNION ALL
    SELECT 'CP-03.2', 'Existe el indice IX_Factura_Fecha', 1,
           (SELECT COUNT(*) FROM sys.indexes WHERE name = 'IX_Factura_Fecha' AND object_id = OBJECT_ID('dbo.Factura'))
    UNION ALL
    SELECT 'CP-03.3', 'Existe el indice CIX_Bitacora_Fecha', 1,
           (SELECT COUNT(*) FROM sys.indexes WHERE name = 'CIX_Bitacora_Fecha' AND object_id = OBJECT_ID('dbo.BitacoraAuditoria'))
) AS t(Caso, Descripcion, Esperado, Obtenido);
GO

-- =====================================================================
-- PARTE 4. MEDICIÓN "DESPUÉS" (Con Índices)
-- =====================================================================
-- Vuelve a ejecutar con Ctrl+M. Compara los Mensajes con la Parte 2.
SET STATISTICS TIME ON;
SET STATISTICS IO ON;
GO

PRINT '   EJECUTANDO sp_ReporteClientes (CON ÍNDICES)'
EXEC dbo.sp_ReporteClientes @Estado = 'A';
GO

PRINT '   EJECUTANDO sp_ReporteFacturas (CON ÍNDICES)'
EXEC dbo.sp_ReporteFacturas '2024-06-01', '2024-08-31';
GO

PRINT '   EJECUTANDO sp_ReporteAuditoria (CON ÍNDICES)'
EXEC dbo.sp_ReporteAuditoria '2024-02-01', '2024-02-28';
GO

SET STATISTICS TIME OFF;
SET STATISTICS IO OFF;
GO

/*
================================================================
----------------REQ 010 Diccionario de datos--------------------
================================================================
*/

USE Propi
go

--Esto muestra todas las columnas del sistema
SELECT s.name AS esquema, t.name AS tabla, c.name AS columna,
       ty.name AS tipo, c.max_length, c.is_nullable,
       dc.definition AS valor_default,
       CAST(ep.value AS NVARCHAR(500)) AS descripcion
FROM sys.tables t
JOIN sys.schemas s ON s.schema_id = t.schema_id
JOIN sys.columns c ON c.object_id = t.object_id
JOIN sys.types ty ON ty.user_type_id = c.user_type_id
LEFT JOIN sys.default_constraints dc ON dc.object_id = c.default_object_id
LEFT JOIN sys.extended_properties ep ON ep.major_id = t.object_id
     AND ep.minor_id = c.column_id AND ep.name = 'MS_Description'
ORDER BY s.name, t.name, c.column_id;
GO


/*
================================================================
----------------REQ 012 Política de seguridad---------------------
================================================================
*/

USE Propi;
GO

--1.Crear una Clave Maestra de Base de Datos (DMK)
CREATE MASTER KEY ENCRYPTION BY PASSWORD = 'f4ctUra7ion23!';
GO

--2. Crear un Certificado para proteger la Clave Simétrica
CREATE CERTIFICATE Cert_Propi_Columna
WITH SUBJECT = 'Certificado para cifrado de columnas sensibles de Propi';
GO

--3.Crear la Clave Simétrica: Es la llave que realmente cifrará y descifrará los datos,
--y está protegida por el certificado del paso anterior.
CREATE SYMMETRIC KEY SK_Propi_Columna
WITH ALGORITHM = AES_256
ENCRYPTION BY CERTIFICATE Cert_Propi_Columna;
GO

--4.Cifrar y Descifrar los Datos
-- Abrir la llave para poder usarla
OPEN SYMMETRIC KEY SK_Propi_Columna DECRYPTION BY CERTIFICATE Cert_Propi_Columna;
GO

-- Ejemplo: Cifrar el correo electrónico de un cliente (simulado)
-- Se usa la función EncryptByKey
UPDATE dbo.Cliente
SET CorreoElectronico_Cifrado = EncryptByKey(Key_GUID('SK_Propi_Columna'), CorreoElectronico)
WHERE ClienteID = 1;

-- Ejemplo: Descifrar el dato para consultarlo
-- Se usa la función DecryptByKey
SELECT ClienteID, CONVERT(VARCHAR(256), DecryptByKey(CorreoElectronico_Cifrado)) AS CorreoDescifrado
FROM dbo.Cliente
WHERE ClienteID = 1;
GO

-- Cerrar la llave por seguridad
CLOSE SYMMETRIC KEY SK_Propi_Columna;
GO

--======================Monitoreo y Auditoría =====================

--1. Crear una Auditoría a Nivel de Servidor
USE master;
GO
CREATE SERVER AUDIT Audit_Propi
TO FILE (FILEPATH = 'C:\SQLAudit\'); -- verificar que esta carpeta exista
GO
ALTER SERVER AUDIT Audit_Propi WITH (STATE = ON);
GO

/*
2.Crear una Especificación de Auditoría a Nivel de Base de Datos:
Define qué eventos específicos se van a registrar. Por ejemplo, rastrear 
todos los SELECT sobre la tabla Cliente.*/
USE Propi;
GO
CREATE DATABASE AUDIT SPECIFICATION Audit_Propi_Cliente
FOR SERVER AUDIT Audit_Propi
ADD (SELECT, INSERT, UPDATE, DELETE ON dbo.Cliente BY public)
WITH (STATE = ON);
GO


/*
================================================================
----------------REQ 013 Política de seguridad---------------------
================================================================
*/

/*
     Paso 1: Preparar la Base de Datos (Cambiar a modelo FULL)
*/
USE master;
GO
-- Cambiar el modelo de recuperación a FULL
ALTER DATABASE Propi SET RECOVERY FULL;
GO
-- Hacer un primer Backup Completo para inicializar el modelo
-- rev la ruta por defecto SELECT SERVERPROPERTY('InstanceDefaultBackupPath') AS Ruta_Defecto_Backups;
BACKUP DATABASE Propi 
TO DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Full_Inicial.bak' 
WITH FORMAT, INIT, NAME = 'Backup Full Inicial', STATS = 10;
GO

--Paso 2: Crear los Scripts de Respaldo 

--A. Backup Completo (Se programaría para los Domingos):
BACKUP DATABASE Propi 
TO DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Full.bak' 
WITH FORMAT, INIT, STATS = 10;
GO

--B. Backup Diferencial (Se programaría de Lunes a Sábado):
BACKUP DATABASE Propi 
TO DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Diff.bak' 
WITH DIFFERENTIAL, STATS = 10;
GO

--C. Backup de Log de Transacciones (Se programaría cada 15 min):
--Importante: Debes colocarle un nombre dinámico o fecha para no sobrescribir el mismo archivo.

DECLARE @Fecha VARCHAR(20) = FORMAT(GETDATE(), 'yyyyMMdd_HHmm');
DECLARE @Ruta VARCHAR(200) = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Log_' + @Fecha + '.trn';

BACKUP LOG Propi 
TO DISK = @Ruta
WITH STATS = 10;
GO



--Paso 3: El Procedimiento de Recuperación Paso a Paso (El Simulacro)

-- SIMULACRO DE RECUPERACIÓN (DRP) EN UN SERVIDOR DE CONTINGENCIA


-- PASO 1: Restaurar el Backup Completo (Full) con NORECOVERY
RESTORE DATABASE Propi_Recuperada 
FROM DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Full.bak' 
WITH NORECOVERY, REPLACE, STATS = 10;
-- NOTA: Usamos NORECOVERY para decirle a SQL Server "no abras la BD aún, 
-- falta aplicar más backups".
GO

-- PASO 2: Restaurar el Backup Diferencial con NORECOVERY
RESTORE DATABASE Propi_Recuperada 
FROM DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Diff.bak' 
WITH NORECOVERY, STATS = 10;
GO

-- PASO 3: Restaurar los Backups de Log en orden cronológico.
-- Restauramos todos los logs hasta el penúltimo con NORECOVERY
RESTORE LOG Propi_Recuperada 
FROM DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Log_20231026_1400.trn' 
WITH NORECOVERY, STATS = 10;
GO

-- PASO 4: Restaurar el ÚLTIMO log con RECOVERY.
-- Esto abre la base de datos y la deja lista para usarse.
-- Aquí usamos STOPAT para recuperar exactamente el estado antes de la caída (14:15).
RESTORE LOG Propi_Recuperada 
FROM DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Log_20231026_1415.trn' 
WITH RECOVERY, STATS = 10;
GO

-- PASO 5: Verificar que la base de datos funciona
DBCC CHECKDB(Propi_Recuperada);
GO

--===================== Paso 4: Pruebas de Mantenimiento ======================
-- Verificar que el archivo de backup no esté corrupto (Prueba mensual)
RESTORE VERIFYONLY 
FROM DISK = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\Backup\Propi_Full.bak';
GO


SELECT 
    SERVERPROPERTY('Edition') AS Edicion,
    SERVERPROPERTY('IsHadrEnabled') AS AlwaysOn_Habilitado;

    /*
================================================================
----------------REQ 014 Base de datos secundaria ---------------
================================================================
*/

-- 1. Crear la base de datos secundaria
EXECUTE AS USER = 'Admin-propi'
GO

CREATE DATABASE Propi_Secundaria;
GO
USE Propi_Secundaria;
GO

-- 2. Crear la misma tabla que queremos replicar (ejemplo: Facturas)
CREATE TABLE dbo.Facturas_Replica (
    FacturaID INT PRIMARY KEY,
    ClienteID INT,
    Fecha DATETIME,
    MontoTotal DECIMAL(18,2)
);
GO


--Paso 2: Crear el Trigger en la Base de Datos Principal
USE Propi;
GO

CREATE TRIGGER trg_Replicar_Facturas
ON dbo.Facturas
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- Replicar los INSERTs y UPDATEs (hacia la secundaria)
    MERGE Propi_Secundaria.dbo.Facturas_Replica AS Destino
    USING (SELECT FacturaID, ClienteID, Fecha, MontoTotal FROM inserted) AS Origen
    ON Destino.FacturaID = Origen.FacturaID
    WHEN MATCHED THEN 
        UPDATE SET ClienteID = Origen.ClienteID, Fecha = Origen.Fecha, MontoTotal = Origen.MontoTotal
    WHEN NOT MATCHED BY TARGET THEN 
        INSERT (FacturaID, ClienteID, Fecha, MontoTotal) 
        VALUES (Origen.FacturaID, Origen.ClienteID, Origen.Fecha, Origen.MontoTotal);

    -- Replicar los DELETEs
    DELETE FROM Propi_Secundaria.dbo.Facturas_Replica 
    WHERE FacturaID IN (SELECT FacturaID FROM deleted);
END;
GO

--Paso 3: Hacer la Prueba y Medir la Latencia

-- 1. Insertamos una factura en la principal
USE Propi;
INSERT INTO dbo.Facturas (ClienteID, Fecha, MontoTotal) 
VALUES (1, GETDATE(), 15000.00);

-- 2. Consultamos la secundaria INMEDIATAMENTE
USE Propi_Secundaria;
SELECT * FROM dbo.Facturas_Replica; 
-- ¡El dato debe aparecer instantáneamente! Latencia < 1 segundo.