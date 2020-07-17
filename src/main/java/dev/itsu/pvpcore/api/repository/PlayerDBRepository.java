package dev.itsu.pvpcore.api.repository;

import dev.itsu.pvpcore.model.PlayerStatus;

import java.sql.*;

public class PlayerDBRepository implements IRepository {

    private Connection connection;

    public PlayerDBRepository() {
        connect();
    }

    public boolean create(String name, int level, int experienceLevel, int matchCount, int winCount) {
        if (exists(name)) return false;
        try {
            String sql = "INSERT INTO player (name, level, experienceLevel, matchesCount, winsCount) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, name);
            statement.setInt(2, level);
            statement.setInt(3, experienceLevel);
            statement.setInt(4, matchCount);
            statement.setInt(5, winCount);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String name) {
        if (!exists(name)) return false;
        try {
            String sql = "DELETE FROM player WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PlayerStatus status) {
        if (!exists(status.getName())) return false;
        try {
            String sql = "UPDATE player SET name = ?, level = ?, experienceLevel = ?, matchesCount = ?, winsCount = ? WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, status.getName());
            statement.setInt(2, status.getLevel());
            statement.setInt(3, status.getExperienceLevel());
            statement.setInt(4, status.getMatchCount());
            statement.setInt(5, status.getWinCount());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public PlayerStatus getPlayerStatus(String name) {
        PlayerStatus status = null;
        try {
            String sql = "SELECT * FROM player WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                status = new PlayerStatus(
                        resultSet.getString("name"),
                        resultSet.getInt("level"),
                        resultSet.getInt("exp"),
                        resultSet.getInt("matchesCount"),
                        resultSet.getInt("winsCount")
                );
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean exists(String name) {
        try {
            String sql = "SELECT player WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, name);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/PVPCore/PlayerData.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(50);
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "name TEXT NOT NULL," +
                            "level INTEGER NOT NULL," +
                            "exp INTEGER NOT NULL," +
                            "matchesCount INTEGER NOT NULL," +
                            "winsCount INTEGER NOT NULL )"
            );
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
