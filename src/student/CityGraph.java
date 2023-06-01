package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityGraph {

    public static class Node {
        public int cityId;
        public int distance;

        public Node(int cityId, int distance) {
            this.cityId = cityId;
            this.distance = distance;
        }
    }

    public static HashMap<Integer, List<Node>> activeOrders = new HashMap<>();

    // TO DO: activate order

    // TO DO: update active orders - triggered from time func

    public static List<Node> convertStringToList(String string) {
        List<Node> arrayList = new ArrayList<>();
        String[] strArray = string.split("-");
        arrayList.add(new Node(Integer.parseInt(strArray[0]), 0));
        for(int i = 1; i < strArray.length - 1; i += 2) {
            arrayList.add(new Node(Integer.parseInt(strArray[i + 1]), Integer.parseInt(strArray[i])));
        }
        return arrayList;
    }

    public static String shortestPathQuery = "WITH CTE AS (\n" +
            "    SELECT c1.IdC AS CityFrom, c2.IdC AS CityTo, d1.[Days] AS Distance,\n" +
            "           CAST(c1.IdC AS VARCHAR(MAX)) + '-' + CAST(d1.[Days] AS VARCHAR(MAX)) + '-' + CAST(c2.IdC AS VARCHAR(MAX)) AS Path, 1 AS [Level]\n" +
            "    FROM Distance d1 \n" +
            "\tJOIN City c1 ON d1.IdC1 = c1.IdC \n" +
            "\tJOIN City c2 ON d1.IdC2 = c2.IdC\n" +
            "    \n" +
            "    UNION ALL\n" +
            "    \n" +
            "    SELECT c.CityFrom, d2.IdC2, (c.Distance + d2.[Days]) AS Distance,\n" +
            "           c.Path + '-' + CAST((c.Distance + d2.[Days]) AS VARCHAR(MAX))+ '-' + CAST(d2.IdC2 AS VARCHAR(MAX)), c.[Level] + 1\n" +
            "    FROM CTE c\n" +
            "    JOIN Distance d2 ON c.CityTo = d2.IdC1\n" +
            "    WHERE c.[Level] < 20\n" +
            ")\n" +
            "SELECT TOP 1 CityFrom, CityTo, MIN(Distance) AS MinDistance, Path AS ShortestPath\n" +
            "FROM CTE\n" +
            "WHERE (CityFrom = ? AND CityTo = ?) OR (CityFrom = ? AND CityTo = ?)\n" +
            "GROUP BY CityFrom, CityTo, Path\n" +
            "ORDER BY MinDistance ASC;";

    public static List<Node> findShortestPath(int cityIdFrom, int cityIdTo) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(shortestPathQuery)) {
            ps.setInt(1, cityIdFrom);
            ps.setInt(2, cityIdTo);
            ps.setInt(3, cityIdTo);
            ps.setInt(4, cityIdFrom);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String shortestPathString = rs.getString("ShortestPath");
                List<Node> shortestPath = convertStringToList(shortestPathString);
                return shortestPath;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int findMinDistance(int cityIdFrom, int cityIdTo) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(shortestPathQuery)) {
            ps.setInt(1, cityIdFrom);
            ps.setInt(2, cityIdTo);
            ps.setInt(3, cityIdTo);
            ps.setInt(4, cityIdFrom);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int minDistance = rs.getInt("MinDistance");
                return minDistance;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

}
