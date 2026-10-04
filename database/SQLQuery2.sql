CREATE TABLE Provincia
(
    ProvinciaID INT IDENTITY(1,1)
        PRIMARY KEY,

    CodigoProvincia VARCHAR(2)
        NOT NULL
        UNIQUE,

    Nombre VARCHAR(100)
        NOT NULL,

    Estado CHAR(1)
        NOT NULL
        DEFAULT 'A',

    CONSTRAINT CK_Provincia_Estado
        CHECK (Estado IN ('A', 'I'))
);
GO



CREATE TABLE Canton
(
    CantonID INT IDENTITY(1,1)
        PRIMARY KEY,

    ProvinciaID INT
        NOT NULL,

    CodigoCanton VARCHAR(4)
        NOT NULL
        UNIQUE,

    Nombre VARCHAR(100)
        NOT NULL,

    Estado CHAR(1)
        NOT NULL
        DEFAULT 'A',

    CONSTRAINT FK_Canton_Provincia
        FOREIGN KEY (ProvinciaID)
        REFERENCES Provincia(ProvinciaID),

    CONSTRAINT CK_Canton_Estado
        CHECK (Estado IN ('A', 'I'))
);
GO


CREATE TABLE Distrito
(
    DistritoID INT IDENTITY(1,1)
        PRIMARY KEY,

    CantonID INT
        NOT NULL,

    CodigoDistrito VARCHAR(6)
        NOT NULL
        UNIQUE,

    Nombre VARCHAR(100)
        NOT NULL,

    Estado CHAR(1)
        NOT NULL
        DEFAULT 'A',

    CONSTRAINT FK_Distrito_Canton
        FOREIGN KEY (CantonID)
        REFERENCES Canton(CantonID),

    CONSTRAINT CK_Distrito_Estado
        CHECK (Estado IN ('A', 'I'))
);
GO

CREATE TABLE ActividadEconomica
(
    ActividadEconomicaID INT IDENTITY(1,1)
        PRIMARY KEY,

    CodigoActividad VARCHAR(6)
        NOT NULL
        UNIQUE,

    Descripcion VARCHAR(250)
        NOT NULL,

    Estado CHAR(1)
        NOT NULL
        DEFAULT 'A',

    CONSTRAINT CK_ActividadEconomica_Estado
        CHECK (Estado IN ('A', 'I'))
);
GO


CREATE TABLE Cliente
(
    ClienteID INT IDENTITY(1,1)
        PRIMARY KEY,

    Nombre VARCHAR(150)
        NOT NULL,

    TipoIdentificacion VARCHAR(2)
        NOT NULL,

    NumeroIdentificacion VARCHAR(30)
        NOT NULL,

    DistritoID INT
        NOT NULL,

    Barrio VARCHAR(150)
        NULL,

    OtrasSenas VARCHAR(500)
        NULL,

    ProfesionOficio VARCHAR(150)
        NULL,

    ActividadEconomicaID INT
        NOT NULL,

    CorreoElectronico VARCHAR(150)
        NULL,

    Telefono VARCHAR(30)
        NULL,

    FechaRegistro DATETIME2
        NOT NULL
        DEFAULT SYSDATETIME(),

    Estado CHAR(1)
        NOT NULL
        DEFAULT 'A',

    CONSTRAINT UQ_Cliente_Identificacion
        UNIQUE (NumeroIdentificacion),

    CONSTRAINT FK_Cliente_Distrito
        FOREIGN KEY (DistritoID)
        REFERENCES Distrito(DistritoID),

    CONSTRAINT FK_Cliente_ActividadEconomica
        FOREIGN KEY (ActividadEconomicaID)
        REFERENCES ActividadEconomica(ActividadEconomicaID),

    CONSTRAINT CK_Cliente_Estado
        CHECK (Estado IN ('A', 'I'))
);
GO
