package keyboardcrusader.locations.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.capability.ILocationsCap;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.capability.LocationsCap;
import keyboardcrusader.locations.network.CurrentLocationPacket;
import keyboardcrusader.locations.network.LocationPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
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
    public static void setCurrentLocation(PlayerEntity playerEntity, Long id, boolean discovery) {
        if (id == 0) {
            MinecraftForge.EVENT_BUS.post(new PlayerLocationEvent.Leave(playerEntity));
        }
        else {
            MinecraftForge.EVENT_BUS.post(new PlayerLocationEvent.Enter(playerEntity, get(playerEntity, id), discovery));
        }

        getCapability(playerEntity).setCurrentLocation(id);
        if (!playerEntity.getEntityWorld().isRemote()) {
            PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerEntity), new CurrentLocationPacket(id, discovery));
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
        discover(capabilityProvider, new HashMap<Long, Location>(){{put(id, location);}});
    }
    public static void discover(ICapabilityProvider capabilityProvider, Map<Long, Location> locationMap) {
        Map<Long, Location> newLocationMap = new HashMap<>();
        ILocationsCap locationsCap = getCapability(capabilityProvider);
        Locations.LOGGER.debug("Discover: " + capabilityProvider + ", " + locationMap);

        locationMap.forEach((id, location) -> {
            if (locationsCap.discover(id, location)) {
                newLocationMap.put(id, location);
                MinecraftForge.EVENT_BUS.post(new LocationEvent.Discover(capabilityProvider, id, location));
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
    public static void discoverGlobal(ServerWorld world, Map<Long, Location> locationMap) {
        discover(world, locationMap);
        world.getPlayers().forEach(playerEntity -> discover(playerEntity, locationMap));
    }
    /**
     * Updates a locations bounds, syncs it to client and posts event for it
     * @param capabilityProvider Player/World to update
     * @param id Location ID
     * @param location Updated location details
     */
    public static void update(ICapabilityProvider capabilityProvider, Long id, Location location) {
        update(capabilityProvider, new HashMap<Long, Location>(){{put(id, location);}});
    }
    public static void update(ICapabilityProvider capabilityProvider, Map<Long, Location> locationMap) {
        Map<Long, Location> updateLocationMap = new HashMap<>();
        ILocationsCap locationsCap = getCapability(capabilityProvider);

        Locations.LOGGER.debug("Update: " + capabilityProvider + ", " + locationMap);

        locationMap.forEach((id, location) -> {
            Location oldLocation = new Location(get(capabilityProvider, id));
            if (locationsCap.update(id, location)) {
                updateLocationMap.put(id, location);
                MinecraftForge.EVENT_BUS.post(new LocationEvent.Update(capabilityProvider, id, location, oldLocation));
            }
        });

        if (!updateLocationMap.isEmpty()) {
            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(updateLocationMap, LocationPacket.PacketType.UPDATE));
                }
            }
        }
    }
    public static void updateGlobal(ServerWorld world, Map<Long, Location> locationMap) {
        update(world, locationMap);
        world.getPlayers().forEach(playerEntity -> update(playerEntity, locationMap));
    }
    /**
     * Removes a location, syncs change to client and posts event
     * @param capabilityProvider Player/World to remove location from
     * @param id Location ID to remove
     */
    public static void remove(ICapabilityProvider capabilityProvider, Long id) {
        remove(capabilityProvider, Lists.newArrayList(id));
    }
    public static void remove(ICapabilityProvider capabilityProvider, List<Long> idList) {
        Map<Long, Location> removeLocationMap = new HashMap<>();
        ILocationsCap locationsCap = getCapability(capabilityProvider);

        Locations.LOGGER.debug("Remove: " + capabilityProvider + ", " + idList);

        idList.forEach((id) -> {
            Location location = locationsCap.get(id);
            if (locationsCap.remove(id)) {
                removeLocationMap.put(id, location);
                MinecraftForge.EVENT_BUS.post(new LocationEvent.Remove(capabilityProvider, id, location));
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
    public static void removeGlobal(ServerWorld world, List<Long> idList) {
        remove(world, idList);
        world.getPlayers().forEach(playerEntity -> remove(playerEntity, idList));
    }
    /**
     * Renames a location
     */
    public static void rename(ICapabilityProvider capabilityProvider, Long id, String name) {
        if (isDiscovered(capabilityProvider, id)) {
            Location location = get(capabilityProvider, id);
            String oldName = location.getName();
            location.setName(name);
            MinecraftForge.EVENT_BUS.post(new LocationEvent.Rename(capabilityProvider, id, location, oldName));

            if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
                if (((PlayerEntity) capabilityProvider).isAlive()) {
                    PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(new HashMap<Long, Location>(){{put(id, location);}}, LocationPacket.PacketType.RENAME));
                }
            }
        }
    }
    public static void rename(ICapabilityProvider capabilityProvider, Map<Long, Location> locationMap) {
        locationMap.forEach((id, location) -> rename(capabilityProvider, id, location.getName()));
    }
    public static void renameGlobal(ServerWorld world, Long id, String name) {
        rename(world, id, name);
        world.getPlayers().forEach(playerEntity -> rename(playerEntity, id, name));
    }
    /**
     * Checks if position is in a location by checking against the world list
     * @param world World to check in
     * @param vector3d Position to check
     * @return Location in if inside one, otherwise null
     */
    public static Map.Entry<Long, Location> inLocation(World world, RegistryKey<World> dimension, Vector3d vector3d) {
        // TODO Maybe store by chunk coords and just check nearby to improve performance slightly
        for (Map.Entry<Long, Location> mapEntry : getAll(world).entrySet()) {
            if (!mapEntry.getValue().getInfo().isDisabled()) {
                if (mapEntry.getValue().getDimension() == dimension) {
                    if (mapEntry.getValue().isVecInside(vector3d)) {
                        return mapEntry;
                    }
                }
            }
        }
        return null;
    }

    public static BlockPos getPosition(StructurePiece structurePiece) {
        return new BlockPos(
                structurePiece.getBoundingBox().minX + (structurePiece.getBoundingBox().getXSize() / 2),
                structurePiece.getBoundingBox().minY + (structurePiece.getBoundingBox().getYSize() / 2),
                structurePiece.getBoundingBox().minZ + (structurePiece.getBoundingBox().getZSize() / 2));
    }

    public static Long generateID(ResourceLocation type, RegistryKey<World> dimension, BlockPos position) {
        return (Math.abs(type.hashCode()) + Math.abs(dimension.getLocation().hashCode()) + Math.abs(position.toLong())) % Long.MAX_VALUE;
    }

    public static AxisAlignedBB mutableToAxisAlignedBB(MutableBoundingBox mutableBoundingBox) {
        return new AxisAlignedBB(
                mutableBoundingBox.minX,
                mutableBoundingBox.minY,
                mutableBoundingBox.minZ,
                mutableBoundingBox.maxX,
                mutableBoundingBox.maxY,
                mutableBoundingBox.maxZ
        );
    }

    public static IntArrayNBT toIntArrayNBT(AxisAlignedBB axisAlignedBB) {
        return new IntArrayNBT(new int[]{(int) axisAlignedBB.minX, (int) axisAlignedBB.minY, (int) axisAlignedBB.minZ, (int) axisAlignedBB.maxX, (int) axisAlignedBB.maxY, (int) axisAlignedBB.maxZ});
    }

    public static AxisAlignedBB fromIntArray(int[] coords) {
        return new AxisAlignedBB(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
    }

    public static AxisAlignedBB getMultiBlockLocation(ImmutableSet<BlockState> blockStates, World world, BlockPos pos) {
        List<BlockPos> checkedBlocks = Lists.newArrayList(pos);
        checkedBlocks = touching(blockStates, world, pos, checkedBlocks);

        int minX = pos.getX();
        int minY = pos.getY();
        int minZ = pos.getZ();
        int maxX = pos.getX();
        int maxY = pos.getY();
        int maxZ = pos.getZ();

        for (BlockPos blockPos : checkedBlocks) {
            minX = Math.min(minX, blockPos.getX());
            minY = Math.min(minY, blockPos.getY());
            minZ = Math.min(minZ, blockPos.getZ());
            maxX = Math.max(maxX, blockPos.getX());
            maxY = Math.max(maxY, blockPos.getY());
            maxZ = Math.max(maxZ, blockPos.getZ());
        }
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static List<BlockPos> touching(ImmutableSet<BlockState> blockStates, World world, BlockPos pos, List<BlockPos> checkedBlocks) {
        if (blockStates.contains(world.getBlockState(pos.up())) && !checkedBlocks.contains(pos.up())) {
            checkedBlocks.add(pos.up());
            touching(blockStates, world, pos.up(), checkedBlocks);
        }
        if (blockStates.contains(world.getBlockState(pos.down())) && !checkedBlocks.contains(pos.down())) {
            checkedBlocks.add(pos.down());
            touching(blockStates, world, pos.down(), checkedBlocks);
        }
        if (blockStates.contains(world.getBlockState(pos.east())) && !checkedBlocks.contains(pos.east())) {
            checkedBlocks.add(pos.east());
            touching(blockStates, world, pos.east(), checkedBlocks);
        }
        if (blockStates.contains(world.getBlockState(pos.west())) && !checkedBlocks.contains(pos.west())) {
            checkedBlocks.add(pos.west());
            touching(blockStates, world, pos.west(), checkedBlocks);
        }
        if (blockStates.contains(world.getBlockState(pos.north())) && !checkedBlocks.contains(pos.north())) {
            checkedBlocks.add(pos.north());
            touching(blockStates, world, pos.north(), checkedBlocks);
        }
        if (blockStates.contains(world.getBlockState(pos.south())) && !checkedBlocks.contains(pos.south())) {
            checkedBlocks.add(pos.south());
            touching(blockStates, world, pos.south(), checkedBlocks);
        }
        return checkedBlocks;
    }

}
