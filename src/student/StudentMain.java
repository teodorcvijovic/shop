package student;

import org.junit.Assert;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class StudentMain {

    public static void main(String[] args) {
        boolean runPublicTests = true;
        boolean runMyTest1 = false;
        boolean runMyTest2 = false;

        ArticleOperations articleOperations = new ct190431_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new ct190431_BuyerOperations();
        CityOperations cityOperations = new ct190431_CityOperations();
        GeneralOperations generalOperations = new ct190431_GeneralOperations();
        OrderOperations orderOperations = new ct190431_OrderOperations();
        ShopOperations shopOperations = new ct190431_ShopOperations();
        TransactionOperations transactionOperations = new ct190431_TransactionOperations();

        CityGraph.orderOperations = orderOperations;
        CityGraph.generalOperations = generalOperations;

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

        if (runMyTest1) {
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
            System.out.println("City: " + orderOperations.getLocation(order));

//            generalOperations.eraseAll();
        }

        if (runMyTest2) {
            generalOperations.eraseAll();

            Calendar initialTime = Calendar.getInstance();
            initialTime.clear();
            initialTime.set(2018, 0, 1);
            generalOperations.setInitialTime(initialTime);
            Calendar receivedTime = Calendar.getInstance();
            receivedTime.clear();
            receivedTime.set(2018, 0, 22);
            int cityB = cityOperations.createCity("B");
            int cityC1 = cityOperations.createCity("C1");
            int cityA = cityOperations.createCity("A");
            int cityC2 = cityOperations.createCity("C2");
            int cityC3 = cityOperations.createCity("C3");
            int cityC4 = cityOperations.createCity("C4");
            int cityC5 = cityOperations.createCity("C5");
            cityOperations.connectCities(cityB, cityC1, 8);
            cityOperations.connectCities(cityC1, cityA, 10);
            cityOperations.connectCities(cityA, cityC2, 3);
            cityOperations.connectCities(cityC2, cityC3, 2);
            cityOperations.connectCities(cityC3, cityC4, 1);
            cityOperations.connectCities(cityC4, cityA, 3);
            cityOperations.connectCities(cityA, cityC5, 15);
            cityOperations.connectCities(cityC5, cityB, 2);

            int shopA = shopOperations.createShop("shopA", "A");
            int shopC2 = shopOperations.createShop("shopC2", "C2");
            int shopC3 = shopOperations.createShop("shopC3", "C3");
            shopOperations.setDiscount(shopA, 20);
            shopOperations.setDiscount(shopC2, 50);

            int laptop = articleOperations.createArticle(shopA, "laptop", 1000);
            int monitor = articleOperations.createArticle(shopC2, "monitor", 200);
            int stolica = articleOperations.createArticle(shopC3, "stolica", 100);
            int sto = articleOperations.createArticle(shopC3, "sto", 200);

            shopOperations.increaseArticleCount(laptop, 10);
            shopOperations.increaseArticleCount(monitor, 10);
            shopOperations.increaseArticleCount(stolica, 10);
            shopOperations.increaseArticleCount(sto, 10);

            int buyer = buyerOperations.createBuyer("kupac", cityB);
            buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));

            int order = buyerOperations.createOrder(buyer);
            orderOperations.addArticle(order, laptop, 5);
            orderOperations.addArticle(order, monitor, 4);
            orderOperations.addArticle(order, stolica, 10);
            orderOperations.addArticle(order, sto, 4);

            Assert.assertNull(orderOperations.getSentTime(order));
            Assert.assertTrue("created".equals(orderOperations.getState(order)));

            orderOperations.completeOrder(order);
            Assert.assertTrue("sent".equals(orderOperations.getState(order)));

//            int buyerTransactionId = (Integer) transactionOperations.getTransationsForBuyer(buyer).get(0);
//            Assert.assertEquals(initialTime, transactionOperations.getTimeOfExecution(buyerTransactionId));
//            Assert.assertNull(transactionOperations.getTransationsForShop(shopA));

            BigDecimal shopAAmount = (new BigDecimal("5")).multiply(new BigDecimal("1000")).setScale(3);
            BigDecimal shopAAmountWithDiscount = (new BigDecimal("0.8")).multiply(shopAAmount).setScale(3);
            BigDecimal shopC2Amount = (new BigDecimal("4")).multiply(new BigDecimal("200")).setScale(3);
            BigDecimal shopC2AmountWithDiscount = (new BigDecimal("0.5")).multiply(shopC2Amount).setScale(3);
            BigDecimal shopC3Amount = (new BigDecimal("10")).multiply(new BigDecimal("100")).add((new BigDecimal("4")).multiply(new BigDecimal("200"))).setScale(3);
            BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3);
            BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3Amount).setScale(3);
            BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3);
            BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3);
            BigDecimal shopC3AmountReal = shopC3Amount.multiply(new BigDecimal("0.95")).setScale(3);

            Assert.assertEquals(amountWithDiscounts, orderOperations.getFinalPrice(order));
            Assert.assertEquals(amountWithoutDiscounts.subtract(amountWithDiscounts), orderOperations.getDiscountSum(order));
            Assert.assertEquals(amountWithDiscounts, transactionOperations.getBuyerTransactionsAmmount(buyer));
            Assert.assertEquals(transactionOperations.getShopTransactionsAmmount(shopA), (new BigDecimal("0")).setScale(3));
            Assert.assertEquals(transactionOperations.getShopTransactionsAmmount(shopC2), (new BigDecimal("0")).setScale(3));
            Assert.assertEquals(transactionOperations.getShopTransactionsAmmount(shopC3), (new BigDecimal("0")).setScale(3));
            Assert.assertEquals((new BigDecimal("0")).setScale(3), transactionOperations.getSystemProfit());

            generalOperations.time(2);
            Calendar time = orderOperations.getSentTime(order);
            Assert.assertEquals(initialTime, time);
            Assert.assertNull(orderOperations.getRecievedTime(order));
            Assert.assertEquals((long) orderOperations.getLocation(order), (long) cityA);

            generalOperations.time(9);
            Assert.assertEquals((long) orderOperations.getLocation(order), (long) cityA);

            generalOperations.time(8);
            Assert.assertEquals((long) orderOperations.getLocation(order), (long) cityC5);

            generalOperations.time(5);
            Assert.assertEquals((long) orderOperations.getLocation(order), (long) cityB);

            time = orderOperations.getRecievedTime(order);
            Assert.assertEquals(receivedTime, time);

            Assert.assertEquals(shopAAmountReal, transactionOperations.getShopTransactionsAmmount(shopA));
            Assert.assertEquals(shopC2AmountReal, transactionOperations.getShopTransactionsAmmount(shopC2));
            Assert.assertEquals(shopC3AmountReal, transactionOperations.getShopTransactionsAmmount(shopC3));
            Assert.assertEquals(systemProfit, transactionOperations.getSystemProfit());
            int shopATransactionId = transactionOperations.getTransactionForShopAndOrder(order, shopA);
            Assert.assertNotEquals(-1L, (long)shopATransactionId);
            Assert.assertEquals(receivedTime, transactionOperations.getTimeOfExecution(shopATransactionId));

//            generalOperations.eraseAll();
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
