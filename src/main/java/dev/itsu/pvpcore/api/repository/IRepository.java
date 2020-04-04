package dev.itsu.pvpcore.api.repository;

public interface IRepository {
    void connect();
    void disConnect();
    boolean exists(String name);
}
