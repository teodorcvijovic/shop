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
import java.util.HashMap;
import java.util.List;

public class StudentMain {

    public static void main(String[] args) {
        boolean runPublicTests = true;
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

    /************************* Client *******************************/

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
}
