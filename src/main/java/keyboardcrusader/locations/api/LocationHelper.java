package keyboardcrusader.locations.api;

import com.google.common.collect.ImmutableSet;
import keyboardcrusader.locations.capability.ILocationsCap;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.capability.LocationsCap;
import keyboardcrusader.locations.network.CurrentLocationPacket;
import keyboardcrusader.locations.network.LocationPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

public class LocationHelper {
    private static ILocationsCap getCapability(ICapabilityProvider capabilityProvider) {
        return capabilityProvider.getCapability(LocationsCap.LOCATIONS_CAPABILITY).orElseThrow(() -> new IllegalArgumentException(capabilityProvider.toString()+" missing locations capability"));
    }

    /**
     * Gets the location the player is currently in
     * @param playerEntity Player to check
     * @return ID of the current location, 0 if none
     */
    public static Long currentLocation(PlayerEntity playerEntity) {
        return getCapability(playerEntity).currentLocation();
    }

    /**
     * Sets the current player location and syncs it to that player
     * @param playerEntity Player to update
     * @param id Location ID currently inside
     */
    public static void setCurrentLocation(PlayerEntity playerEntity, Long id) {
        if (id == 0) {
            MinecraftForge.EVENT_BUS.post(new PlayerLocationEvent.Leave(playerEntity));
        }
        else {
            MinecraftForge.EVENT_BUS.post(new PlayerLocationEvent.Enter(playerEntity, get(playerEntity, id)));
        }

        getCapability(playerEntity).setCurrentLocation(id);
        if (!playerEntity.getEntityWorld().isRemote()) {
            PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new CurrentLocationPacket(id));
        }
    }

    /**
     * Gets the location by ID
     * @param capabilityProvider Player/World to query, player is discovered only world has all locations
     * @param id Location ID
     * @return Location if exists/discovered, otherwise null
     */
    public static Location get(ICapabilityProvider capabilityProvider, Long id) {
        return getCapability(capabilityProvider).get(id);
    }

    /**
     * Gets all the locations from a certain source
     * @param capabilityProvider Player/World to check
     * @param source Location.Source enum type
     * @return Map of all matching locations
     */
    public static Map<Long, Location> getBySource(ICapabilityProvider capabilityProvider, Location.Source source) {
        Map<Long, Location> locationMap = new HashMap<>();
        getAll(capabilityProvider).forEach((id, location) -> {
            if (location.getSource() == source) {
                locationMap.put(id, location);
            }
        });
        return locationMap;
    }

    /**
     * Gets all locations
     * @param capabilityProvider Player/World to query
     * @return All known locations for that entity
     */
    public static Map<Long, Location> getAll(ICapabilityProvider capabilityProvider) {
        return getCapability(capabilityProvider).getAll();
    }

    /**
     * Sets all known locations, used when a player dies to keep their discovered locations
     * @param capabilityProvider Player/World to set locations for
     * @param locations Map of locations with IDs
     */
    public static void setAll(ICapabilityProvider capabilityProvider, Map<Long, Location> locations) {
        getCapability(capabilityProvider).setAll(locations);
    }

    /**
     * Check if a location has been discovered already
     * @param capabilityProvider Player/World to check
     * @param id Location ID to check
     * @return True if already discovered, false if not
     */
    public static boolean isDiscovered(ICapabilityProvider capabilityProvider, Long id) {
        return getCapability(capabilityProvider).isDiscovered(id);
    }

    /**
     * Discovers a new location, syncs it to client and posts event for it
     * @param capabilityProvider Player/World that has discovered it
     * @param id New location ID
     * @param location New location details
     */
    public static void discover(ICapabilityProvider capabilityProvider, Long id, Location location) {
        if (getCapability(capabilityProvider).discover(id, location)) {
            if (location.getSource() == Location.Source.STRUCTURE || location.getSource() == Location.Source.FEATURE) {
                MinecraftForge.EVENT_BUS.post(new LocationEvent.Discover(capabilityProvider, id, location));
            }

            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(id, location, LocationPacket.PacketType.DISCOVER));
                }
            }
        }
    }
    // Doesn't post event
    public static void discover(ICapabilityProvider capabilityProvider, Map<Long, Location> locationMap) {
        Map<Long, Location> newLocationMap = new HashMap<>();
        ILocationsCap locationsCap = getCapability(capabilityProvider);

        locationMap.forEach((id, location) -> {
            if (locationsCap.discover(id, location)) {
                newLocationMap.put(id, location);
            }
        });

        if (!newLocationMap.isEmpty()) {
            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(newLocationMap, LocationPacket.PacketType.DISCOVER));
                }
            }
        }
    }
    /**
     * Updates a location, syncs it to client and posts event for it
     * @param capabilityProvider Player/World to update
     * @param id Location ID
     * @param location Updated location details
     */
    public static void update(ICapabilityProvider capabilityProvider, Long id, Location location) {
        if (getCapability(capabilityProvider).update(id, location)) {
            if (location.getSource() == Location.Source.STRUCTURE || location.getSource() == Location.Source.FEATURE) {
                MinecraftForge.EVENT_BUS.post(new LocationEvent.Update(capabilityProvider, id, location));
            }

            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(id, location, LocationPacket.PacketType.UPDATE));
                }
            }
        }
    }
    /**
     * Removes a location, syncs change to client and posts event
     * @param capabilityProvider Player/World to remove location from
     * @param id Location ID to remove
     * @param location Location to remove
     */
    public static void remove(ICapabilityProvider capabilityProvider, Long id, Location location) {
        if (getCapability(capabilityProvider).remove(id, location)) {
            if (location.getSource() == Location.Source.STRUCTURE || location.getSource() == Location.Source.FEATURE) {
                MinecraftForge.EVENT_BUS.post(new LocationEvent.Remove(capabilityProvider, id, location));
            }

            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(id, location, LocationPacket.PacketType.REMOVE));
                }
            }
        }
    }
    // Doesn't post event
    public static void remove(ICapabilityProvider capabilityProvider, Map<Long, Location> locationMap) {
        Map<Long, Location> removeLocationMap = new HashMap<>();
        ILocationsCap locationsCap = getCapability(capabilityProvider);

        locationMap.forEach((id, location) -> {
            if (locationsCap.remove(id, location)) {
                removeLocationMap.put(id, location);
            }
        });

        if (!removeLocationMap.isEmpty()) {
            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(removeLocationMap, LocationPacket.PacketType.REMOVE));
                }
            }
        }
    }


    /**
     * Checks if position is in a location by checking against the world list
     * @param world World to check in
     * @param vector3i Position to check
     * @return Location in if inside one, otherwise null
     */
    public static Map.Entry<Long, Location> inLocation(World world, Vector3i vector3i) {
        for (Map.Entry<Long, Location> mapEntry : getAll(world).entrySet()) {
            if (!mapEntry.getValue().getInfo().isDisabled()) {
                if (mapEntry.getValue().getSource() == Location.Source.STRUCTURE || mapEntry.getValue().getSource() == Location.Source.FEATURE || mapEntry.getValue().getSource() == Location.Source.PERMANENT_POI) { // Ignore map markers and POIs
                    if (mapEntry.getValue().isVecInside(vector3i)) {
                        return mapEntry;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isTouching(ImmutableSet<BlockState> blockStates, BlockPos blockPos, World world) {
        if (blockStates.contains(world.getBlockState(blockPos.north()))) return true;
        if (blockStates.contains(world.getBlockState(blockPos.south()))) return true;
        if (blockStates.contains(world.getBlockState(blockPos.east()))) return true;
        if (blockStates.contains(world.getBlockState(blockPos.west()))) return true;
        if (blockStates.contains(world.getBlockState(blockPos.up()))) return true;
        if (blockStates.contains(world.getBlockState(blockPos.down()))) return true;
        return false;
    }
}
