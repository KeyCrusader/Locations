package keyboardcrusader.locations.api;

import keyboardcrusader.locations.capability.Location;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class PlayerLocationEvent extends Event {
    private final PlayerEntity playerEntity;

    public PlayerLocationEvent(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public PlayerEntity getPlayer() {
        return playerEntity;
    }

    /**
     * Called whenever a player leaves a location
     */
    public static class Leave extends PlayerLocationEvent {
        public Leave(PlayerEntity playerEntity) {
            super(playerEntity);
        }
    }

    /**
     * Called whenever a player enters a location
     */
    public static class Enter extends PlayerLocationEvent {
        private final Location location;

        public Enter(PlayerEntity playerEntity, Location location) {
            super(playerEntity);
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }
    }
}
