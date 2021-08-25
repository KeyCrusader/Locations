package keyboardcrusader.locations.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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

    private Long currentLocationID = 0L;
    private Map<Long, Location> locations = new HashMap<>();

    @Override
    public Long currentLocation() {
        return currentLocationID;
    }

    @Override
    public void setCurrentLocation(Long id) {
        currentLocationID = id;
    }

    @Override
    public Location get(Long id) {
        return locations.get(id);
    }

    @Override
    public Map<Long, Location> getAll() {
        return locations;
    }

    @Override
    public void setAll(Map<Long, Location> locations) {
        this.locations = locations;
    }

    @Override
    public boolean discover(Long id, Location location) {
        if (!isDiscovered(id)) {
            locations.put(id, location);
            return true;
        }
        return false;
    }

    @Override
    public boolean update(Long id, Location location) {
        if (isDiscovered(id)) {
            locations.get(id).update(location);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Long id) {
        if (isDiscovered(id)) {
            return locations.remove(id) != null;
        }
        return false;
    }

    @Override
    public boolean isDiscovered(Long id) {
        return locations.containsKey(id);
    }

    @Override
    public ListNBT serializeNBT() {
        ListNBT listNBT = new ListNBT();
        for (Map.Entry<Long, Location> mapEntry : locations.entrySet()) {
            listNBT.add(mapEntry.getValue().serialize(mapEntry.getKey()));
        }
        return listNBT;
    }

    @Override
    public void deserializeNBT(ListNBT nbt) {
        for (INBT inbt : nbt) {
            CompoundNBT compoundNBT = (CompoundNBT) inbt;
            locations.put(compoundNBT.getLong("id"), new Location(compoundNBT));
        }
    }
}
