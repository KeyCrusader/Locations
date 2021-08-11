package keyboardcrusader.locations.api;

import keyboardcrusader.locations.capability.ILocationsCap;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.capability.LocationsCap;
import keyboardcrusader.locations.config.LocationInfo;
import keyboardcrusader.locations.config.LocationsConfig;
import keyboardcrusader.locations.events.IconSpriteUploader;
import keyboardcrusader.locations.network.CurrentLocationPacket;
import keyboardcrusader.locations.network.LocationPacket;
import keyboardcrusader.locations.network.PacketHandler;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class LocationHelper {
    private static ILocationsCap getCapability(ICapabilityProvider capabilityProvider) {
        return capabilityProvider.getCapability(LocationsCap.LOCATIONS_CAPABILITY).orElseThrow(() -> new IllegalArgumentException(capabilityProvider.toString()+" missing locations capability"));
    }

    public static Long currentLocation(PlayerEntity playerEntity) {
        return getCapability(playerEntity).currentLocation();
    }
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

    public static List<Location> get(ICapabilityProvider capabilityProvider) {
        return getCapability(capabilityProvider).get();
    }
    public static Location get(ICapabilityProvider capabilityProvider, Long id) {
        return getCapability(capabilityProvider).get(id);
    }
    public static List<Location> getBySource(ICapabilityProvider capabilityProvider, Location.Source source) {
        List<Location> locationList = new ArrayList<>();
        for (Location location : getCapability(capabilityProvider).get()) {
            if (location.getSource() == source) {
                locationList.add(location);
            }
        }
        return locationList;
    }

    public static void set(ICapabilityProvider capabilityProvider, List<Location> locations) {
        getCapability(capabilityProvider).set(locations);
    }

    public static boolean isDiscovered(ICapabilityProvider capabilityProvider, Location location) {
        return getCapability(capabilityProvider).isDiscovered(location);
    }
    public static boolean isDiscovered(ICapabilityProvider capabilityProvider, Long id) {
        return getCapability(capabilityProvider).isDiscovered(id);
    }

    public static void discover(ICapabilityProvider capabilityProvider, Location location) {
        MinecraftForge.EVENT_BUS.post(new LocationEvent.Discover(location.getType(), capabilityProvider, location));
        getCapability(capabilityProvider).discover(location);

        if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
            PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(location, LocationPacket.PacketType.DISCOVER));
        }
    }
    public static void update(ICapabilityProvider capabilityProvider, Location location) {
        MinecraftForge.EVENT_BUS.post(new LocationEvent.Update(location.getType(), capabilityProvider, location));
        getCapability(capabilityProvider).update(location);
        if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
            PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(location, LocationPacket.PacketType.UPDATE));
        }
    }
    public static void remove(ICapabilityProvider capabilityProvider, Location location) {
        MinecraftForge.EVENT_BUS.post(new LocationEvent.Remove(location.getType(), capabilityProvider, location));
        getCapability(capabilityProvider).remove(location);
        if (capabilityProvider instanceof PlayerEntity && !(((PlayerEntity) capabilityProvider).getEntityWorld().isRemote())) {
            PacketHandler.HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) capabilityProvider), new LocationPacket(location, LocationPacket.PacketType.REMOVE));
        }
    }

    public static LocationInfo getLocationInfo(Location location) {
        return LocationsConfig.LOCATION_INFO.get(location.getType().getNamespace()).get(location.getType().getPath());
    }
    public static LocationInfo getLocationInfo(Structure<?> structure) {
        return LocationsConfig.LOCATION_INFO.get(structure.getRegistryName().getNamespace()).get(structure.getRegistryName().getPath());
    }

    public static Location inLocation(BlockPos pos, ICapabilityProvider capabilityProvider) {
        for (Location location : get(capabilityProvider)) {
            if (location.getBounds().isVecInside(pos)) {
                return location;
            }
        }
        return null;
    }

    public static TextureAtlasSprite getLocationIcon(Location location) {
        return IconSpriteUploader.LOCATION_ICONS_SPRITES.getSprite(location);
    }
}
