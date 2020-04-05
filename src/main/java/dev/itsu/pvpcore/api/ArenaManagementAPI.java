package dev.itsu.pvpcore.api;

import cn.nukkit.level.Location;
import dev.itsu.pvpcore.api.repository.ArenaDBRepository;
import dev.itsu.pvpcore.model.Arena;

import java.util.List;

public class ArenaManagementAPI {

    private static ArenaManagementAPI instance = new ArenaManagementAPI();
    private ArenaDBRepository repository;

    private ArenaManagementAPI() {
        repository = new ArenaDBRepository();
    }

    public boolean createArena(String name, String owner, String description, String world, int x, int y, int z) {
        if (description.length() > 50) throw new IllegalArgumentException("Description length must be less than 50.");
        if (name.length() > 20) throw new IllegalArgumentException("Name length must be less than 20.");

        return repository.create(name, owner, description, world, x, y, z);
    }

    public boolean existsArena(String name) {
        return repository.exists(name);
    }

    public boolean deleteArena(int id) {
        return repository.delete(id);
    }

    public boolean updateName(int id, String name) {
        Arena arena = getArenaById(id);

        if (arena == null) return false;
        if (name.length() > 20) throw new IllegalArgumentException("Name length must be less than 20.");

        return repository.update(new Arena(
                arena.getId(),
                name,
                arena.getOwner(),
                arena.getDescription(),
                arena.getWorld(),
                arena.getX(),
                arena.getY(),
                arena.getZ(),
                arena.getStatus()
        ));
    }

    public boolean updateDescription(int id, String description) {
        Arena arena = getArenaById(id);

        if (arena == null) return false;
        if (description.length() > 50) throw new IllegalArgumentException("Description length must be less than 50.");

        return repository.update(new Arena(
                arena.getId(),
                arena.getName(),
                arena.getOwner(),
                description,
                arena.getWorld(),
                arena.getX(),
                arena.getY(),
                arena.getZ(),
                arena.getStatus()
        ));
    }

    public boolean updateWorld(int id, Location location) {
        Arena arena = getArenaById(id);
        if (arena == null) return false;
        return repository.update(
                new Arena(
                        arena.getId(),
                        arena.getName(),
                        arena.getOwner(),
                        arena.getDescription(),
                        location.level.getName(),
                        location.getFloorX(),
                        location.getFloorY(),
                        location.getFloorZ(),
                        arena.getStatus()
                )
        );
    }

    public boolean updateStatus(int id, Arena.Status status) {
        Arena arena = getArenaById(id);
        if (arena == null) return false;
        return repository.update(
                new Arena(
                        arena.getId(),
                        arena.getName(),
                        arena.getOwner(),
                        arena.getDescription(),
                        arena.getName(),
                        arena.getX(),
                        arena.getY(),
                        arena.getZ(),
                        status
                )
        );
    }

    public Arena getArenaById(int id) {
        return repository.getArenaById(id);
    }

    public Arena getArenaByName(String name) {
        return repository.getArenaByName(name);
    }

    public List<Arena> getArenas() {
        return repository.getArenas();
    }

    public List<Arena> getArenasByOwner(String owner) {
        return repository.getArenasByOwner(owner);
    }

    public List<Arena> getNotUsedArenas() {
        return repository.getNotUsedArenas();
    }

    public static ArenaManagementAPI getInstance() {
        return instance;
    }

}
