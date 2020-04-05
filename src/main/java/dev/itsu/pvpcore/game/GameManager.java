package dev.itsu.pvpcore.game;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import dev.itsu.pvpcore.api.ArenaManagementAPI;
import dev.itsu.pvpcore.api.RoomManagementAPI;
import dev.itsu.pvpcore.exception.RoomNotFoundException;
import dev.itsu.pvpcore.model.Arena;
import dev.itsu.pvpcore.model.MatchRoom;

import java.util.LinkedList;

public class GameManager {

    private GameState state;
    private MatchRoom room;
    private LinkedList<String> result;

    public GameManager(MatchRoom room) {
        this.room = room;
        result = new LinkedList<>();
    }

    public void start() {
        Thread mainThread = new Thread(() -> {
            try {
                state = GameState.STATE_PREPARING;
                GameListener.getInstance().addGameManager(this);
                teleportToStartLocation();
                setFreezed(true);
                doCountDownLatch();
                sendTitleToJoiners("PVP スタート!!", TextFormat.GRAY + "さあ、戦え!");
                setFreezed(false);

                state = GameState.STATE_FIGHTING;

            } catch (Exception e) {
                e.printStackTrace();
                sendMessageToJoiners("§aシステム§r>>§cPvP中にエラーが発生したため中断しました。");
                finish();
            }
        });
        mainThread.setName("GameThread/" + room.getId());
        mainThread.start();
    }

    private void finish() {
        state = GameState.STATE_FINISHED;
        sendTitleToJoiners("§cゲーム終了");

        // TODO 順位・経験値の計算
        // TODO 初期位置に戻す

        GameListener.getInstance().removeGameManager(this);
        ArenaManagementAPI.getInstance().updateStatus(room.getArenaId(), Arena.Status.AVAILABLE);
        RoomManagementAPI.getInstance().removeRoom(room.getId());
    }

    public GameState getState() {
        return state;
    }

    public MatchRoom getRoom() {
        return room;
    }

    private void teleportToStartLocation() {
        Arena arena = ArenaManagementAPI.getInstance().getArenaById(room.getArenaId());
        if (arena == null) throw new RoomNotFoundException();

        room.getJoiners().forEach(name -> {
            Player player = Server.getInstance().getPlayer(name);
            double xRand = 5 * Math.random();
            double zRand = 5 * Math.random();
            player.teleport(new Position(arena.getX() + xRand, arena.getY(), arena.getZ() + zRand, Server.getInstance().getLevelByName(arena.getWorld())));
        });
    }

    private void doCountDownLatch() throws InterruptedException{
        for (int startCountDown = 10; startCountDown > 0; startCountDown--) {
            String count = "" + TextFormat.GREEN + startCountDown;
            if (startCountDown <= 3) count = "" + TextFormat.RED + startCountDown;
            sendTitleToJoiners("ゲーム開始まで " + count);
            Thread.sleep(1000);
        }
    }

    private void sendTitleToJoiners(String message) {
        sendTitleToJoiners(message, "");
    }

    private void sendTitleToJoiners(String message, String subMessage) {
        room.getJoiners().forEach(name -> {
            Server.getInstance().getPlayer(name).sendTitle(message, subMessage);
        });
    }

    private void sendMessageToJoiners(String message) {
        room.getJoiners().forEach(name -> {
            Server.getInstance().getPlayer(name).sendMessage(message);
        });
    }

    private void setFreezed(boolean bool) {
        if (bool) {
            room.getJoiners().forEach(name -> GameListener.getInstance().addfreezed(name));
        } else {
            room.getJoiners().forEach(name -> GameListener.getInstance().removeFreezed(name));
        }
    }

    public void onDeath(Player player) {
        result.addFirst(player.getName());
        sendMessageToJoiners("§dPVPシステム§f>>プレイヤー: " + player.getName() + "が§c死亡§rしました。");
        if (result.size() == room.getJoiners().size() - 1) finish();
    }

    public void onLeave(Player player) {
        result.remove(player.getName());
        room.getJoiners().remove(player.getName());
        GameListener.getInstance().removeFreezed(player.getName());
        sendMessageToJoiners("§dPVPシステム§f>>プレイヤー: " + player.getName() + "が§e退出§eしました。");
        // TODO プレイヤーの経験値を引く

        if (result.size() == room.getJoiners().size() - 1) finish();
    }
}
