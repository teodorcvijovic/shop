package student;

import student.*;
import rs.etf.sab.operations.*;
import org.junit.Test;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StudentMain {


    public static void main(String[] args) {
        boolean runPublicTests = false;
        boolean runMyTest = true;

        ArticleOperations articleOperations = new ct190431_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new ct190431_BuyerOperations();
        CityOperations cityOperations = new ct190431_CityOperations();
        GeneralOperations generalOperations = new ct190431_GeneralOperations();
        OrderOperations orderOperations = new ct190431_OrderOperations();
        ShopOperations shopOperations = new ct190431_ShopOperations();
        TransactionOperations transactionOperations = new ct190431_TransactionOperations();

        if (runPublicTests) {
            TestHandler.createInstance(
                    articleOperations,
                    buyerOperations,
                    cityOperations,
                    generalOperations,
                    orderOperations,
                    shopOperations,
                    transactionOperations
            );

            TestRunner.runTests();
        }

        if (runMyTest) {
            generalOperations.eraseAll();

            int idA = cityOperations.createCity("A");
            int idB = cityOperations.createCity("B");
            int idC = cityOperations.createCity("C");
            int idD = cityOperations.createCity("D");
            int idE = cityOperations.createCity("E");
            int idF = cityOperations.createCity("F");
            cityOperations.connectCities(idA, idB, 6);
            cityOperations.connectCities(idA, idC, 7);
            cityOperations.connectCities(idA, idD, 3);
            cityOperations.connectCities(idD, idC, 1);
            cityOperations.connectCities(idD, idE, 2);
            cityOperations.connectCities(idD, idF, 5);

            int shopB = shopOperations.createShop("shopB", "B");
            int shopC = shopOperations.createShop("shopC", "C");
            int shopE = shopOperations.createShop("shopE", "E");
            shopOperations.setDiscount(shopE, 30);
            int laptop = articleOperations.createArticle(shopE, "laptop", 1000);
            int monitor = articleOperations.createArticle(shopE, "monitor", 200);
            int stolica = articleOperations.createArticle(shopC, "stolica", 100);
            int sto = articleOperations.createArticle(shopB, "sto", 200);
            shopOperations.increaseArticleCount(laptop, 10);
            shopOperations.increaseArticleCount(monitor, 10);
            shopOperations.increaseArticleCount(stolica, 15);
            shopOperations.increaseArticleCount(sto, 5);

            int buyer = buyerOperations.createBuyer("kupac", idA);
            buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
            int order = buyerOperations.createOrder(buyer);
            orderOperations.addArticle(order, laptop, 5);
            orderOperations.addArticle(order, monitor, 4);
            orderOperations.addArticle(order, stolica, 3);
            orderOperations.addArticle(order, sto, 4);

            orderOperations.completeOrder(order);
            System.out.println(orderOperations.getSentTime(order).getTime().toString());
//            System.out.println(orderOperations.getRecievedTime(order).toString());
            System.out.println("FinalPrice: " + orderOperations.getFinalPrice(order));
            System.out.println("DiscountSum: " + orderOperations.getDiscountSum(order));

            generalOperations.eraseAll();
        }
    }

    // helper methods

    static int createClient() {
        Connection conn = DB.getInstance().getConnection();
        String sql = "INSERT INTO Client DEFAULT VALUES";

        try (PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int clientId = generatedKeys.getInt(1);
                return clientId;
            }

        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return -1;
    }

    static void deleteClient(int clientId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "DELETE FROM Client WHERE IdC = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            statement.executeUpdate();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    public static class GraphNode {
        public int cityId = -1;
        public int distance = -1;
        public boolean hasShop = false;

        public GraphNode(int cityId, int distance) {
            this.cityId = cityId;
            this.distance = distance;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GraphNode graphNode = (GraphNode) o;

            return cityId == graphNode.cityId;
        }

        @Override
        public int hashCode() {
            return cityId;
        }
    }

    public static int findCityWithClosestShop(int buyersCityId) throws SQLException {
        // TO DO
        return -1;
    }

//    public static int findCityWithClosestShop(int buyersCityId) throws SQLException {
//        Connection conn = DB.getInstance().getConnection();
//
//        /************ check if buyers city has a shop ************/
//
//        String sql = "select * from Shop where IdC = ?";
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, buyersCityId);
//
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                return buyersCityId;
//            }
//        }
//
//        /******************* init BFS ***********************/
//
//        int closestShop = -1;
//
//        HashSet<Integer> visitedNoShop = new HashSet<>();
//        List<GraphNode> queue = new ArrayList<>();
//
//        GraphNode root = new GraphNode(buyersCityId, 0);
//        queue.add(root);
//
//        /***************** BFS loop **********************/
//
//        while (!queue.isEmpty()) {
//            int n = queue.size();
//            int min = Integer.MAX_VALUE;
//
//            for (int i = 0; i < n; i++) {
//                GraphNode node = queue.remove(0);
//                if (!node.hasShop) {
//                    visitedNoShop.add(node.cityId);
//                }
//
//                if (node.hasShop && node.distance < min) {
//                    min = node.distance;
//                    closestShop = node.cityId;
//                } else {
//                    int currCityId = node.cityId;
//                    int currCityDistance = node.distance;
//
//                    // find all city neigbours
//                    sql =   "select IdC1 as 'IdC', Days from Distance where IdC2 = ?\n" +
//                            "union\n" +
//                            "select IdC2 as 'IdC', Days from Distance where IdC1 = ?";
//                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                        ps.setInt(1, currCityId);
//                        ps.setInt(2, currCityId);
//
//                        ResultSet rs = ps.executeQuery();
//
//                        while (rs.next()) {
//                            int cityId = rs.getInt("IdC");
//                            int days = rs.getInt("Days");
//                            if (visitedNoShop.contains(cityId)) continue;
//                            GraphNode node1 = new GraphNode(cityId, currCityDistance + days);
//                            queue.add(node1);
//                        }
//                    }
//
//                    // find all city neighbours with shops
//                    sql =   "select IdC1 as 'IdC', Days\n" +
//                            "from Distance join Shop S on (S.IdC=IdC1)\n" +
//                            "where IdC2 = ?\n" +
//                            "union\n" +
//                            "select IdC2 as 'IdC', Days\n" +
//                            "from Distance join Shop S on (S.IdC=IdC2)\n" +
//                            "where IdC1 = ?";
//                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                        ps.setInt(1, currCityId);
//                        ps.setInt(2, currCityId);
//
//                        ResultSet rs = ps.executeQuery();
//
//                        while (rs.next()) {
//                            int cityId = rs.getInt("IdC");
//
//                            Optional<GraphNode> result = queue.stream()
//                                    .filter(element -> element.cityId == cityId)
//                                    .findFirst();
//
//                            if (result.isPresent()) {
//                                GraphNode node1 = result.get();
//                                node1.hasShop = true;
//                            }
//                        }
//                    }
//                } // end if
//            } // end for
//        } // end while
//
//        return closestShop;
//    }
}
