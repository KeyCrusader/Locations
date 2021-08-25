package keyboardcrusader.locations.capability;

import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;

public interface ILocationsCap extends INBTSerializable<ListNBT> {
    Long currentLocation();
    void setCurrentLocation(Long id);

    Location get(Long id);
    Map<Long, Location> getAll();

    void setAll(Map<Long, Location> locations);

    boolean discover(Long id, Location location);
    boolean update(Long id, Location location);
    //boolean remove(Long id, Location location);
    boolean remove(Long id);

    boolean isDiscovered(Long id);
}
