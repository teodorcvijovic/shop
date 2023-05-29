package student;

import student.*;
import rs.etf.sab.operations.*;
import org.junit.Test;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.util.Calendar;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new ct190431_ArticleOperations(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new ct190431_BuyerOperations();
        CityOperations cityOperations = new ct190431_CityOperations();
        GeneralOperations generalOperations = new ct190431_GeneralOperations();
        OrderOperations orderOperations = new ct190431_OrderOperations();
        ShopOperations shopOperations = new ct190431_ShopOperations();
        TransactionOperations transactionOperations = new ct190431_TransactionOperations();

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
}
