/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import java.sql.*;
import java.util.Calendar;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Teodor
 */
public class ct190431_GeneralOperations implements GeneralOperations {

    @Override
    public void setInitialTime(Calendar clndr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Calendar time(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Calendar getCurrentTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eraseAll() {
        Connection connection = DB.getInstance().getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM [Transaction]");
            statement.executeUpdate("DELETE FROM [Basket]");
            statement.executeUpdate("DELETE FROM [Article]");
            statement.executeUpdate("DELETE FROM [Shop]");
            statement.executeUpdate("DELETE FROM [Order]");
            statement.executeUpdate("DELETE FROM [Buyer]");
            statement.executeUpdate("DELETE FROM [Client]");
            statement.executeUpdate("DELETE FROM [Distance]");
            statement.executeUpdate("DELETE FROM [City]");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
