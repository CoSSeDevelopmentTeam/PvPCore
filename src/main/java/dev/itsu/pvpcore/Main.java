package dev.itsu.pvpcore;

import cn.nukkit.plugin.PluginBase;
import dev.itsu.pvpcore.game.EventListener;

public class Main extends PluginBase {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("Enabled.");
    }

    public static Main getInstance() {
        return instance;
    }
}
