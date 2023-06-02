/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.util.List;
import rs.etf.sab.operations.CityOperations;

import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Teodor
 */
public class ct190431_CityOperations implements CityOperations {

    @Override
    public int createCity(String name) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "insert into City (Name) values (?)";
        
        try (PreparedStatement statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, name);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    return generatedId;
                }
            }
        } catch(com.microsoft.sqlserver.jdbc.SQLServerException e) {
//            e.printStackTrace();
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Integer> getCities() {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> cityIds = new ArrayList<>();
        String sql = "select IdC from City";

        try (Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int cityId = resultSet.getInt("IdC");
                cityIds.add(cityId);
            }
            
            return cityIds;
        } catch (SQLException e) {
//          e.printStackTrace();
        }

        return null;
    }

    @Override
    public int connectCities(int cityId1, int cityId2, int distance) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "INSERT INTO Distance (IdC1, IdC2, Days) VALUES (?, ?, ?), (?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, cityId1);
            statement.setInt(2, cityId2);
            statement.setInt(3, distance);

            statement.setInt(4, cityId2);
            statement.setInt(5, cityId1);
            statement.setInt(6, distance);
            return statement.executeUpdate();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Integer> getConnectedCities(int cityId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> connectedCities = new ArrayList<>();
        String sql =    "select IdC1  as 'id' from Distance where IdC2 = ?\n" +
                        "union\n" +
                        "select IdC2 as 'id' from Distance where IdC1 = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, cityId);
            statement.setInt(2, cityId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int connectedCityId = resultSet.getInt("id");
                    connectedCities.add(connectedCityId);
                }
            }
            
            return connectedCities;
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Integer> getShops(int cityId) {
        List<Integer> shopIds = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();

        String sql = "SELECT IdS FROM Shop WHERE IdC = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cityId);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int shopId = resultSet.getInt("IdS");
                shopIds.add(shopId);
            }

            return shopIds;
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return null;
    }
    
}
