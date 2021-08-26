package keyboardcrusader.locations.events.forge;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.*;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.config.type.StructureInfo;
import keyboardcrusader.locations.network.LocationPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerForgeEventHandler {
    /**
     * Keep all known locations after death
     */
    @SubscribeEvent
    public static void playerDeath(final PlayerEvent.Clone event) {
        if (event.getPlayer().getEntityWorld().isRemote()) return;

        Map<Long, Location> previousLocations = LocationHelper.getAll(event.getOriginal());
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(event.getOriginal().getPosition()).grow(5);
        Location deathLocation = new Location(
                event.getOriginal().getPosition(),
                event.getOriginal().getEntityWorld().getDimensionKey(),
                "Death",
                new ResourceLocation(Locations.MODID, "death"),
                Sets.newHashSet(axisAlignedBB),
                axisAlignedBB,
                Location.Source.DEATH,
                true,
                true
        );
        if (!deathLocation.getInfo().isDisabled() && event.isWasDeath()) {
                previousLocations.put(LocationHelper.generateID(new ResourceLocation(Locations.MODID, "death"), event.getOriginal().getEntityWorld().getDimensionKey(), event.getOriginal().getPosition()),
                        deathLocation);
        }
        LocationHelper.setAll(event.getEntityLiving(), previousLocations);
    }

    @SubscribeEvent
    public static void playerSetSpawn(final PlayerSetSpawnEvent event) {
        if (event.getPlayer().getEntityWorld().isRemote()) return;

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(event.getNewSpawn());
        Location newHomeLocation = new Location(
                event.getNewSpawn(),
                event.getSpawnWorld(),
                "Spawn",
                new ResourceLocation(Locations.MODID, "spawn"),
                Sets.newHashSet(axisAlignedBB),
                axisAlignedBB,
                Location.Source.SPAWN,
                true,
                false
        );
        if (!newHomeLocation.getInfo().isDisabled()) {
            Long id = (long) event.getSpawnWorld().hashCode();

            if (LocationHelper.isDiscovered(event.getPlayer(), id)) {
                if (!LocationHelper.get(event.getPlayer(), id).equals(newHomeLocation)) {
                    LocationHelper.update(event.getPlayer(), id, newHomeLocation);
                }
            } else {
                LocationHelper.discover(event.getPlayer(), id, newHomeLocation);
            }
        }
    }


    /**
     * Syncs player locations to player
     */
    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getPlayer().world.isRemote) return;
        syncLocations((ServerPlayerEntity) event.getPlayer());
    }
    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.getPlayer().world.isRemote) return;
        syncLocations((ServerPlayerEntity) event.getPlayer());
    }
    @SubscribeEvent
    public static void playerRespawn(final PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.getPlayer().world.isRemote) return;
        syncLocations((ServerPlayerEntity) event.getPlayer());
    }
    private static void syncLocations(final ServerPlayerEntity playerEntity)
    {
        // Checks against server in case anything was removed whilst offline/changing dimension/dead
        Map<Long, Location> allLocations = LocationHelper.getAll(playerEntity);
        Map<Long, Location> existingLocations = new HashMap<>();
        for (Map.Entry<Long, Location> mapEntry : allLocations.entrySet()) {
            if (LocationHelper.isDiscovered(playerEntity.getServerWorld(), mapEntry.getKey())) {
                existingLocations.put(mapEntry.getKey(), mapEntry.getValue());
            }
        }

        LocationHelper.setAll(playerEntity, existingLocations);
        PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> playerEntity), new LocationPacket(existingLocations, LocationPacket.PacketType.SYNC));
    }


    /**
     * Checks player location against all locations known to server when player moves to see if they are in a location
     */
    @SubscribeEvent
    public static void playerMove(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().getEntityWorld().isRemote()) return;
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getEntityLiving();
        ServerWorld serverWorld = playerEntity.getServerWorld();

        if (playerEntity.isSpectator()) return;

        for (Map.Entry<Long, Location> deathLocation : LocationHelper.getBySource(playerEntity, Location.Source.DEATH).entrySet()) {
            if (deathLocation.getValue().isVecInside(playerEntity.getPositionVec())) {
                LocationHelper.remove(playerEntity, deathLocation.getKey());
            }
        }

        Map.Entry<Long, Location> currentLocation = LocationHelper.inLocation(serverWorld, serverWorld.getDimensionKey(), playerEntity.getPositionVec());
        if (currentLocation == null) {
            if (LocationHelper.currentLocation(playerEntity) != 0L) {
                LocationHelper.setCurrentLocation(playerEntity, 0L, false);
            }
        } else {
            boolean discovery = false;
            if (!LocationHelper.isDiscovered(playerEntity, currentLocation.getKey())) {
                LocationHelper.discover(playerEntity, currentLocation.getKey(), currentLocation.getValue());
                discovery = true;
            }
            if (!LocationHelper.currentLocation(playerEntity).equals(currentLocation.getKey())) {
                LocationHelper.setCurrentLocation(playerEntity, currentLocation.getKey(), discovery);
            }
        }
    }

    /**
     * Checks when a structure is loaded if it matches the one on records and updates if necessary
     */
    @SubscribeEvent
    public static void structureLoad(ChunkEvent.Load event) {
        if (!(event.getWorld() instanceof ServerWorld)) return;
        ServerLifecycleHooks.getCurrentServer().deferTask(() -> {
            ServerWorld world = ((ServerWorld) event.getWorld());
            for (StructureStart<?> structureStart : event.getChunk().getStructureStarts().values()) {
                if (structureStart.isValid()) {
                    Long id = LocationHelper.generateID(structureStart.getStructure().getRegistryName(), world.getDimensionKey(), structureStart.getPos());

                    Location location = new Location(structureStart, world);
                    if (!LocationHelper.isDiscovered(world, id)) {
                        if (!location.getInfo().isDisabled()) {
                            LocationHelper.discover(world, id, location);
                        }
                    }
                    else {
                        Location storedLocation = LocationHelper.get(world, id);
                        if (!storedLocation.equals(location)) {
                            LocationHelper.updateGlobal(world, new HashMap<Long, Location>(){{put(id, location);}});
                        }
                    }
                }
            }
        });
    }

    /**
     * Mixin events for feature generation
     */
    @SubscribeEvent
    public static void featureGenerated(final FeatureEvent.Created event) {
        Long id = LocationHelper.generateID(event.getFeature().getRegistryName(), event.getWorld().getDimensionKey(), event.getPos());

        if (!LocationHelper.isDiscovered(event.getWorld(), id)) {
            Location location = new Location(event.getFeature(), event.getWorld(), event.getPos());
            if (!location.getInfo().isDisabled()) {
                LocationHelper.discover(event.getWorld(), id, location);
            }
        }
    }

    /**
     * Send and sync POI info to player if they enter or leave a location with points of interest
     */
    @SubscribeEvent
    public static void enterLocation(final PlayerLocationEvent.Enter event) {
        if (event.getPlayer().getEntityWorld().isRemote()) return;

        if (event.getLocation().getSource() == Location.Source.STRUCTURE) {
            if (((StructureInfo)event.getLocation().getInfo()).hasPOIs()) {
                PlayerEntity playerEntity = event.getPlayer();
                // Send POIs
                Map<Long, Location> poiMap = new HashMap<>();
                ((ServerWorld) event.getPlayer().getEntityWorld()).getPointOfInterestManager().getInSquare(
                        PointOfInterestType.MATCH_ANY,
                        new BlockPos(event.getLocation().getMaxBounds().minX, event.getLocation().getMaxBounds().minY, event.getLocation().getMaxBounds().minZ),
                        (int) Math.max(event.getLocation().getMaxBounds().getXSize(), event.getLocation().getMaxBounds().getZSize()),
                        PointOfInterestManager.Status.ANY).forEach(poi -> {
                            Long id = LocationHelper.generateID(poi.getType().getRegistryName(), playerEntity.getEntityWorld().getDimensionKey(), poi.getPos());
                            Location location = new Location(poi.getType(), (ServerWorld) playerEntity.getEntityWorld(), poi.getPos());
                            if (!location.getInfo().isDisabled() && !location.isPermanent()) {
                                poiMap.put(id, location);
                            }
                        });
                LocationHelper.discover(playerEntity, poiMap);
            }
        }
    }
    @SubscribeEvent
    public static void leaveLocation(final PlayerLocationEvent.Leave event) {
        if (event.getPlayer().getEntityWorld().isRemote()) return;

        Map<Long, Location> poiLocations = LocationHelper.getBySource(event.getPlayer(), Location.Source.POI).entrySet().stream()
                .filter(locationEntry -> !locationEntry.getValue().isPermanent())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!poiLocations.isEmpty()) {
            LocationHelper.remove(event.getPlayer(), Lists.newArrayList(poiLocations.keySet()));
        }
    }
    @SubscribeEvent
    public static void createPOI(final PointOfInterestEvent.Created event) {
        Location location = new Location(event.getType(), event.getWorld(), event.getPos());
        long id = LocationHelper.generateID(event.getType().getRegistryName(), event.getWorld().getDimensionKey(), location.getPosition());

        // If the POI should be treated as a feature (not in a location, e.g. nether portal) then add as a location to discover
        if (location.isPermanent()) {
            if (!LocationHelper.isDiscovered(event.getWorld(), id)) {
                LocationHelper.discover(event.getWorld(), id, location);
            }
        }
        else {
            // If it is a location poi (e.g. workstation) then check send to players in a that location
            Map.Entry<Long, Location> currentLocation = LocationHelper.inLocation(event.getWorld(), event.getWorld().getDimensionKey(), Vector3d.copyCentered(event.getPos()));
            if (currentLocation != null) {
                event.getWorld().getPlayers().forEach(playerEntity -> {
                    if (LocationHelper.currentLocation(playerEntity).equals(currentLocation.getKey())) {
                        if (!LocationHelper.isDiscovered(playerEntity, id)) {
                            LocationHelper.discover(playerEntity, id, location);
                        }
                    }
                });
            }
        }

    }
    @SubscribeEvent
    public static void removePOI(final PointOfInterestEvent.Removed event) {
        Location positionLocation = new Location(event.getType(), event.getWorld(), event.getPos());
        long id = LocationHelper.generateID(event.getType().getRegistryName(), event.getWorld().getDimensionKey(), positionLocation.getPosition());

        LocationHelper.removeGlobal(event.getWorld(), Lists.newArrayList(id));
    }
}
