package dev.itsu.pvpcore.api;

import dev.itsu.pvpcore.exception.ArenaNotFoundException;
import dev.itsu.pvpcore.exception.ArenaUnavailableException;
import dev.itsu.pvpcore.exception.InvalidPlayersCountException;
import dev.itsu.pvpcore.exception.RoomNotFoundException;
import dev.itsu.pvpcore.game.GameManager;
import dev.itsu.pvpcore.game.GameState;
import dev.itsu.pvpcore.model.Arena;
import dev.itsu.pvpcore.model.MatchRoom;

import java.util.*;

public class RoomManagementAPI {

    private static RoomManagementAPI instance = new RoomManagementAPI();

    private final Map<Integer, MatchRoom> rooms;
    private final Map<String, Integer> entrying;

    private RoomManagementAPI() {
        rooms = new HashMap<>();
        entrying = new HashMap<>();
    }

    public void entry(int id, String playerName) {
        MatchRoom room = rooms.get(id);
        if (room == null) throw new RoomNotFoundException();

        // すでにエントリーしている場合は退出する
        if (isEntrying(playerName)) cancelEntry(getEntryingRoom(playerName).getId(), playerName);

        // このルームの所有者でない場合には、参加する人が所有しているルームを削除
        if (!room.getOwner().equals(playerName)) {
            MatchRoom r = getRoomByOwner(playerName);
            if (r != null) removeRoom(r.getId());
        }

        room.getJoiners().add(playerName);
        entrying.put(playerName, id);

        // 最大人数に到達したらPVP開始
        if (room.getJoiners().size() == room.getMaxCount()) startGame(id);
    }

    public void cancelEntry(int id, String playerName) {
        MatchRoom room = rooms.get(id);
        if (room == null) throw new RoomNotFoundException();

        // エントリーしている人をキャンセル
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
        if (minCount < 2 || maxCount < 2 || minCount > maxCount) throw new InvalidPlayersCountException();

        Arena arena = ArenaManagementAPI.getInstance().getArenaById(arenaId);
        if (arena == null) throw new ArenaNotFoundException();
        else if (arena.getStatus() == Arena.Status.USED) throw new ArenaUnavailableException();

        // すでに所有しているルームは削除する
        MatchRoom r = getRoomByOwner(owner);
        if (r != null) removeRoom(r.getId());

        // 他のルームにエントリーしている場合は退出する
        if (isEntrying(owner)) cancelEntry(getEntryingRoom(owner).getId(), owner);

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

        ArenaManagementAPI.getInstance().updateStatus(room.getArenaId(), Arena.Status.RESERVED);
        rooms.put(room.getId(), room);

        // 自分のルームにエントリーする
        entry(room.getId(), owner);

        return room.getId();
    }

    public void removeRoom(int id) {
        MatchRoom room = getRoomById(id);
        room.getJoiners().forEach(name -> cancelEntry(room.getId(), name));
        rooms.remove(id);
        ArenaManagementAPI.getInstance().updateStatus(room.getArenaId(), Arena.Status.AVAILABLE);
    }

    public boolean checkCanStart(int id) {
        MatchRoom room = rooms.get(id);
        if (room == null) throw new RoomNotFoundException();

        if (room.getJoiners().size() >= room.getMinCount()) return true;
        return false;
    }

    // ゲーム開始
    public void startGame(int id) {
        MatchRoom room = getRoomById(id);
        ArenaManagementAPI.getInstance().updateStatus(room.getArenaId(), Arena.Status.USED);
        new GameManager(room).start();
    }

    public MatchRoom getRoomById(int id) {
        return rooms.get(id);
    }

    public MatchRoom getRoomByOwner(String playerName) {
        return rooms.values().stream()
                .filter(it -> it.getOwner().equals(playerName))
                .findFirst()
                .orElse(null); // 見つからなければnullを返す
    }

    public List<MatchRoom> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    public static RoomManagementAPI getInstance() {
        return instance;
    }

}
