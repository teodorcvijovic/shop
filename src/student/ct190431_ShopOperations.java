/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.ShopOperations;

import javax.xml.transform.Result;
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
//            e.printStackTrace();
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
//            e.printStackTrace();
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
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int setCity(int shopId, String cityName) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "update Shop set IdC = (select IdC from City where Name=?) where IdS=?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cityName);
            stmt.setInt(2, shopId);

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
    public int getCity(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "select IdC from Shop where IdS = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shopId);

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
    public int setDiscount(int shopId, int discount) {
        if (discount < 0 || discount > 100) {
            return -1;
        }

        Connection conn = DB.getInstance().getConnection();
        String sql = "update Shop set Discount = ? where IdS=?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discount);
            stmt.setInt(2, shopId);

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
    public int increaseArticleCount(int articleId, int increment) {
        if (increment < 0) {
            return -1;
        }
        Connection conn = DB.getInstance().getConnection();
        String sql = "update Article set AvailableCount = AvailableCount + ? where IdA = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, increment);
            stmt.setInt(2, articleId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                return -1;
            }

        } catch (SQLException e) {
            return -1;
        }

        String sql2 = "select AvailableCount from Article where IdA = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql2)) {
            stmt.setInt(1, articleId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int numOfArticles = rs.getInt("AvailableCount");
                return numOfArticles;
            }

        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getArticleCount(int articleId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "select AvailableCount from Article where IdA = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, articleId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("AvailableCount");
                return count;
            }

        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Integer> getArticles(int shopId) {
        List<Integer> articleIds = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT IdA FROM Article WHERE IdS = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shopId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int articleId = resultSet.getInt("IdA");
                articleIds.add(articleId);
            }

            return articleIds;
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int getDiscount(int shopId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "select Discount from Shop where IdS = ?";

        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, shopId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int discount = rs.getInt("Discount");
                return discount;
            }

        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }
    
}
