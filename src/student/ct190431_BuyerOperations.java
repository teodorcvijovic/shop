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
import java.util.List;
import rs.etf.sab.operations.BuyerOperations;

/**
 *
 * @author Teodor
 */
public class ct190431_BuyerOperations implements BuyerOperations {

    @Override
    public int createBuyer(String name, int cityId) {
        Connection conn = DB.getInstance().getConnection();

        int clientId = StudentMain.createClient();
        if (clientId == -1) {
            return -1;
        }
        String sql = "INSERT INTO Buyer (IdB, Name, IdC) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clientId);
            stmt.setString(2, name);
            stmt.setInt(3, cityId);

            stmt.executeUpdate();

            return clientId;
        } catch (SQLException e) {
            StudentMain.deleteClient(clientId);
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int setCity(int buyerId, int cityId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "update Buyer set IdC = ? where IdB=?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cityId);
            stmt.setInt(2, buyerId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                return -1;
            }

            return 1;
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getCity(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "select IdC from Buyer where IdB = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, buyerId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int cityId = rs.getInt("IdC");
                return cityId;
            }

        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public BigDecimal increaseCredit(int buyerId, BigDecimal increment) {
        Connection conn = DB.getInstance().getConnection();

        String selectSql = "SELECT Credit FROM Buyer WHERE IdB = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);) {
            selectStmt.setInt(1, buyerId);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                BigDecimal currentCredit = resultSet.getBigDecimal("Credit");
                BigDecimal newCredit = currentCredit.add(increment);

                String updateSql = "UPDATE Buyer SET Credit = ? WHERE IdB = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql);) {
                    updateStmt.setBigDecimal(1, newCredit);
                    updateStmt.setInt(2, buyerId);
                    updateStmt.executeUpdate();

                    return newCredit;
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int createOrder(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        String insertOrderSql = "INSERT INTO [Order] (IdB) VALUES (?)";

        try (PreparedStatement insertOrderStmt = conn.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS);){
            insertOrderStmt.setInt(1, buyerId);
            insertOrderStmt.executeUpdate();

            ResultSet generatedKeys = insertOrderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int orderId = generatedKeys.getInt(1);
                return orderId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Integer> getOrders(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> orderIds = new ArrayList<>();

        String selectOrdersSql = "SELECT IdO FROM [Order] WHERE IdB = ?";
        try (PreparedStatement selectOrdersStmt = conn.prepareStatement(selectOrdersSql);) {
            selectOrdersStmt.setInt(1, buyerId);
            ResultSet resultSet = selectOrdersStmt.executeQuery();

            while (resultSet.next()) {
                int orderId = resultSet.getInt("IdO");
                orderIds.add(orderId);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return orderIds;
    }

    @Override
    public BigDecimal getCredit(int buyerId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT Credit FROM Buyer WHERE IdB = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, buyerId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                BigDecimal credit = resultSet.getBigDecimal("Credit");
                return credit;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return null;
    }
    
}
