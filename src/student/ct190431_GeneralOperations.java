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
        // TO DO: update cityId in order and write a trigger for transaction creation

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

    public static int getTotalDaysBetweenDates(Calendar startDate, Calendar endDate) {
        // Set the time of both calendars to the start and end of their respective days
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);

        // Calculate the difference in milliseconds between the two dates
        long startTimeInMillis = startDate.getTimeInMillis();
        long endTimeInMillis = endDate.getTimeInMillis();
        long differenceInMillis = endTimeInMillis - startTimeInMillis;

        // Calculate the number of days by dividing the difference by the number of milliseconds in a day
        int daysBetween = (int) (differenceInMillis / (24 * 60 * 60 * 1000));

        return daysBetween;
    }
    
}
