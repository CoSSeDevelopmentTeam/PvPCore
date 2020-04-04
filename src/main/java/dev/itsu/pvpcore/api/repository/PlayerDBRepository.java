package dev.itsu.pvpcore.api.repository;

import java.sql.*;

public class PlayerDBRepository implements IRepository {

    private Connection connection;

    protected PlayerDBRepository() {
        connect();
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
                            "winsCount INTEGER NOT NULL," +
                            "matchesCount INTEGER NOT NULL )"
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
