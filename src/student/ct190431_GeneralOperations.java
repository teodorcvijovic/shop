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

    private Calendar time = null;

    @Override
    public void setInitialTime(Calendar time) {
        this.time = time;
    }

    @Override
    public Calendar time(int days) {
        this.time.add(Calendar.DAY_OF_MONTH, days);
        return this.time;
    }

    @Override
    public Calendar getCurrentTime() {
        return this.time;
    }

    @Override
    public void eraseAll() {
        Connection connection = DB.getInstance().getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM [Transaction]");
            statement.executeUpdate("DELETE FROM [Item]");
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
