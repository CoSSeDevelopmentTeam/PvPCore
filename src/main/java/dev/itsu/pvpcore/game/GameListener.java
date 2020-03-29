package dev.itsu.pvpcore.game;

import cn.nukkit.event.Listener;
import dev.itsu.pvpcore.model.MatchRoom;

public class GameListener implements Listener {

    private GameManager manager;

    public GameListener(GameManager manager) {
        this.manager = manager;
    }
}
