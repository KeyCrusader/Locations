package keyboardcrusader.locations.capability;

import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface ILocationsCap extends INBTSerializable<ListNBT> {
    boolean inLocation();
    Long currentLocation();
    void setCurrentLocation(Long id);
    void setCurrentLocation(Location location);

    Location get(Long id);
    List<Location> get();
    void set(List<Location> locations);

    void discover(Location location);
    void update(Location location);
    void remove(Location location);
    void remove(Long id);

    boolean isDiscovered(Location location);
    boolean isDiscovered(Long id);
}
