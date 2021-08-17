package keyboardcrusader.locations.events.forge;

import com.google.common.collect.Lists;
import hunternif.mc.api.AtlasAPI;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.api.*;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.config.LocationsConfig;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import keyboardcrusader.locations.generators.NoneNameGenerator;
import keyboardcrusader.locations.network.LocationPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerForgeEventHandler {
    /**
     * Keep all known locations after death
     */
    @SubscribeEvent
    public static void playerDeath(final PlayerEvent.Clone event) {
        if (event.getEntity().getEntityWorld().isRemote()) return;

        if (event.isWasDeath()) {
            Map<Long, Location> previousLocations = LocationHelper.getAll(event.getOriginal());

            if (LocationsConfig.COMMON.DEATH_LOCATION.get()) {
                MutableBoundingBox mutableBoundingBox = new MutableBoundingBox(
                        event.getOriginal().getPosition().getX() - 5,
                        event.getOriginal().getPosition().getY() - 5,
                        event.getOriginal().getPosition().getZ() - 5,
                        event.getOriginal().getPosition().getX() + 5,
                        event.getOriginal().getPosition().getY() + 5,
                        event.getOriginal().getPosition().getZ() + 5);
                previousLocations.put(Location.generateID(new ResourceLocation(Locations.MODID, "death"), event.getOriginal().getEntityWorld().getDimensionKey(), event.getOriginal().getPosition()),
                        new Location(
                                event.getOriginal().getPosition(),
                                event.getOriginal().getEntityWorld().getDimensionKey(),
                                "Death",
                                new ResourceLocation(Locations.MODID, "death"),
                                Lists.newArrayList(mutableBoundingBox),
                                mutableBoundingBox,
                                Location.Source.DEATH
                        ));
            }

            // Synced on playerJoin
            LocationHelper.setAll(event.getEntityLiving(), previousLocations);
        }
    }

    /**
     * Syncs player locations to player
     */
    @SubscribeEvent
    public static void playerJoin(final EntityJoinWorldEvent event) {
        if (event.getEntity().getEntityWorld().isRemote()) return;
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getEntity()), new LocationPacket(LocationHelper.getAll(event.getEntity()), LocationPacket.PacketType.SYNC));
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

        for (Map.Entry<Long, Location> deathLocation : LocationHelper.getAll(playerEntity).entrySet()) {
            if (deathLocation.getValue().getSource() == Location.Source.DEATH) {
                if (deathLocation.getValue().isVecInside(playerEntity.getPosition())) {
                    LocationHelper.remove(playerEntity, deathLocation.getKey(), deathLocation.getValue());
                }
            }
        }

        Map.Entry<Long, Location> currentLocation = LocationHelper.inLocation(serverWorld, playerEntity.getPosition());
        if (currentLocation == null) {
            if (LocationHelper.currentLocation(playerEntity) != 0L) {
                LocationHelper.setCurrentLocation(playerEntity, 0L);
            }
        } else {
            if (!LocationHelper.isDiscovered(playerEntity, currentLocation.getKey())) {
                LocationHelper.discover(playerEntity, currentLocation.getKey(), currentLocation.getValue());
            }
            if (!LocationHelper.currentLocation(playerEntity).equals(currentLocation.getKey())) {
                LocationHelper.setCurrentLocation(playerEntity, currentLocation.getKey());
            }
        }
    }

    /**
     * Mixin events for location generation
     */
    @SubscribeEvent
    public static void structureGenerated(final StructureEvent.Created event) {
        Long id = Location.generateID(event.getStructure().getStructure().getRegistryName(), event.getWorld().getDimensionKey(), Location.getPosition(event.getStructure().getComponents().get(0)));

        if (!LocationHelper.isDiscovered(event.getWorld(), id)) {
            Location location = new Location(event.getStructure(), event.getWorld());
            if (!location.getInfo().isDisabled()) {
                LocationHelper.discover(event.getWorld(), id, location);
            }
        }
    }
    @SubscribeEvent
    public static void featureGenerated(final FeatureEvent.Created event) {
        Long id = Location.generateID(event.getFeature().getRegistryName(), event.getWorld().getDimensionKey(), event.getPos());

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
                        Math.max(event.getLocation().getMaxBounds().getXSize(), event.getLocation().getMaxBounds().getZSize()),
                        PointOfInterestManager.Status.ANY).forEach(poi -> {
                            Long id = Location.generateID(poi.getType().getRegistryName(), playerEntity.getEntityWorld().getDimensionKey(), poi.getPos());
                            Location location = new Location(poi.getType(), (ServerWorld) playerEntity.getEntityWorld(), poi.getPos());
                            if (!location.getInfo().isDisabled() && ((POIInfo) location.getInfo()).isInLocation()) {
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

        Map<Long, Location> poiLocations = LocationHelper.getBySource(event.getPlayer(), Location.Source.POI);
        if (!poiLocations.isEmpty()) {
            LocationHelper.remove(event.getPlayer(), poiLocations);
        }
    }
    @SubscribeEvent
    public static void createPOI(final PointOfInterestEvent.Created event) {
        Long id = Location.generateID(event.getType().getRegistryName(), event.getWorld().getDimensionKey(), event.getPos());
        // If the POI should be treated as a feature (not in a location, e.g. nether portal) then add as a location to discover
        if (!LocationsRegistry.POIS.get(event.getType().getRegistryName()).isInLocation()) {
            Location location = new Location(event.getType(), event.getWorld(), event.getPos(), true);
            if (((POIInfo) location.getInfo()).isConnected() && LocationHelper.isTouching(event.getType().getBlockStates(), event.getPos(), event.getWorld())) {
                return;
            }
            LocationHelper.discover(event.getWorld(), id, location);
        }
        else {
            // If it is a location poi (e.g. workstation) then check send to players in a that location
            Map.Entry<Long, Location> currentLocation = LocationHelper.inLocation(event.getWorld(), event.getPos());
            if (currentLocation != null) {
                event.getWorld().getPlayers().forEach(playerEntity -> {
                    if (LocationHelper.currentLocation(playerEntity).equals(currentLocation.getKey())) {
                        if (!LocationHelper.isDiscovered(playerEntity, id)) {
                            Location location = new Location(event.getType(), event.getWorld(), event.getPos());
                            LocationHelper.discover(playerEntity, id, location);
                        }
                    }
                });
            }
        }
    }
    @SubscribeEvent
    public static void removePOI(final PointOfInterestEvent.Removed event) {
        Long id = Location.generateID(event.getType().getRegistryName(), event.getWorld().getDimensionKey(), event.getPos());
        if (!LocationsRegistry.POIS.get(event.getType().getRegistryName()).isInLocation()) {
            if (LocationHelper.isDiscovered(event.getWorld(), id)) {
                Location location = LocationHelper.get(event.getWorld(), id);
                LocationHelper.remove(event.getWorld(), id, location);
            }
        }

        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(playerEntity -> {
            if (LocationHelper.isDiscovered(playerEntity, id)) {
                Location location = LocationHelper.get(playerEntity, id);
                LocationHelper.remove(playerEntity, id, location);
            }
        });
    }

    public static class WaystoneServerForgeEventHandler {
        /**
         * Renames waystones so that they match the location they're in if they're in one
         */
        @SubscribeEvent
        public static void overwriteWaystoneName(final GenerateWaystoneNameEvent event) {
            if (FMLEnvironment.dist == Dist.CLIENT) return;
            ServerWorld world = ServerLifecycleHooks.getCurrentServer().getWorld(event.getWaystone().getDimension());
            Map.Entry<Long, Location> location = LocationHelper.inLocation(world, event.getWaystone().getPos());
            if (location != null && location.getValue().getSource() == Location.Source.STRUCTURE && !(((StructureInfo) location.getValue().getInfo()).getNameGenerator() instanceof NoneNameGenerator)) {
                event.setName(location.getValue().getName());
            }
        }

        /**
         * Adds waystones to the antique atlas when they are used
         */
        @SubscribeEvent
        public static void addWaystoneToAtlas(WaystoneActivatedEvent event) {
            if (!LocationsConfig.COMMON.ANTIQUE_ATLAS_LOADED) return;
            if (event.getPlayer().getEntityWorld().isRemote()) return;

            World world = event.getPlayer().getEntityWorld();
            if (LocationHelper.inLocation(world, event.getWaystone().getPos()) != null) return;

            List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(event.getPlayer());
            for (int atlasID : playerAtlases) {
                if (!AtlasHelper.doesMarkerExist(new ResourceLocation(Locations.MODID, "waystone"), atlasID, world, event.getWaystone().getPos())) {
                    AtlasAPI.getMarkerAPI().putMarker(world, false, atlasID, new ResourceLocation(Locations.MODID, "waystone"), new StringTextComponent(event.getWaystone().getName()), event.getWaystone().getPos().getX(), event.getWaystone().getPos().getZ());
                }
            }
        }

    }

    /**
     * Automatically adds location to all antique atlases in inventory if location has an atlasMarker set in configs
     */
    @SubscribeEvent
    public static void createAtlasMarker(final PlayerLocationEvent.Enter event) {
        if (event.getPlayer().getEntityWorld().isRemote()) return;
        if (LocationsConfig.COMMON.ANTIQUE_ATLAS_LOADED && event.getLocation().getInfo().hasAtlasMarker()) {
            PlayerEntity playerEntity = event.getPlayer();
            List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(playerEntity);
            for (int atlasID : playerAtlases) {
                if (!AtlasHelper.doesMarkerExist(event.getLocation().getInfo().getAtlasMarker(), atlasID, playerEntity.getEntityWorld(), event.getLocation().getPosition())) {
                    AtlasAPI.getMarkerAPI().putMarker(playerEntity.getEntityWorld(), false, atlasID, event.getLocation().getInfo().getAtlasMarker(), new StringTextComponent(event.getLocation().getName()), event.getLocation().getPosition().getX(), event.getLocation().getPosition().getZ());
                }
            }
        }
    }
}
