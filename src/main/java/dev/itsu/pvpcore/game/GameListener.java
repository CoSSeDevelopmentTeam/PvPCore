package dev.itsu.pvpcore.game;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class GameListener implements Listener {

    private static GameListener instance = new GameListener();
    private List<String> freezedPlayers;
    private List<GameManager> managers;

    private GameListener() {
        freezedPlayers = new ArrayList<>();
        managers = new ArrayList<>();
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (freezedPlayers.contains(event.getPlayer().getName())) event.setCancelled();
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        GameManager manager = getGameManagerByPlayer(event.getEntity());
        if (manager == null) return;
        manager.onDeath(event.getEntity());
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        GameManager manager = getGameManagerByPlayer(event.getPlayer());
        if (manager == null) return;
        manager.onLeave(event.getPlayer());
    }

    private GameManager getGameManagerByPlayer(Player player) {
        return managers.stream()
                .filter(it -> it.getRoom().getJoiners().contains(player.getName()))
                .findFirst()
                .orElse(null);
    }

    public static GameListener getInstance() {
        return instance;
    }

    public void addfreezed(String name) {
        freezedPlayers.add(name);
    }

    public void removeFreezed(String name) {
        freezedPlayers.remove(name);
    }

    public void addGameManager(GameManager manager) {
        managers.add(manager);
    }

    public void removeGameManager(GameManager manager) {
        managers.remove(manager);
    }

}
