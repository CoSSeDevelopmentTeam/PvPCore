package dev.itsu.pvpcore.game;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import dev.itsu.pvpcore.api.RoomManagementAPI;
import dev.itsu.pvpcore.model.MatchRoom;

public class EventListener implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RoomManagementAPI api = RoomManagementAPI.getInstance();

        if (api.isEntrying(player.getName())) {
            api.cancelEntry(api.getEntryingRoom(player.getName()).getId(), player.getName());
        }

        MatchRoom room = api.getRoomByOwner(player.getName());
        if (room != null) api.removeRoom(room.getId());
    }

}
