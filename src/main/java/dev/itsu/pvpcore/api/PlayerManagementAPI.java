package dev.itsu.pvpcore.api;

import dev.itsu.pvpcore.api.repository.PlayerDBRepository;
import dev.itsu.pvpcore.model.PlayerStatus;

public class PlayerManagementAPI {
    private static final PlayerManagementAPI instance = new PlayerManagementAPI();
    private PlayerDBRepository repository;

    private PlayerManagementAPI() {
        repository = new PlayerDBRepository();
    }

    public boolean createPlayerStatus(String name, int level, int experienceLevel, int matchCount, int winCount) {
        return repository.create(name, level, experienceLevel, matchCount, winCount);
    }

    public boolean existsPlayerStatus(String name) {
        return repository.exists(name);
    }

    public boolean deletePlayerStatus(String name) {
        return repository.delete(name);
    }

    public boolean updateLevel(String name) {
        PlayerStatus status = getPlayerStatus(name);
        if (status == null) return false;
        return repository.update(
                new PlayerStatus(
                        name,
                        status.getLevel() + 1,
                        status.getExperienceLevel(),
                        status.getMatchCount(),
                        status.getWinCount()
                )
        );
    }

    public boolean setPlayerExp(String name, int value) {
        PlayerStatus status = getPlayerStatus(name);
        if (status == null) return false;
        return repository.update(
                new PlayerStatus(
                        name,
                        status.getLevel(),
                        value,
                        status.getMatchCount(),
                        status.getWinCount()
                )
        );
    }

    public boolean updateMatchCount(String name, int count) {
        PlayerStatus status = getPlayerStatus(name);
        if (status == null) return false;
        return repository.update(
                new PlayerStatus(
                        name,
                        status.getLevel(),
                        status.getExperienceLevel(),
                        status.getMatchCount() + count,
                        status.getWinCount()
                )
        );
    }

    public boolean updateWinCount(String name) {
        PlayerStatus status = getPlayerStatus(name);
        if (status == null) return false;
        return repository.update(
                new PlayerStatus(
                        name,
                        status.getLevel(),
                        status.getExperienceLevel(),
                        status.getMatchCount(),
                        status.getWinCount() + 1
                )
        );
    }

    public PlayerStatus getPlayerStatus(String name) {
        return repository.getPlayerStatus(name);
    }

    public void disconnect() {
        repository.disConnect();
    }
    public static PlayerManagementAPI getInstance() {
        return instance;
    }
}
