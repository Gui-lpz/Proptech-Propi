/* =====================================================================
   PROYECTO PROPI - SISTEMA DE GESTION DE FACTURACION
   IF0010 BASES DE DATOS II - II CICLO 2026
   Script 01: creacion de la base de datos, tablas, catalogos, llaves,
              restricciones, bitacoras y cifrado de columnas */

/* ---------------------------------------------------------------------
   1. BASE DE DATOS
   --------------------------------------------------------------------- */
IF DB_ID(N'PropiFacturacion') IS NULL
    CREATE DATABASE PropiFacturacion;
GO
USE PropiFacturacion;
GO

/* ---------------------------------------------------------------------
   2. CIFRADO DE CELDAS (REQ 006)
   Jerarquia: Service Master Key -> Database Master Key -> Certificado
              -> Llave simetrica AES_256
   IMPORTANTE: cambie la contrasena y guardela en un lugar seguro.
   --------------------------------------------------------------------- */
IF NOT EXISTS (SELECT 1 FROM sys.symmetric_keys WHERE name = N'##MS_DatabaseMasterKey##')
    CREATE MASTER KEY ENCRYPTION BY PASSWORD = N'#Propi2026';
GO
IF NOT EXISTS (SELECT 1 FROM sys.certificates WHERE name = N'cert_Propi')
    CREATE CERTIFICATE cert_Propi
        WITH SUBJECT = N'Cifrado de datos sensibles de Cliente',
             EXPIRY_DATE = '20301231';
GO
IF NOT EXISTS (SELECT 1 FROM sys.symmetric_keys WHERE name = N'sk_Propi')
    CREATE SYMMETRIC KEY sk_Propi
        WITH ALGORITHM = AES_256
        ENCRYPTION BY CERTIFICATE cert_Propi;
GO
/* Respaldo del certificado (ajuste las rutas y contrasena; sin esto no se
   pueden descifrar los datos si se pierde el servidor):
BACKUP CERTIFICATE cert_Propi
    TO FILE = 'X:\Backup\cert_Propi.cer'
    WITH PRIVATE KEY (FILE = 'X:\Backup\cert_Propi.pvk',
                      ENCRYPTION BY PASSWORD = N'OtraClave#Segura2026');
*/

/* ---------------------------------------------------------------------
   3. CATALOGOS GEOGRAFICOS Y DE IDENTIFICACION
   --------------------------------------------------------------------- */
CREATE TABLE dbo.Provincia (
    ProvinciaID      INT IDENTITY(1,1) NOT NULL,
    CodigoProvincia  VARCHAR(10)  NOT NULL,
    Nombre           VARCHAR(100) NOT NULL,
    CONSTRAINT PK_Provincia PRIMARY KEY (ProvinciaID),
    CONSTRAINT UQ_Provincia_Codigo UNIQUE (CodigoProvincia)
);
GO
CREATE TABLE dbo.Canton (
    CantonID      INT IDENTITY(1,1) NOT NULL,
    ProvinciaID   INT          NOT NULL,
    CodigoCanton  VARCHAR(10)  NOT NULL,
    Nombre        VARCHAR(100) NOT NULL,
    CONSTRAINT PK_Canton PRIMARY KEY (CantonID),
    CONSTRAINT FK_Canton_Provincia FOREIGN KEY (ProvinciaID) REFERENCES dbo.Provincia (ProvinciaID),
    CONSTRAINT UQ_Canton_Codigo UNIQUE (ProvinciaID, CodigoCanton)
);
GO
CREATE TABLE dbo.Distrito (
    DistritoID      INT IDENTITY(1,1) NOT NULL,
    CantonID        INT          NOT NULL,
    CodigoDistrito  VARCHAR(10)  NOT NULL,
    Nombre          VARCHAR(100) NOT NULL,
    CONSTRAINT PK_Distrito PRIMARY KEY (DistritoID),
    CONSTRAINT FK_Distrito_Canton FOREIGN KEY (CantonID) REFERENCES dbo.Canton (CantonID),
    CONSTRAINT UQ_Distrito_Codigo UNIQUE (CantonID, CodigoDistrito)
);
GO
CREATE TABLE dbo.TipoIdentificacion (
    TipoIdentificacionID  INT IDENTITY(1,1) NOT NULL,
    Codigo                VARCHAR(10)  NOT NULL,
    Descripcion           VARCHAR(100) NOT NULL,
    Estado                BIT          NOT NULL CONSTRAINT DF_TipoIdentificacion_Estado DEFAULT (1),
    CONSTRAINT PK_TipoIdentificacion PRIMARY KEY (TipoIdentificacionID),
    CONSTRAINT UQ_TipoIdentificacion_Codigo UNIQUE (Codigo)
);
GO
CREATE TABLE dbo.ActividadEconomica (
    ActividadEconomicaID  INT IDENTITY(1,1) NOT NULL,
    Codigo                VARCHAR(10)  NOT NULL,
    Descripcion           VARCHAR(150) NOT NULL,
    Estado                BIT          NOT NULL CONSTRAINT DF_ActividadEconomica_Estado DEFAULT (1),
    CONSTRAINT PK_ActividadEconomica PRIMARY KEY (ActividadEconomicaID),
    CONSTRAINT UQ_ActividadEconomica_Codigo UNIQUE (Codigo)
);
GO

/* ---------------------------------------------------------------------
   4. CATALOGOS DE FACTURACION
   --------------------------------------------------------------------- */
CREATE TABLE dbo.UnidadMedida (
    UnidadMedidaID  INT IDENTITY(1,1) NOT NULL,
    Codigo          VARCHAR(10)  NOT NULL,
    Descripcion     VARCHAR(100) NOT NULL,
    Estado          BIT          NOT NULL CONSTRAINT DF_UnidadMedida_Estado DEFAULT (1),
    CONSTRAINT PK_UnidadMedida PRIMARY KEY (UnidadMedidaID),
    CONSTRAINT UQ_UnidadMedida_Codigo UNIQUE (Codigo)
);
GO
CREATE TABLE dbo.TipoImpuesto (
    TipoImpuestoID  INT IDENTITY(1,1) NOT NULL,
    Codigo          VARCHAR(10)  NOT NULL,
    Descripcion     VARCHAR(100) NOT NULL,
    Porcentaje      DECIMAL(5,2) NOT NULL,   -- ejemplo: 13.00
    Tarifa          DECIMAL(5,4) NOT NULL,   -- factor decimal, ejemplo: 0.1300
    Estado          BIT          NOT NULL CONSTRAINT DF_TipoImpuesto_Estado DEFAULT (1),
    CONSTRAINT PK_TipoImpuesto PRIMARY KEY (TipoImpuestoID),
    CONSTRAINT UQ_TipoImpuesto_Codigo UNIQUE (Codigo),
    CONSTRAINT CK_TipoImpuesto_Porcentaje CHECK (Porcentaje BETWEEN 0 AND 100),
    CONSTRAINT CK_TipoImpuesto_Tarifa CHECK (Tarifa BETWEEN 0 AND 1)
);
GO
CREATE TABLE dbo.CondicionVenta (
    CondicionVentaID  INT IDENTITY(1,1) NOT NULL,
    Codigo            CHAR(2)      NOT NULL,   -- CO contado, CR credito
    Descripcion       VARCHAR(50)  NOT NULL,
    CONSTRAINT PK_CondicionVenta PRIMARY KEY (CondicionVentaID),
    CONSTRAINT UQ_CondicionVenta_Codigo UNIQUE (Codigo)
);
GO
CREATE TABLE dbo.MedioPago (
    MedioPagoID  INT IDENTITY(1,1) NOT NULL,
    Codigo       CHAR(2)      NOT NULL,
    Descripcion  VARCHAR(50)  NOT NULL,
    CONSTRAINT PK_MedioPago PRIMARY KEY (MedioPagoID),
    CONSTRAINT UQ_MedioPago_Codigo UNIQUE (Codigo)
);
GO
CREATE TABLE dbo.Moneda (
    MonedaID     INT IDENTITY(1,1) NOT NULL,
    Codigo       CHAR(3)      NOT NULL,   -- CRC, USD
    Descripcion  VARCHAR(50)  NOT NULL,
    CONSTRAINT PK_Moneda PRIMARY KEY (MonedaID),
    CONSTRAINT UQ_Moneda_Codigo UNIQUE (Codigo)
);
GO

/* ---------------------------------------------------------------------
   5. CLIENTE Y PRODUCTO/SERVICIO
   Cliente: NumeroIdentificacion, CorreoElectronico y Telefono se guardan
   cifrados (VARBINARY). NumeroIdentificacionHash permite garantizar
   unicidad y buscar sin descifrar (se calcula en el procedimiento de
   insercion con HASHBYTES; se recomienda anteponer un valor secreto).
   --------------------------------------------------------------------- */
CREATE TABLE dbo.Cliente (
    ClienteID                 INT IDENTITY(1,1) NOT NULL,
    TipoIdentificacionID      INT            NOT NULL,
    NumeroIdentificacion      VARBINARY(256) NOT NULL,   -- cifrado
    NumeroIdentificacionHash  VARBINARY(32)  NOT NULL,   -- SHA2_256 para unicidad
    Nombre                    VARCHAR(150)   NOT NULL,
    DistritoID                INT            NOT NULL,
    Barrio                    VARCHAR(100)   NULL,
    ProfesionOficio           VARCHAR(100)   NULL,
    ActividadEconomicaID      INT            NOT NULL,
    CorreoElectronico         VARBINARY(512) NOT NULL,   -- cifrado
    Telefono                  VARBINARY(256) NULL,       -- cifrado
    FechaRegistro             DATETIME       NOT NULL CONSTRAINT DF_Cliente_FechaRegistro DEFAULT (GETDATE()),
    Estado                    BIT            NOT NULL CONSTRAINT DF_Cliente_Estado DEFAULT (1),
    CONSTRAINT PK_Cliente PRIMARY KEY (ClienteID),
    CONSTRAINT FK_Cliente_TipoIdentificacion FOREIGN KEY (TipoIdentificacionID) REFERENCES dbo.TipoIdentificacion (TipoIdentificacionID),
    CONSTRAINT FK_Cliente_Distrito FOREIGN KEY (DistritoID) REFERENCES dbo.Distrito (DistritoID),
    CONSTRAINT FK_Cliente_ActividadEconomica FOREIGN KEY (ActividadEconomicaID) REFERENCES dbo.ActividadEconomica (ActividadEconomicaID)
);
GO
CREATE UNIQUE INDEX UX_Cliente_Identificacion
    ON dbo.Cliente (TipoIdentificacionID, NumeroIdentificacionHash);
GO
CREATE TABLE dbo.ProductoServicio (
    ProductoServicioID  INT IDENTITY(1,1) NOT NULL,
    CodigoCABYS         VARCHAR(13)   NOT NULL,
    Descripcion         VARCHAR(250)  NOT NULL,
    UnidadMedidaID      INT           NOT NULL,
    Precio              DECIMAL(18,2) NOT NULL,
    TipoImpuestoID      INT           NOT NULL,
    Estado              BIT           NOT NULL CONSTRAINT DF_ProductoServicio_Estado DEFAULT (1),
    CONSTRAINT PK_ProductoServicio PRIMARY KEY (ProductoServicioID),
    CONSTRAINT UQ_ProductoServicio_CABYS UNIQUE (CodigoCABYS),
    CONSTRAINT FK_ProductoServicio_UnidadMedida FOREIGN KEY (UnidadMedidaID) REFERENCES dbo.UnidadMedida (UnidadMedidaID),
    CONSTRAINT FK_ProductoServicio_TipoImpuesto FOREIGN KEY (TipoImpuestoID) REFERENCES dbo.TipoImpuesto (TipoImpuestoID),
    CONSTRAINT CK_ProductoServicio_CABYS CHECK (LEN(CodigoCABYS) = 13 AND CodigoCABYS NOT LIKE '%[^0-9]%'),
    CONSTRAINT CK_ProductoServicio_Precio CHECK (Precio >= 0)
);
GO

/* ---------------------------------------------------------------------
   6. FACTURAS, DETALLE Y NOTAS DE CREDITO/DEBITO
   --------------------------------------------------------------------- */
CREATE TABLE dbo.Facturas (
    FacturaID         INT IDENTITY(1,1) NOT NULL,
    ClienteID         INT           NOT NULL,
    Fecha             DATETIME      NOT NULL CONSTRAINT DF_Facturas_Fecha DEFAULT (GETDATE()),
    CondicionVentaID  INT           NOT NULL,
    MedioPagoID       INT           NOT NULL,
    MonedaID          INT           NOT NULL,
    TipoDeCambio      DECIMAL(18,4) NOT NULL CONSTRAINT DF_Facturas_TipoDeCambio DEFAULT (1),
    Observaciones     VARCHAR(500)  NULL,
    Subtotal          DECIMAL(18,2) NOT NULL,
    TotalImpuestos    DECIMAL(18,2) NOT NULL,
    MontoTotal        DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_Facturas PRIMARY KEY (FacturaID),
    CONSTRAINT FK_Facturas_Cliente FOREIGN KEY (ClienteID) REFERENCES dbo.Cliente (ClienteID),
    CONSTRAINT FK_Facturas_CondicionVenta FOREIGN KEY (CondicionVentaID) REFERENCES dbo.CondicionVenta (CondicionVentaID),
    CONSTRAINT FK_Facturas_MedioPago FOREIGN KEY (MedioPagoID) REFERENCES dbo.MedioPago (MedioPagoID),
    CONSTRAINT FK_Facturas_Moneda FOREIGN KEY (MonedaID) REFERENCES dbo.Moneda (MonedaID),
    CONSTRAINT CK_Facturas_TipoDeCambio CHECK (TipoDeCambio > 0),
    CONSTRAINT CK_Facturas_Montos CHECK (Subtotal >= 0 AND TotalImpuestos >= 0 AND MontoTotal = Subtotal + TotalImpuestos)
);
GO
CREATE TABLE dbo.FacturasDetalle (
    FacturaID           INT           NOT NULL,
    LineaNum            INT           NOT NULL,
    ProductoServicioID  INT           NOT NULL,
    Cantidad            DECIMAL(18,2) NOT NULL,
    PrecioUnitario      DECIMAL(18,2) NOT NULL,   -- precio al momento de la venta
    MontoImpuesto       DECIMAL(18,2) NOT NULL,
    MontoLinea          DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_FacturasDetalle PRIMARY KEY (FacturaID, LineaNum),
    CONSTRAINT FK_FacturasDetalle_Facturas FOREIGN KEY (FacturaID) REFERENCES dbo.Facturas (FacturaID),
    CONSTRAINT FK_FacturasDetalle_Producto FOREIGN KEY (ProductoServicioID) REFERENCES dbo.ProductoServicio (ProductoServicioID),
    CONSTRAINT CK_FacturasDetalle_Cantidad CHECK (Cantidad > 0),
    CONSTRAINT CK_FacturasDetalle_Montos CHECK (PrecioUnitario >= 0 AND MontoImpuesto >= 0 AND MontoLinea >= 0)
);
GO
CREATE TABLE dbo.NotasCreditoDebito (
    Codigo     INT IDENTITY(1,1) NOT NULL,
    Fecha      DATETIME      NOT NULL CONSTRAINT DF_NotasCreditoDebito_Fecha DEFAULT (GETDATE()),
    FacturaID  INT           NOT NULL,
    Tipo       CHAR(1)       NOT NULL,   -- C credito, D debito
    Motivo     VARCHAR(500)  NOT NULL,
    Monto      DECIMAL(18,2) NOT NULL,
    CONSTRAINT PK_NotasCreditoDebito PRIMARY KEY (Codigo),
    CONSTRAINT FK_NotasCreditoDebito_Facturas FOREIGN KEY (FacturaID) REFERENCES dbo.Facturas (FacturaID),
    CONSTRAINT CK_NotasCreditoDebito_Tipo CHECK (Tipo IN ('C', 'D')),
    CONSTRAINT CK_NotasCreditoDebito_Monto CHECK (Monto > 0)
);
GO

/* ---------------------------------------------------------------------
   7. BITACORAS
   BitacoraAcceso   (REQ 003): ingresos y salidas de la aplicacion.
   BitacoraAuditoria (REQ 005): cambios INSERT/UPDATE/DELETE por trigger.
   La bitacora de auditoria no lleva llaves foraneas a proposito: debe
   conservar el registro aunque se borre el dato original.
   --------------------------------------------------------------------- */
CREATE TABLE dbo.BitacoraAcceso (
    BitacoraAccesoID  BIGINT IDENTITY(1,1) NOT NULL,
    NombreUsuario     VARCHAR(128) NOT NULL,
    NombreTerminal    VARCHAR(128) NULL,
    DireccionIP       VARCHAR(45)  NULL,      -- admite IPv4 e IPv6
    FechaHoraIngreso  DATETIME     NOT NULL,
    FechaHoraSalida   DATETIME     NULL,
    FechaRegistro     DATETIME     NOT NULL CONSTRAINT DF_BitacoraAcceso_FechaRegistro DEFAULT (GETDATE()),
    CONSTRAINT PK_BitacoraAcceso PRIMARY KEY (BitacoraAccesoID),
    CONSTRAINT CK_BitacoraAcceso_Fechas CHECK (FechaHoraSalida IS NULL OR FechaHoraSalida >= FechaHoraIngreso)
);
GO
CREATE TABLE dbo.BitacoraAuditoria (
    BitacoraID         BIGINT IDENTITY(1,1) NOT NULL,
    fec_registro       DATETIME      NOT NULL CONSTRAINT DF_BitacoraAuditoria_Fecha DEFAULT (GETDATE()),
    cod_guid           VARCHAR(255)  NULL     CONSTRAINT DF_BitacoraAuditoria_Guid DEFAULT (CONVERT(VARCHAR(255), NEWID())),
    ind_accion         CHAR(1)       NOT NULL,   -- I insercion, D borrado, U modificacion
    cod_usuario        CHAR(128)     NOT NULL,
    cod_sesion_usuario INT           NULL,
    cod_tabla          VARCHAR(255)  NOT NULL,
    cod_llave          VARCHAR(2000) NULL,
    des_datos          VARCHAR(MAX)  NULL,
    CONSTRAINT PK_BitacoraAuditoria PRIMARY KEY (BitacoraID),
    CONSTRAINT CK_BitacoraAuditoria_Accion CHECK (ind_accion IN ('I', 'D', 'U'))
);
GO

/* ---------------------------------------------------------------------
   8. DATOS SEMILLA DE CATALOGOS (ejemplos; ajuste a su necesidad)
   --------------------------------------------------------------------- */
INSERT dbo.TipoIdentificacion (Codigo, Descripcion) VALUES
    ('01', 'Cédula física'), ('02', 'Cédula jurídica'), ('03', 'DIMEX'), ('04', 'NITE');

INSERT dbo.CondicionVenta (Codigo, Descripcion) VALUES ('CO', 'Contado'), ('CR', 'Crédito');

INSERT dbo.MedioPago (Codigo, Descripcion) VALUES
    ('EF', 'Efectivo'), ('TA', 'Tarjeta'), ('CH', 'Cheque'),
    ('TR', 'Transferencia'), ('SM', 'SINPE Móvil'), ('OT', 'Otros');

INSERT dbo.Moneda (Codigo, Descripcion) VALUES ('CRC', 'Colón costarricense'), ('USD', 'Dólar estadounidense');

INSERT dbo.TipoImpuesto (Codigo, Descripcion, Porcentaje, Tarifa) VALUES
    ('08', 'IVA tarifa general', 13.00, 0.1300),
    ('04', 'IVA tarifa reducida', 4.00, 0.0400),
    ('02', 'IVA tarifa reducida', 1.00, 0.0100),
    ('10', 'Exento', 0.00, 0.0000);

INSERT dbo.UnidadMedida (Codigo, Descripcion) VALUES
    ('Unid', 'Unidad'), ('Sp', 'Servicios profesionales'), ('m2', 'Metro cuadrado');

INSERT dbo.ActividadEconomica (Codigo, Descripcion) VALUES
    ('A001', 'Actividades inmobiliarias'),
    ('A002', 'Comercio al por menor'),
    ('A003', 'Servicios profesionales');

INSERT dbo.Provincia (CodigoProvincia, Nombre) VALUES ('3', 'Cartago');
INSERT dbo.Canton (ProvinciaID, CodigoCanton, Nombre)
    SELECT ProvinciaID, '02', 'Paraíso' FROM dbo.Provincia WHERE CodigoProvincia = '3';
INSERT dbo.Distrito (CantonID, CodigoDistrito, Nombre)
    SELECT c.CantonID, '01', 'Paraíso' FROM dbo.Canton AS c WHERE c.CodigoCanton = '02';
GO

/* ---------------------------------------------------------------------
   9. PRUEBA RAPIDA: insertar un cliente con datos cifrados y leerlo
   --------------------------------------------------------------------- */
OPEN SYMMETRIC KEY sk_Propi DECRYPTION BY CERTIFICATE cert_Propi;

INSERT dbo.Cliente (TipoIdentificacionID, NumeroIdentificacion, NumeroIdentificacionHash,
                    Nombre, DistritoID, Barrio, ProfesionOficio, ActividadEconomicaID,
                    CorreoElectronico, Telefono)
SELECT  ti.TipoIdentificacionID,
        EncryptByKey(Key_GUID('sk_Propi'), '304560789'),
        HASHBYTES('SHA2_256', '01|304560789'),
        'Cliente de prueba',
        (SELECT TOP 1 DistritoID FROM dbo.Distrito),
        'Centro', 'Ingeniera',
        (SELECT TOP 1 ActividadEconomicaID FROM dbo.ActividadEconomica),
        EncryptByKey(Key_GUID('sk_Propi'), 'prueba@correo.com'),
        EncryptByKey(Key_GUID('sk_Propi'), '88887777')
FROM    dbo.TipoIdentificacion AS ti
WHERE   ti.Codigo = '01';

SELECT  Nombre,
        CONVERT(VARCHAR(30),  DECRYPTBYKEY(NumeroIdentificacion)) AS NumeroIdentificacion,
        CONVERT(VARCHAR(100), DECRYPTBYKEY(CorreoElectronico))    AS CorreoElectronico,
        CONVERT(VARCHAR(20),  DECRYPTBYKEY(Telefono))             AS Telefono
FROM    dbo.Cliente;

CLOSE SYMMETRIC KEY sk_Propi;
GO
