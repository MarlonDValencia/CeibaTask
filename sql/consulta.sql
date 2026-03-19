SELECT DISTINCT c.nombre
FROM Cliente c
INNER JOIN Inscripcion i ON c.id = i.idCliente
WHERE NOT EXISTS (
    SELECT 1
    FROM Disponibilidad d
    WHERE d.idProducto = i.idProducto
    AND d.idSucursal NOT IN (
        SELECT v.idSucursal
        FROM Visitan v
        WHERE v.idCliente = c.id
    )
);
