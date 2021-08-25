package keyboardcrusader.locations.events;

import keyboardcrusader.locations.api.LocationHelper;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.config.type.StructureInfo;
import keyboardcrusader.locations.generators.NoneNameGenerator;
import net.blay09.mods.waystones.api.GenerateWaystoneNameEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Map;

public class WaystoneEventHandler {
    /**
     * Renames waystones so that they match the location they're in if they're in one
     */
    @SubscribeEvent
    public static void overwriteWaystoneName(final GenerateWaystoneNameEvent event) {
        World world;
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            world = ServerLifecycleHooks.getCurrentServer().getWorld(event.getWaystone().getDimension());
        } else {
            world = Minecraft.getInstance().getRenderViewEntity().getEntityWorld();
        }

        Map.Entry<Long, Location> location = LocationHelper.inLocation(world, world.getDimensionKey(), Vector3d.copyCentered(event.getWaystone().getPos()));
        if (location != null && location.getValue().getSource() == Location.Source.STRUCTURE && !(((StructureInfo) location.getValue().getInfo()).getNameGenerator() instanceof NoneNameGenerator)) {
            event.setName(location.getValue().getName());
        }
    }
}
