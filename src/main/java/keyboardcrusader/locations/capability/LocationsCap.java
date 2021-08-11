package keyboardcrusader.locations.capability;

import keyboardcrusader.locations.Locations;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class LocationsCap implements ILocationsCap {
    @CapabilityInject(ILocationsCap.class)
    public static final Capability<ILocationsCap> LOCATIONS_CAPABILITY = null;
    public static void register() {
        // Register the Player Data capability and create new storage for it
        CapabilityManager.INSTANCE.register(ILocationsCap.class, new Capability.IStorage<ILocationsCap>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<ILocationsCap> capability, ILocationsCap instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ILocationsCap> capability, ILocationsCap instance, Direction side, INBT nbt) {
                instance.deserializeNBT((ListNBT) nbt);

            }
        }, LocationsCap::new);

    }


    private List<Location> locations = new ArrayList<>();
    private long current = 0;

    @Override
    public boolean inLocation() {
        return !(currentLocation() == null);
    }

    @Override
    public Long currentLocation() {
        return current;
    }

    @Override
    public void setCurrentLocation(Long id) {
        current = id;
    }
    @Override
    public void setCurrentLocation(Location location) {
        setCurrentLocation(location.getID());
    }

    @Override
    public Location get(Long id) {
        for (Location location : locations) {
            if (location.getID() == id) {
                return location;
            }
        }
        return null;
    }
    @Override
    public List<Location> get() {
        return locations;
    }

    @Override
    public void set(List<Location> locations) {
        this.locations = locations;
    }

    @Override
    public void discover(Location location) {
        if (!isDiscovered(location.getID())) {
            locations.add(location);
        }
        else {
            update(location);
        }
    }

    @Override
    public void update(Location location) {
        if (!isDiscovered(location.getID())) {
            discover(location);
        }
        else {
            get(location.getID()).update(location);
        }
    }

    @Override
    public void remove(Location location) {
        if (!isDiscovered(location)) return;
        locations.remove(location);
    }
    @Override
    public void remove(Long id) {
        remove(get(id));
    }

    @Override
    public boolean isDiscovered(Location location) {
        return isDiscovered(location.getID());
    }
    @Override
    public boolean isDiscovered(Long id) {
        return !(get(id) == null);
    }

    @Override
    public ListNBT serializeNBT() {
        ListNBT listNBT = new ListNBT();
        for (Location location : locations) {
            if (location.getSource() != Location.Source.MAP) {
                listNBT.add(location.serialize());
            }
        }
        return listNBT;
    }

    @Override
    public void deserializeNBT(ListNBT nbt) {
        for (INBT inbt : nbt) {
            locations.add(new Location((CompoundNBT) inbt));
        }
    }
}
