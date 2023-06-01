/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import rs.etf.sab.operations.OrderOperations;
import java.sql.*;

/**
 *
 * @author Teodor
 */
public class ct190431_OrderOperations implements OrderOperations {

    @Override
    public int addArticle(int orderId, int articleId, int increment) {
        Connection conn = DB.getInstance().getConnection();

        // check if there is enough articles in shop
        String sql = "select AvailableCount from Article where IdA = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, articleId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int availableCount = rs.getInt("AvailableCount");
                if (increment > availableCount) {
                    return -1;
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            return -1;
        }

        // check if item exists in order
        sql = "select IdI from Item where IdA = ? and IdO = ?";
        int itemId = -1;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, articleId);
            ps.setInt(2, orderId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                itemId = rs.getInt("IdI");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        // create item if does not exist
        if (itemId == -1) {
            sql = "insert into Item (IdA, IdO) values (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, articleId);
                ps.setInt(2, orderId);

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    itemId = rs.getInt(1);
                }
            } catch (SQLException e) {
//                e.printStackTrace();
            }
        }
        if (itemId == -1) {
            return -1;
        }

        // increment item count
        sql = "update Item set Count = Count + ? where IdI = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, increment);
            ps.setInt(2, itemId);

            ps.executeUpdate();

            return itemId;
        } catch (SQLException e) {
//          e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int removeArticle(int orderId, int articleId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "select Count from Item where IdO = ? and IdA = ?";
        int count = 0;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            statement.setInt(2, articleId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                count = rs.getInt("Count");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
            return -1;
        }

        sql = "DELETE FROM Item WHERE IdO = ? AND IdA = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            statement.setInt(2, articleId);

            statement.executeUpdate();

            sql = "update Article set AvailableCount = AvailableCount + ? where IdA = ?";
            try(PreparedStatement statement2 = conn.prepareStatement(sql)) {
                statement2.setInt(1, count);
                statement2.setInt(2, articleId);

                statement2.executeUpdate();

                return 1;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Integer> getItems(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> itemIds = new ArrayList<>();
        String sql = "SELECT IdI FROM Item WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int itemId = resultSet.getInt("IdA");
                itemIds.add(itemId);
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return itemIds;
    }

    @Override
    public int completeOrder(int orderId) {
        /****************** update order ******************/

        Connection conn = DB.getInstance().getConnection();
        String sql = "update [Order] set State = 'sent', SentTime = getdate() where IdO = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return -1;
            }
        } catch (SQLException e) {
//          e.printStackTrace();
            return -1;
        }

        /******** called SP_FINAL_PRICE to calculate FinalPrice and DiscountSum *************/

        sql = "{ call SP_FINAL_PRICE(?) }";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, orderId);
            stmt.execute();
        } catch (SQLException e) {
//          e.printStackTrace();
            return -1;
        }

        /********* create buyers transaction *************/

        // TO DO

        /****** location is now the city with shop closest to buyer *******/

        // get cityIds of cities with shops

        // calculate distances

        // save cityId with min distance + remember that path

        /*********** for every Item's Shop in Order calculate min distanceFromA and save max(distanceFromA) in Order ***************/

        // get cityIds of items' shops

        // calculate min distanceFromA from each cityId

        // save max(distanceFromA) in order

        return -1;
    }

    @Override
    public BigDecimal getFinalPrice(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT FinalPrice FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("FinalPrice");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return new BigDecimal(-1);
    }

    @Override
    public BigDecimal getDiscountSum(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT DiscountSum FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("DiscountSum");
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return new BigDecimal(-1);
    }

    @Override
    public String getState(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT State FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String state = resultSet.getString("State");
                return state;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Calendar getSentTime(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT SentTime FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp sentTimestamp = resultSet.getTimestamp("SentTime");
                if (sentTimestamp != null) {
                    Calendar sentTime = Calendar.getInstance();
                    sentTime.setTimeInMillis(sentTimestamp.getTime());
                    return sentTime;
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Calendar getRecievedTime(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT ReceiveTime FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Timestamp receivedTimestamp = resultSet.getTimestamp("ReceiveTime");
                if (receivedTimestamp != null) {
                    Calendar receivedTime = Calendar.getInstance();
                    receivedTime.setTimeInMillis(receivedTimestamp.getTime());
                    return receivedTime;
                }
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getBuyer(int orderId) {
        Connection conn = DB.getInstance().getConnection();
        String sql = "SELECT IdB FROM [Order] WHERE IdO = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int buyerId = resultSet.getInt("IdB");
                return buyerId;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getLocation(int orderId) {
        String sql = "select IdC from [Order] where IdO = ?";
        Connection conn = DB.getInstance().getConnection();

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, orderId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int cityId = resultSet.getInt("IdC");
                return cityId;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return -1;
    }
    
}
