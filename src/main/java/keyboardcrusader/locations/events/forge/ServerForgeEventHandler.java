package keyboardcrusader.locations.events.forge;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.marker.DimensionMarkersData;
import hunternif.mc.impl.atlas.marker.Marker;
import hunternif.mc.impl.atlas.marker.MarkersData;
import hunternif.mc.impl.atlas.registry.MarkerType;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.api.PlayerLocationEvent;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.config.LocationInfo;
import keyboardcrusader.locations.config.LocationsConfig;
import keyboardcrusader.locations.generators.NoneNameGenerator;
import keyboardcrusader.locations.network.LocationPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.blay09.mods.waystones.api.WaystoneActivatedEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerForgeEventHandler {
    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event) {
        if (event.getEntity().getEntityWorld().isRemote()) return;

        if (event.isWasDeath()) {
            List<Location> previousLocations = LocationHelper.get(event.getOriginal());
            if (LocationsConfig.SERVER.DEATH_LOCATION.get()) {
                previousLocations.add(new Location(
                        event.getOriginal().getPosition(),
                        event.getOriginal().getEntityWorld().getDimensionKey(),
                        "Death",
                        new ResourceLocation("death"),
                        new MutableBoundingBox(
                                event.getOriginal().getPosition().getX() - 5,
                                event.getOriginal().getPosition().getY() - 5,
                                event.getOriginal().getPosition().getZ() - 5,
                                event.getOriginal().getPosition().getX() + 5,
                                event.getOriginal().getPosition().getY() + 5,
                                event.getOriginal().getPosition().getZ() + 5),
                        Location.Source.DEATH
                ));
            }

            // Synced on playerJoin
            LocationHelper.set(event.getEntityLiving(), previousLocations);
        }
    }

    @SubscribeEvent
    public static void playerJoin(final EntityJoinWorldEvent event) {
        if (event.getEntity().getEntityWorld().isRemote()) return;
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getEntity()), new LocationPacket(LocationHelper.get(event.getEntity()), LocationPacket.PacketType.SYNC));
    }

    @SubscribeEvent
    public static void playerMoveEvent(final LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().getEntityWorld().isRemote()) return;
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        ServerPlayerEntity playerEntity = (ServerPlayerEntity) event.getEntityLiving();
        ServerWorld serverWorld = playerEntity.getServerWorld();

        List<Location> deathLocations = LocationHelper.getBySource(playerEntity, Location.Source.DEATH);
        for (Location location : deathLocations) {
            if (location.getBounds().isVecInside(playerEntity.getPosition())) {
                LocationHelper.remove(playerEntity, location);
            }
        }

        long currentLocation = 0L;
        for (Structure<?> structureType : Registry.STRUCTURE_FEATURE) {
            LocationInfo locationInfo = LocationHelper.getLocationInfo(structureType);
            if (locationInfo.isDisabled()) continue;
            StructureStart<?> structure = serverWorld.getStructureManager().getStructureStart(playerEntity.getPosition(), !locationInfo.useSquareBounds(), structureType);

            if (structure.isValid()) {
                Location location;
                long id = Location.generateID(structure.getPos(), serverWorld.getDimensionKey(), structureType.getRegistryName());
                if (!LocationHelper.isDiscovered(serverWorld, id)) {
                    location = new Location(structure.getPos(), serverWorld.getDimensionKey(), locationInfo.getNameGenerator().generateName(structureType.getRegistryName(), serverWorld.getBiome(structure.getPos()).getTemperature()), structureType, structure.getBoundingBox(), Location.Source.WORLD);
                    LocationHelper.discover(serverWorld, location);
                }
                else {
                    location = LocationHelper.get(serverWorld, id);
                }

                if (!LocationHelper.isDiscovered(playerEntity, location)) {
                    LocationHelper.discover(playerEntity, location);
                    playerEntity.giveExperiencePoints(MathHelper.ceil(5.0F * LocationHelper.getLocationInfo(location).getXPModifier()));
                }

                currentLocation = location.getID();
            }
        }

        if (!LocationHelper.currentLocation(playerEntity).equals(currentLocation)) {
            LocationHelper.setCurrentLocation(playerEntity, currentLocation);
        }
    }

    @SubscribeEvent
    public static void playerEnterLocationEvent(final PlayerLocationEvent.Enter event) {
        if (event.getPlayer().getEntityWorld().isRemote()) return;
        if (!LocationsConfig.SERVER.ANTIQUE_ATLAS_LOADED) return;
        if (LocationHelper.getLocationInfo(event.getLocation()).getAtlasMarker().equals(new ResourceLocation(Locations.MODID, "none"))) return;

        List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(event.getPlayer());
        for (int atlasID : playerAtlases) {
            if (!doesMarkerExist(LocationHelper.getLocationInfo(event.getLocation()).getAtlasMarker(), atlasID, event.getPlayer().getEntityWorld(), event.getLocation().getCenter())) {
                AtlasAPI.getMarkerAPI().putMarker(event.getPlayer().getEntityWorld(), false, atlasID, LocationHelper.getLocationInfo(event.getLocation()).getAtlasMarker(), new StringTextComponent(event.getLocation().getName()), event.getLocation().getCenter().getX(), event.getLocation().getCenter().getZ());
            }
        }


    }

    public static class WaystoneServerForgeEventHandler {
        @SubscribeEvent
        public static void overwriteWaystoneName(GenerateWaystoneNameEvent event) {
            ServerWorld world = ServerLifecycleHooks.getCurrentServer().getWorld(event.getWaystone().getDimension());
            Location location = LocationHelper.inLocation(event.getWaystone().getPos(), world);
            if (location != null && !(LocationHelper.getLocationInfo(location).getNameGenerator() instanceof NoneNameGenerator)) {
                event.setName(location.getName());
            }
        }

        @SubscribeEvent
        public static void addWaystoneToAtlas(WaystoneActivatedEvent event) {
            if (!LocationsConfig.SERVER.ANTIQUE_ATLAS_LOADED) return;
            if (event.getPlayer().getEntityWorld().isRemote()) return;

            World world = event.getPlayer().getEntityWorld();
            if (LocationHelper.inLocation(event.getWaystone().getPos(), world) != null) return;

            List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(event.getPlayer());
            for (int atlasID : playerAtlases) {
                if (!doesMarkerExist(new ResourceLocation(Locations.MODID, "waystone"), atlasID, world, event.getWaystone().getPos())) {
                    AtlasAPI.getMarkerAPI().putMarker(event.getPlayer().getEntityWorld(), false, atlasID, new ResourceLocation(Locations.MODID, "waystone"), new StringTextComponent(event.getWaystone().getName()), event.getWaystone().getPos().getX(), event.getWaystone().getPos().getZ());
                }
            }
        }
    }

    private static boolean doesMarkerExist(ResourceLocation markerType, int atlasID, World world, BlockPos pos) {
        DimensionMarkersData data = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world)
                .getMarkersDataInWorld(world.getDimensionKey());

        List<Marker> markers = data.getMarkersAtChunk((pos.getX() >> 4) / MarkersData.CHUNK_STEP, (pos.getZ() >> 4) / MarkersData.CHUNK_STEP);
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker.getType().equals(markerType)) {
                    // Found the marker.
                    return true;
                }
            }
        }
        return false;
    }
}
