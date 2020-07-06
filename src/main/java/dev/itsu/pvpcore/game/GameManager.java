package dev.itsu.pvpcore.game;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.lang.BaseLang;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import dev.itsu.pvpcore.api.ArenaManagementAPI;
import dev.itsu.pvpcore.api.PlayerManagementAPI;
import dev.itsu.pvpcore.api.RoomManagementAPI;
import dev.itsu.pvpcore.exception.RoomNotFoundException;
import dev.itsu.pvpcore.model.Arena;
import dev.itsu.pvpcore.model.MatchRoom;
import dev.itsu.pvpcore.model.PlayerStatus;

import java.util.LinkedList;

public class GameManager {

    private GameState state;
    private MatchRoom room;
    private LinkedList<String> result;
    private LinkedList<String> alivePlayers;

    public GameManager(MatchRoom room) {
        this.room = room;
        result = new LinkedList<>();
        alivePlayers = (LinkedList<String>) room.getJoiners();
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
        int maxExp = getRoom().getMaxCount() * 100;
        for (int i = 0; i < result.size(); i++) {
            PlayerStatus status = PlayerManagementAPI.getInstance().getPlayerStatus(result.get(i));
            if (i == 1) {
                PlayerManagementAPI.getInstance().setPlayerExp(status.getName(), status.getExperienceLevel() + maxExp);
                PlayerManagementAPI.getInstance().updateWinCount(status.getName());
            } else {
                PlayerManagementAPI.getInstance().setPlayerExp(status.getName(), status.getExperienceLevel() + i * 10 / maxExp);
            }
        }
        // TODO 初期位置に戻す
        result.forEach(name -> {
            Player player = Server.getInstance().getPlayer(name);
            player.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        });

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
        alivePlayers.remove(player.getName());
        sendMessageToJoiners("§dPVPシステム§f>>プレイヤー: " + player.getName() + "が§c死亡§rしました。");
        player.sendMessage("§dPVPシステム§f>>ゲーム終了までお待ちください！");
        preFinish();
    }

    public void onLeave(Player player, String reason) {
        result.remove(player.getName());
        alivePlayers.remove(player.getName());
        room.getJoiners().remove(player.getName());
        GameListener.getInstance().removeFreezed(player.getName());
        sendMessageToJoiners("§dPVPシステム§f>>プレイヤー: " + player.getName() + "が§e退出§eしました。");
        // TODO プレイヤーの経験値を引く
        if (reason.equals(new BaseLang(BaseLang.FALLBACK_LANGUAGE).translateString("disconnectionScreen.noReason"))) {
            PlayerStatus status = PlayerManagementAPI.getInstance().getPlayerStatus(player.getName());
            PlayerManagementAPI.getInstance().setPlayerExp(
                    status.getName(),
                    Math.max(status.getExperienceLevel() - 200, 0)
            );
        }
        preFinish();
    }

    private void preFinish() {
        if (isEndOfGame()) {
            result.addFirst(alivePlayers.get(0));
            finish();
        }
    }

    private boolean isEndOfGame() {
        return result.size() == room.getJoiners().size() - 1;
    }
}
