SELECT
    CL.ClienteID,
    CL.Nombre,

    P.Nombre AS Provincia,
    C.Nombre AS Canton,
    D.Nombre AS Distrito,

    CL.Barrio,
    CL.OtrasSenas,

    AE.CodigoActividad,
    AE.Descripcion AS ActividadEconomica

FROM Cliente CL

INNER JOIN Distrito D
    ON CL.DistritoID = D.DistritoID

INNER JOIN Canton C
    ON D.CantonID = C.CantonID

INNER JOIN Provincia P
    ON C.ProvinciaID = P.ProvinciaID

INNER JOIN ActividadEconomica AE
    ON CL.ActividadEconomicaID = AE.ActividadEconomicaID;
GO