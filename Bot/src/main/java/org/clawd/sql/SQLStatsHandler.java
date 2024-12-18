package org.clawd.sql;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.data.inventory.UserStats;
import org.clawd.main.Bot;
import org.clawd.main.Main;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLStatsHandler {

    /**
     * Fetches all user stats (minedCount, xpCount, goldCount, mobKills, bossKills) in one query for improved efficiency.
     *
     * @param userID The user ID
     * @return       A UserStats object containing all relevant stats
     */
    public UserStats getUserStats(String userID) {
        UserStats userStats = new UserStats();
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "SELECT minedCount, xpCount, goldCount, mobKills, bossKills FROM playertable WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userStats.setUserID(userID);
                userStats.setMinedCount(resultSet.getInt("minedCount"));
                userStats.setXpCount(Main.generator.transformDouble(resultSet.getDouble("xpCount"))); // Fix precision issue
                userStats.setGoldCount(resultSet.getInt("goldCount"));
                userStats.setMobKills(resultSet.getInt("mobKills"));
                userStats.setBossKills(resultSet.getInt("bossKills"));
                Main.LOG.info("Retrieved all stats for user: " + userID);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException ex) {
            Main.LOG.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return userStats;
    }

    /**
     * Updates a users XP, gold and mob kill count
     *
     * @param userID the users ID
     * @param gold   negative or positive gold amount
     * @param xp     positive XP amount
     */
    public void updateUserStatsAfterKill(String userID, int gold, double xp) {
        UserStats currentStats = this.getUserStats(userID);

        int updatedMobKills = currentStats.getMobKills() + 1;
        int updatedGoldCount = currentStats.getGoldCount() + gold;
        double updatedXPCount = Main.generator.transformDouble(currentStats.getXpCount() + xp);  // Fix precision issue

        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "UPDATE playertable SET mobKills = ?, goldCount = ?, xpCount = ? WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, updatedMobKills);
            preparedStatement.setInt(2, updatedGoldCount);
            preparedStatement.setDouble(3, updatedXPCount);
            preparedStatement.setString(4, userID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                Main.LOG.info("Updated stats for user: " + userID + ". New mob kills: " + updatedMobKills +
                        ", New gold count: " + updatedGoldCount + ", New XP count: " + updatedXPCount);
            } else {
                Main.LOG.warning("Failed to update stats for user: " + userID);
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            Main.LOG.severe("Some SQL error occurred: " + ex.getMessage());
        }
    }

    /**
     * Updates a users XP, gold and mine count
     *
     * @param userID the user ID
     * @param xp     positive XP amount
     * @param gold   negative or positive gold amount
     */
    public void updateUserStatsAfterMining(String userID, double xp, int gold) {
        UserStats currentStats = this.getUserStats(userID);

        int updatedMineCount = currentStats.getMinedCount() + 1;
        double updatedXPCount = Main.generator.transformDouble(currentStats.getXpCount() + xp);
        int updatedGoldCount = currentStats.getGoldCount() + gold;

        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "UPDATE playertable SET minedCount = ?, xpCount = ?, goldCount = ? WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setInt(1, updatedMineCount);
            preparedStatement.setDouble(2, updatedXPCount);
            preparedStatement.setInt(3, updatedGoldCount);
            preparedStatement.setString(4, userID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                Main.LOG.info("Updated stats for user: " + userID + ". New mine count: " + updatedMineCount +
                        ", New XP count: " + updatedXPCount + ", New gold count: " + updatedGoldCount);
            } else {
                Main.LOG.warning("Failed to update stats for user: " + userID);
            }

            preparedStatement.close();
        } catch (SQLException ex) {
            Main.LOG.severe("Some SQL error occurred: " + ex.getMessage());
        }
    }

    /**
     * Fetches a users XP count
     *
     * @param userID the user ID
     * @return       the users XP
     */
    public double getXPCountFromUser(String userID) {
        double xpCount = 0.0;
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "SELECT xpCount FROM playertable WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double retrievedXP = resultSet.getDouble("xpCount");
                // Call to transformDouble() to hopefully fix 'precision issue' where doubles have to
                // many decimal places
                xpCount = Main.generator.transformDouble(retrievedXP);
                Main.LOG.info("Retrieved the total XP amount: " + xpCount + ". From user:" + userID);
            }

        } catch (SQLException ex) {
            Main.LOG.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return xpCount;
    }

    /**
     * Fetches a users gold count
     *
     * @param userID the user ID
     * @return       the users gold count
     */
    public int getGoldCountFromUser(String userID) {
        int goldCount = 0;
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "SELECT goldCount FROM playertable WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                goldCount = resultSet.getInt("goldCount");
                Main.LOG.info("Retrieved the total gold amount: " + goldCount + ". From user:" + userID);
            }

        } catch (SQLException ex) {
            Main.LOG.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return goldCount;
    }

    /**
     * Changes a users gold count
     *
     * @param userID the user ID
     * @param gold   negative or positive gold amount
     */
    public void changeGoldCount(String userID, int gold) {
        int currentGold = this.getGoldCountFromUser(userID);
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "UPDATE playertable SET goldCount = ? WHERE userID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            int newCount = currentGold + gold;

            preparedStatement.setInt(1, newCount);
            preparedStatement.setString(2, userID);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                Main.LOG.info("Updated gold count for user: " + userID + ". New gold count: " + newCount);
            } else {
                Main.LOG.warning("Failed to update gold count for user " + userID);
            }
        } catch (SQLException ex) {
            Main.LOG.severe("Some SQL error occurred: " + ex.getMessage());
        }
    }

    /**
     * This method creates and sends a message in case of leveling up
     *
     * @param userCurrentXP the users current xp
     * @param userUpdatedXP the users xp after updating stats
     * @param event         the corresponding button interaction event
     */
    public void replyToUserLevelUp(double userCurrentXP, double userUpdatedXP, ButtonInteractionEvent event) {
        int userCurrentLvl = Main.generator.computeLevel(userCurrentXP);
        int userUpdatedLvl = Main.generator.computeLevel(userUpdatedXP);

        if (userUpdatedLvl > userCurrentLvl) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(":mushroom: Level UP! :mushroom:");
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.addField(
                    "You've reached level " + userUpdatedLvl,
                    ":black_small_square: With **" + userUpdatedXP + "** XP",
                    false
            );
            event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }
}