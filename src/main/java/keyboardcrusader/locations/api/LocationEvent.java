package keyboardcrusader.locations.api;


import keyboardcrusader.locations.capability.Location;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.Event;

public class LocationEvent extends Event {
    private final ICapabilityProvider discoverer;
    private final Long id;
    private final Location location;

    public LocationEvent(ICapabilityProvider discoverer, Long id, Location location) {
        this.discoverer = discoverer;
        this.id = id;
        this.location = location;
    }

    public ICapabilityProvider getDiscoverer() {
        return discoverer;
    }

    public Long getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    /**
     * Called whenever a location is discovered, triggered by players and worlds
     */
    public static class Discover extends LocationEvent {
        public Discover(ICapabilityProvider discoverer, Long id, Location location) {
            super(discoverer, id, location);
        }
    }

    /**
     * Called whenever a location is updated, triggered by players and worlds
     */
    public static class Update extends LocationEvent {
        public Update(ICapabilityProvider discoverer, Long id, Location location) {
            super(discoverer, id, location);
        }
    }

    /**
     * Called whenever a location is removed, triggered by players and worlds
     */
    public static class Remove extends LocationEvent {
        public Remove(ICapabilityProvider discoverer, Long id, Location location) {
            super(discoverer, id, location);
        }
    }
}
