package dev.itsu.pvpcore.api.repository;

import dev.itsu.pvpcore.model.Arena;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ArenaDBRepository implements IRepository {

    private Connection connection;

    public ArenaDBRepository() {
        connect();
    }

    public boolean create(String name, String owner, String description, String world, int x, int y, int z) {
        if (exists(name)) return false;
        try {
            String sql = "INSERT INTO arena (name, owner, description, world, x, y, z, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, name);
            statement.setString(2, owner);
            statement.setString(3, description);
            statement.setString(4, world);
            statement.setInt(5, x);
            statement.setInt(6, y);
            statement.setInt(7, z);
            statement.setInt(8, SQLBoolean.toInt(true));
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        if (getArenaById(id) == null) return false;
        try {
            String sql = "DELETE FROM arena WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setInt(1, id);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Arena arena) {
        if (!exists(arena.getName())) return false;
        try {
            String sql = "UPDATE arena SET name = ?, description = ?, world = ?, x = ?, y = ?, z = ?, status = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, arena.getName());
            statement.setString(2, arena.getDescription());
            statement.setString(3, arena.getWorld());
            statement.setInt(4, arena.getX());
            statement.setInt(5, arena.getY());
            statement.setInt(6, arena.getZ());
            statement.setInt(7, arena.getId());
            statement.setInt(8, arena.getStatus().toInt());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean updateAllStatus(Arena.Status status) {
        try {
            String sql = "UPDATE arena SET status = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setInt(1, status.toInt());
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Arena> getArenas() {
        try {
            String sql = "SELECT * FROM arena";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(100);

            ResultSet resultSet = statement.executeQuery();
            List<Arena> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(
                        new Arena(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("owner"),
                                resultSet.getString("description"),
                                resultSet.getString("world"),
                                resultSet.getInt("x"),
                                resultSet.getInt("y"),
                                resultSet.getInt("z"),
                                Arena.Status.fromInt(resultSet.getInt("status"))
                        )
                );
            }
            resultSet.close();
            statement.close();
            System.out.println(result.size());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Arena> getArenasByOwner(String owner) {
        try {
            String sql = "SELECT * FROM arena WHERE owner = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(100);
            statement.setString(1, owner);

            ResultSet resultSet = statement.executeQuery();
            List<Arena> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(
                        new Arena(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("owner"),
                                resultSet.getString("description"),
                                resultSet.getString("world"),
                                resultSet.getInt("x"),
                                resultSet.getInt("y"),
                                resultSet.getInt("z"),
                                Arena.Status.fromInt(resultSet.getInt("status"))
                        )
                );
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Arena> getNotUsedArenas() {
        try {
            String sql = "SELECT * FROM arena WHERE status = ? OR status = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(100);
            statement.setInt(1, Arena.Status.AVAILABLE.toInt());
            statement.setInt(2, Arena.Status.RESERVED.toInt());

            ResultSet resultSet = statement.executeQuery();
            List<Arena> result = new ArrayList<>();

            while (resultSet.next()) {
                result.add(
                        new Arena(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getString("owner"),
                                resultSet.getString("description"),
                                resultSet.getString("world"),
                                resultSet.getInt("x"),
                                resultSet.getInt("y"),
                                resultSet.getInt("z"),
                                Arena.Status.fromInt(resultSet.getInt("status"))
                        )
                );
            }
            resultSet.close();
            statement.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Arena getArenaById(int id) {
        Arena arena = null;
        try {
            String sql = "SELECT * FROM arena WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                arena = new Arena(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("owner"),
                        resultSet.getString("description"),
                        resultSet.getString("world"),
                        resultSet.getInt("x"),
                        resultSet.getInt("y"),
                        resultSet.getInt("z"),
                        Arena.Status.fromInt(resultSet.getInt("status"))
                );
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arena;
    }

    public Arena getArenaByName(String name) {
        Arena arena = null;
        try {
            String sql = "SELECT * FROM arena WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, name);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                arena = new Arena(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("owner"),
                        resultSet.getString("description"),
                        resultSet.getString("world"),
                        resultSet.getInt("x"),
                        resultSet.getInt("y"),
                        resultSet.getInt("z"),
                        Arena.Status.fromInt(resultSet.getInt("status"))
                );
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return arena;
    }

    public boolean exists(String name) {
        try {
            String sql = "SELECT * FROM arena WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setQueryTimeout(50);
            statement.setString(1, name);

            boolean result = statement.executeQuery().next();
            statement.close();

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/PVPCore/ArenaData.db");

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(50);
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS arena (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "name TEXT NOT NULL," +
                            "owner TEXT NOT NULL," +
                            "description TEXT NOT NULL," +
                            "world TEXT NOT NULL, " +
                            "x INTEGER NOT NULL," +
                            "y INTEGER NOT NULL," +
                            "z INTEGER NOT NULL," +
                            "status INTEGER NOT NULL" +
                            ")"
            );
            statement.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disConnect() {
        try {
            updateAllStatus(Arena.Status.AVAILABLE);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static class SQLBoolean {        
        public static int toInt(boolean b) {
            return b ? 0 : 1;
        }
        
        public static boolean toBoolean(int i) {
            return i == 0;
        }
    }
}
