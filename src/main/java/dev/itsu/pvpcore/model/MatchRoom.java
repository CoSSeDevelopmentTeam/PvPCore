package dev.itsu.pvpcore.model;

import java.util.List;

public class MatchRoom {

    private String name;
    private String owner;
    private String description;
    private int id;
    private int arenaId;
    private boolean privateRoom;
    private int maxCount;
    private int minCount;
    private long createdAt;
    private List<String> joiners;

    public MatchRoom(
            String name,
            String owner,
            String description,
            int id,
            int arenaId,
            boolean privateRoom,
            int maxCount,
            int minCount,
            long createdAt,
            List<String> joiners
    ) {
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.id = id;
        this.arenaId = arenaId;
        this.privateRoom = privateRoom;
        this.maxCount = maxCount;
        this.minCount = minCount;
        this.createdAt = createdAt;
        this.joiners = joiners;
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

    public int getMinCount() {
        return minCount;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<String> getJoiners() {
        return joiners;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public boolean isPrivateRoom() {
        return privateRoom;
    }

    public int getArenaId() {
        return arenaId;
    }

    public int getId() {
        return id;
    }
}
