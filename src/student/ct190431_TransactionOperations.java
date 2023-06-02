/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import rs.etf.sab.operations.TransactionOperations;

/**
 *
 * @author Teodor
 */
public class ct190431_TransactionOperations implements TransactionOperations {

    @Override
    public BigDecimal getBuyerTransactionsAmmount(int buyerId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT coalesce(SUM(Amount),0) AS TotalAmount FROM [Transaction] WHERE IdC = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, buyerId);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return set.getBigDecimal("TotalAmount");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return new BigDecimal(-1);
    }

    @Override
    public BigDecimal getShopTransactionsAmmount(int shopId) {
        return this.getBuyerTransactionsAmmount(shopId);
    }

    @Override
    public List<Integer> getTransationsForBuyer(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT IdT FROM [Transaction] WHERE IdC = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, buyerId);

            ResultSet rs = stmt.executeQuery();

            List<Integer> transactionIds = new ArrayList<>();
            while (rs.next()) {
                transactionIds.add(rs.getInt("IdT"));
            }

            return transactionIds.size() > 0 ? transactionIds : null;
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getTransactionForBuyersOrder(int orderId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT IdT FROM Transaction WHERE IdO = ? and IdC = (select IdB from Order where IdO = ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, orderId);
            stmt.setInt(1, orderId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int transactionId = rs.getInt("IdT");
                return transactionId;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getTransactionForShopAndOrder(int orderId, int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT IdT FROM [Transaction] WHERE IdC = ? AND IdO = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, shopId);
            stmt.setInt(2, orderId);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return set.getInt("IdT");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Integer> getTransationsForShop(int shopId) {
        return this.getTransationsForBuyer(shopId);
    }

    @Override
    public Calendar getTimeOfExecution(int transactionId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT ExecutionTime FROM [Transaction] WHERE IdT = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, transactionId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (rs.getDate("ExecutionTime") != null) {
                    Calendar time = Calendar.getInstance();
                    long milis = rs.getTimestamp("ExecutionTime").getTime();
                    time.setTimeInMillis(milis);
                    return time;
                }
            }

        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BigDecimal getAmmountThatBuyerPayedForOrder(int orderId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT Amount FROM [Transaction] WHERE IdC in (select IdB from Buyer) AND IdO = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);)
        {
            pstmt.setInt(1, orderId);

            ResultSet set = pstmt.executeQuery();

            if (set.next()) {
                return set.getBigDecimal("Amount");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return new BigDecimal(-1);
    }

    @Override
    public BigDecimal getAmmountThatShopRecievedForOrder(int shopId, int orderId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT Amount FROM [Transaction] WHERE IdO = ? AND IdC = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, shopId);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return set.getBigDecimal("Amount");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return new BigDecimal(-1);
    }

    @Override
    public BigDecimal getTransactionAmount(int transactionId) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT Amount FROM [Transaction] WHERE IdT = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            stmt.setInt(1, transactionId);

            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                return set.getBigDecimal("Amount");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return new BigDecimal(-1);
    }

    @Override
    public BigDecimal getSystemProfit() {
        Connection conn = DB.getInstance().getConnection();
        BigDecimal shopAmount = new BigDecimal(0);
        BigDecimal buyersAmount;

        String sql = "SELECT coalesce(SUM(Amount), 0) AS ShopProfit FROM [Transaction] WHERE IdC in (select IdS from Shop)";

        try (PreparedStatement stmt = conn.prepareStatement(sql);)
        {
            ResultSet set = stmt.executeQuery();

            if (set.next()) {
                shopAmount = set.getBigDecimal("ShopProfit");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "SELECT coalesce(SUM(Amount), 0) AS BuyerProfit FROM [Transaction] WHERE IdC in (select IdB from Buyer) AND IsExecuted > 0";
        try (PreparedStatement stmt = conn.prepareStatement(sql))
        {
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                buyersAmount = rs.getBigDecimal("BuyerProfit");
                BigDecimal sysProfit = new BigDecimal(buyersAmount.longValue() - shopAmount.longValue()).setScale(3);
                return sysProfit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new BigDecimal(-1);
    }
    
}
