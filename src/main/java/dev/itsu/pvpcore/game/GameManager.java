package dev.itsu.pvpcore.game;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import dev.itsu.pvpcore.Main;
import dev.itsu.pvpcore.model.MatchRoom;

public class GameManager {

    private GameState state;
    private MatchRoom room;
    private Server server = Server.getInstance();

    public GameManager(MatchRoom room) {
        this.room = room;
    }

    public void start() {
        Thread mainThread = new Thread(() -> {
            try {
                state = GameState.STATE_PREPARING;

                server.getPluginManager().registerEvents(new GameListener(this), Main.getInstance());

                teleportToStartLocation();
                doCountDownLatch();
                sendTitleToJoiners("PVP スタート!!", TextFormat.GRAY + "さあ、戦え!");

                state = GameState.STATE_FIGHTING;

                state = GameState.STATE_FINISHED;

            } catch (Exception e) {
                e.printStackTrace();
                state = GameState.STATE_FINISHED;

                room.getJoiners().forEach(name -> server.getPlayer(name).sendTitle("§aシステム§r>>§cPvP中にエラーが発生したため中断しました。"));
                //TODO 初期位置に戻す

                state = GameState.STATE_WAITING;
            }
        });
        mainThread.setName("GameThread/" + room.getId());
        mainThread.start();
    }

    public GameState getState() {
        return state;
    }

    public MatchRoom getRoom() {
        return room;
    }

    private void teleportToStartLocation() {

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
            server.getPlayer(name).sendTitle(message, subMessage);
        });
    }
}
