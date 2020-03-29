package dev.itsu.pvpcore;

import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Enabled.");
    }

    public static Main getInstance() {
        return instance;
    }
}
