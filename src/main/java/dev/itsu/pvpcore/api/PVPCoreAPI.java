package dev.itsu.pvpcore.api;

import dev.itsu.pvpcore.exception.InvalidPlayersCountException;
import dev.itsu.pvpcore.exception.PlayerAlreadyEntryException;
import dev.itsu.pvpcore.exception.RoomNotFoundException;
import dev.itsu.pvpcore.game.GameManager;
import dev.itsu.pvpcore.game.GameState;
import dev.itsu.pvpcore.model.MatchRoom;

import java.util.*;

public class PVPCoreAPI {

    private final Map<Integer, MatchRoom> rooms;
    private final Map<String, Integer> entrying;

    private PVPCoreAPI() {
        rooms = new HashMap<>();
        entrying = new HashMap<>();
    }

    public void entry(int id, String playerName) {
        MatchRoom room = rooms.get(id);
        if (room == null) throw new RoomNotFoundException();

        if (isEntrying(playerName)) throw new PlayerAlreadyEntryException(getEntryingRoom(playerName));
        if (!room.getOwner().equals(playerName)) {
            MatchRoom r = getRoomByOwner(playerName);
            if (r != null) removeRoom(r.getId());
        }

        room.getJoiners().add(playerName);
        entrying.put(playerName, id);

        if (room.getJoiners().size() == room.getMaxCount()) {
            startGame(id);
        }
    }

    public void cancelEntry(int id, String playerName) {
        MatchRoom room = rooms.get(id);
        if (room == null) throw new RoomNotFoundException();
        if (room.getOwner().equals(playerName)) throw new IllegalStateException("Room owner cannot leave their own room.");
        room.getJoiners().remove(playerName);
        entrying.remove(playerName);
    }

    public MatchRoom getEntryingRoom(String playerName) {
        return getRoomById(entrying.get(playerName));
    }

    public boolean isEntrying(String playerName) {
        return entrying.get(playerName) != null;
    }

    public int createRoom(String owner, String name, String description, int maxCount, int minCount, boolean privateRoom, int arenaId) {
        if (minCount < 2 || minCount > maxCount) throw new InvalidPlayersCountException();

        MatchRoom room = new MatchRoom(
                name,
                owner,
                description,
                new Random().nextInt(8999) + 1000,
                arenaId,
                privateRoom,
                maxCount,
                minCount,
                System.currentTimeMillis(),
                new ArrayList<>(),
                GameState.STATE_WAITING
        );

        rooms.put(room.getId(), room);
        entry(room.getId(), owner);

        return room.getId();
    }

    public void removeRoom(int id) {
        rooms.remove(id);
    }

    public boolean checkCanStart(int id) {
        MatchRoom room = rooms.get(id);
        if (room == null) throw new RoomNotFoundException();

        if (room.getJoiners().size() > room.getMinCount()) return true;
        return false;
    }

    public void startGame(int id) {
        MatchRoom room = getRoomById(id);

        // 参加者が開いているルームは消去
        rooms.forEach((i, r) -> {
            room.getJoiners().forEach(name -> {
                if (r.getOwner().equals(name)) {
                    removeRoom(i);
                }
            });
        });

        new GameManager(room).start();
    }

    public MatchRoom getRoomById(int id) {
        return rooms.get(id);
    }

    public MatchRoom getRoomByOwner(String playerName) {
        return rooms.values().stream()
                .filter(it -> it.getOwner().equals(playerName))
                .findFirst()
                .orElse(null);
    }

    public List<MatchRoom> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    public static class Factory {
        private static PVPCoreAPI instance = new PVPCoreAPI();
        public static PVPCoreAPI getInstance() {
            return instance;
        }
    }
}
