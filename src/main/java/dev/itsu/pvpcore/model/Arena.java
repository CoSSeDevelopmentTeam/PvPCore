package dev.itsu.pvpcore.model;

public class Arena {

    private int id;
    private String name;
    private String owner;
    private String description;
    private String world;
    private int x;
    private int y;
    private int z;

    public Arena(int id, String name, String owner, String description, String world, int x, int y, int z) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public String getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
