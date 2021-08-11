package keyboardcrusader.locations.api;


import keyboardcrusader.locations.capability.Location;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.Event;

public class LocationEvent extends Event {
    private final ResourceLocation type;
    private final ICapabilityProvider discoverer;
    private final Location location;

    public LocationEvent(ResourceLocation type, ICapabilityProvider discoverer, Location location) {
        this.type = type;
        this.discoverer = discoverer;
        this.location = location;
    }

    public ResourceLocation getType() {
        return type;
    }

    public ICapabilityProvider getDiscoverer() {
        return discoverer;
    }

    public Location getLocation() {
        return location;
    }

    public static class Discover extends LocationEvent {
        public Discover(ResourceLocation type, ICapabilityProvider discoverer, Location location) {
            super(type, discoverer, location);
        }
    }

    public static class Update extends LocationEvent {
        public Update(ResourceLocation type, ICapabilityProvider discoverer, Location location) {
            super(type, discoverer, location);
        }
    }

    public static class Remove extends LocationEvent {
        public Remove(ResourceLocation type, ICapabilityProvider discoverer, Location location) {
            super(type, discoverer, location);
        }
    }
}
