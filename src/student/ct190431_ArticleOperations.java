/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import rs.etf.sab.operations.ArticleOperations;
import java.sql.*;

/**
 *
 * @author Teodor
 */
public class ct190431_ArticleOperations implements ArticleOperations {

    @Override
    public int createArticle(int shopId, String articleName, int articlePrice) {
        Connection conn = DB.getInstance().getConnection();

        String sql = "INSERT INTO Article (Name, Price, IdS) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, articleName);
            stmt.setInt(2, articlePrice);
            stmt.setInt(3, shopId);

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int articleId = generatedKeys.getInt(1);
                return articleId;
            }
        } catch (SQLException e) {
//            e.printStackTrace();
        }

        return -1;
    }
    
}
