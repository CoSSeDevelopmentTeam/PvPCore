package dev.itsu.pvpcore;

import cn.nukkit.plugin.PluginBase;
import dev.itsu.pvpcore.game.EventListener;
import dev.itsu.pvpcore.game.GameListener;

public class Main extends PluginBase {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(GameListener.getInstance(), this);
        getLogger().info("Enabled.");
    }

    public static Main getInstance() {
        return instance;
    }
}
