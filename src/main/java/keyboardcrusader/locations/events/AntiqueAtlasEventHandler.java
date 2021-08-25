package keyboardcrusader.locations.events;

import hunternif.mc.api.AtlasAPI;
import hunternif.mc.api.client.AtlasClientAPI;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.AtlasHelper;
import keyboardcrusader.locations.api.LocationEvent;
import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.api.PlayerLocationEvent;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.config.type.POIInfo;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class AntiqueAtlasEventHandler {
    /**
     * Automatically adds location to all antique atlases in inventory if location has an atlasMarker set in configs
     */
    @SubscribeEvent
    public static void locationEntered(final PlayerLocationEvent.Enter event) {
        if (!event.getPlayer().getEntityWorld().isRemote()) return;
        if (event.isDiscovery()) return; // Stops double creation of markers
        updateAtlasMarker(event.getPlayer(), event.getLocation(), false);
    }
    @SubscribeEvent
    public static void locationDiscovered(final LocationEvent.Discover event) {
        if (!(event.getDiscoverer() instanceof ClientPlayerEntity)) return;
        updateAtlasMarker((PlayerEntity) event.getDiscoverer(), event.getLocation(), false);
    }
    @SubscribeEvent
    public static void locationUpdated(final LocationEvent.Update event) {
        if (!(event.getDiscoverer() instanceof ClientPlayerEntity)) return;
        updateAtlasMarker((PlayerEntity) event.getDiscoverer(), event.getLocation(), false);
        updateAtlasMarker((PlayerEntity) event.getDiscoverer(), event.getOldLocation(), true);
    }
    @SubscribeEvent
    public static void locationRemoved(final LocationEvent.Remove event) {
        if (!(event.getDiscoverer() instanceof ClientPlayerEntity)) return;
        updateAtlasMarker((PlayerEntity) event.getDiscoverer(), event.getLocation(), true);
    }

    private static void updateAtlasMarker(PlayerEntity playerEntity, Location location, boolean delete) {
        if (location.getInfo().hasAtlasMarker()) {
            List<Integer> playerAtlases = AtlasAPI.getPlayerAtlases(playerEntity);
            for (int atlasID : playerAtlases) {
                List<Integer> markerList = AtlasHelper.getMarkerFromLocation(location.getInfo().getAtlasMarker(), atlasID, playerEntity.getEntityWorld(), location.getPosition());
                for (int markerID : markerList) {
                    if (delete) {
                        AtlasClientAPI.getMarkerAPI().deleteMarker(playerEntity.getEntityWorld(), atlasID, markerID);
                    }
                }

                if (markerList.isEmpty() && !delete) {
                    AtlasClientAPI.getMarkerAPI().putMarker(playerEntity.getEntityWorld(), false, atlasID, location.getInfo().getAtlasMarker(), new StringTextComponent(location.getName()), location.getPosition().getX(), location.getPosition().getZ());
                }
            }
        }
    }
}
