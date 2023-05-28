/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.util.List;
import rs.etf.sab.operations.ShopOperations;
import java.sql.*;

/**
 *
 * @author Teodor
 */
public class ct190431_ShopOperations implements ShopOperations {

    private int createClient() {
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
            e.printStackTrace();
        }
        return -1;
    }

    private void deleteClient(int clientId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "DELETE FROM Client WHERE IdC = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, clientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int createShop(String name, String cityName) {
        Connection conn = DB.getInstance().getConnection();

        int clientId = this.createClient();
        if (clientId == -1) {
            return -1;
        }
        String sql = "INSERT INTO Shop (IdS, Name, IdC) VALUES (?, ?, (SELECT IdC FROM City WHERE Name = ?))";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, clientId);
            stmt.setString(2, name);
            stmt.setString(3, cityName);

            stmt.executeUpdate();

            return clientId;
        } catch (SQLException e) {
            deleteClient(clientId);
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int setCity(int i, String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getCity(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int setDiscount(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int increaseArticleCount(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getArticleCount(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Integer> getArticles(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getDiscount(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
