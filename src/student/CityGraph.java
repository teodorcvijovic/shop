package student;

import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.OrderOperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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

    // order needs to be activated - in completeOrder method

    public static OrderOperations orderOperations = null;
    public static GeneralOperations generalOperations = null;

    public static int getMaxDistanceFromA(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT MaxDistanceFromA FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int maxDistanceFromA = resultSet.getInt("MaxDistanceFromA");
                return maxDistanceFromA;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return -1;
    }

    public static void setCityInOrder(int orderId, int cityId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "update [Order] set IdC = ? where IdO = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cityId);
            ps.setInt(2, orderId);

            ps.executeUpdate();

        } catch (SQLException e) {
//          e.printStackTrace();
        }
    }

    // invoked in time func
    public static void updateActiveOrders(Calendar currentTime) {
        for (Integer orderId: activeOrders.keySet()) {
            int fromSendTime = ct190431_GeneralOperations.getTotalDaysBetweenDates(orderOperations.getSentTime(orderId), currentTime);
            int daysLeftForTravel = fromSendTime - getMaxDistanceFromA(orderId);
            int daysForTravel = -1;
            int currCityId = orderOperations.getLocation(orderId);
            int newCityId = -1;

            List<Node> path = activeOrders.get(orderId);

            int cnt = 0;
            for(Node cityNode: path) {
                int cityId = cityNode.cityId;
                int distance = cityNode.distance;

                if (daysLeftForTravel < distance) {
                    break;
                }

                newCityId = cityId;
                daysForTravel = distance;
                cnt++;
            }

            if (currCityId == newCityId) return;
            // set new city id
            setCityInOrder(orderId, newCityId);

            // order arrived to buyer's city
            if (cnt == path.size()) {
                // deactivate order
                activeOrders.remove(orderId);

                Connection conn = DB.getInstance().getConnection();
                String sql = "update [Order] set State = 'arrived', ReceiveTime = ? where IdO = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    java.util.Date utilDate = orderOperations.getSentTime(orderId).getTime();
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

                    long updatedTime = sqlDate.getTime() + (getMaxDistanceFromA(orderId) * 24L * 60L * 60L * 1000L);
                    sqlDate.setTime(updatedTime);

                    updatedTime = sqlDate.getTime() + (daysForTravel * 24L * 60L * 60L * 1000L);
                    sqlDate.setTime(updatedTime);

                    ps.setDate(1, sqlDate);
                    ps.setInt(2, orderId);

                    ps.executeUpdate();
                } catch (SQLException e) {
//                  e.printStackTrace();
                }
            }

        }
    }

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
            "    FROM Distance d1\n" +
            "    JOIN City c1 ON d1.IdC1 = c1.IdC\n" +
            "    JOIN City c2 ON d1.IdC2 = c2.IdC\n" +
            "    \n" +
            "    UNION ALL\n" +
            "    \n" +
            "\tSELECT c.CityFrom, d2.IdC2, (c.Distance + d2.[Days]) AS Distance,\n" +
            "           c.Path + '-' + CAST((c.Distance + d2.[Days]) AS VARCHAR(MAX))+ '-' + CAST(d2.IdC2 AS VARCHAR(MAX)), c.[Level] + 1\n" +
            "    FROM CTE c\n" +
            "    JOIN Distance d2 ON (c.CityTo = d2.IdC1 OR c.CityTo = d2.IdC2 OR c.CityFrom = d2.IdC1 OR c.CityFrom = d2.IdC2)\n" +
            "    WHERE c.[Level] < 20 AND c.Path NOT LIKE '%' + CAST(d2.IdC2 AS VARCHAR(MAX)) + '%'\n" +
            "\n" +
            ")\n" +
            "SELECT CityFrom, CityTo, MIN(Distance) AS MinDistance, Path AS ShortestPath\n" +
            "FROM CTE\n" +
            "WHERE (CityFrom = ? AND CityTo = ?)\n" +
            "GROUP BY CityFrom, CityTo, Path\n" +
            "ORDER BY MinDistance ASC;";

    public static List<Node> findShortestPath(int cityIdFrom, int cityIdTo) {
        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(shortestPathQuery)) {
            ps.setInt(1, cityIdFrom);
            ps.setInt(2, cityIdTo);
//            ps.setInt(3, cityIdTo);
//            ps.setInt(4, cityIdFrom);

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
        if (cityIdFrom == cityIdTo) return 0;

        Connection connection = DB.getInstance().getConnection();
        try (PreparedStatement ps = connection.prepareStatement(shortestPathQuery)) {
            ps.setInt(1, cityIdFrom);
            ps.setInt(2, cityIdTo);
//            ps.setInt(3, cityIdTo);
//            ps.setInt(4, cityIdFrom);

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
