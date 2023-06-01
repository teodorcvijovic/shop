

WITH CTE AS (
    SELECT c1.IdC AS CityFrom, c2.IdC AS CityTo, d1.[Days] AS Distance,
           CAST(c1.IdC AS VARCHAR(MAX)) + '->' + CAST(c2.IdC AS VARCHAR(MAX)) AS Path, 1 AS [Level]
    FROM Distance d1 
	JOIN City c1 ON d1.IdC1 = c1.IdC 
	JOIN City c2 ON d1.IdC2 = c2.IdC
    
    UNION ALL
    
    SELECT c.CityFrom, d2.IdC2, (c.Distance + d2.[Days]) AS Distance,
           c.Path + '->' + CAST(d2.IdC2 AS VARCHAR(MAX)), c.[Level] + 1
    FROM CTE c
    JOIN Distance d2 ON c.CityTo = d2.IdC1
    WHERE c.[Level] < 20
)
SELECT TOP 1 CityFrom, CityTo, MIN(Distance) AS MinDistance, Path AS ShortestPath
FROM CTE
WHERE (CityFrom = 31 AND CityTo = 30) OR (CityFrom = 30 AND CityTo = 31)
GROUP BY CityFrom, CityTo, Path
ORDER BY MinDistance ASC;