package org.clawd.sql;

import org.clawd.main.Bot;
import org.clawd.main.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Holds basic methods for SQL actions
 */
public class SQLHandler {
    /**
     * This method returns if a user ID can be found inside the table.
     *
     * @param userID The user ID
     * @return True or false, depending on if the user ID was found or not.
     */
    public boolean isPlayerRegistered(String userID) {
        boolean registered = false;
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "SELECT * FROM playertable WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            registered = resultSet.next(); // If resultSet.next() is true, player is registered
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            Main.logger.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return registered;
    }

    /**
     * This method registers a user if they are not registered yet. The user id is
     * entered into the table, all other values are initialized with default values.
     *
     * @param userID The user ID
     */
    public void registerPlayer (String userID) {
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "INSERT INTO playertable (" +
                    "userID," +
                    " minedCount," +
                    " xpCount," +
                    " goldCount," +
                    " mobKills," +
                    " bossKills," +
                    " equipedItemID," +
                    " inventory" +
                    ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID); // UserID
            preparedStatement.setInt(2, 0); // Default minedCount
            preparedStatement.setInt(3, 0); // Default xpCount
            preparedStatement.setInt(4, 0); // Default goldCount
            preparedStatement.setInt(5, 0); // Default mobKills
            preparedStatement.setInt(6, 0); // Default bossKills
            preparedStatement.setInt(7, 0); // Default equipedItemID
            preparedStatement.setString(8, ""); // Default inventory state

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Main.logger.info("Registered user with ID: " + userID);
            } else {
                Main.logger.severe("Failed to register user. ");
            }
            preparedStatement.close();
        } catch (SQLException ex) {
            Main.logger.severe("Some SQL error occurred: " + ex.getMessage());
        }
    }
}
