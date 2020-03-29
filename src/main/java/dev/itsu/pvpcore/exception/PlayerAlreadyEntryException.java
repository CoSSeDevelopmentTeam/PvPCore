package dev.itsu.pvpcore.exception;

import dev.itsu.pvpcore.model.MatchRoom;

public class PlayerAlreadyEntryException extends RuntimeException {

    private MatchRoom room;

    public PlayerAlreadyEntryException(MatchRoom room) {
        super();
        this.room = room;
    }

    public MatchRoom getRoomId() {
        return room;
    }
}
