package dev.itsu.pvpcore;

import cn.nukkit.plugin.PluginBase;
import dev.itsu.pvpcore.api.ArenaManagementAPI;
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

    @Override
    public void onDisable() {
        ArenaManagementAPI.getInstance().disconnect();
    }

    public static Main getInstance() {
        return instance;
    }
}
