package keyboardcrusader.locations.events.forge;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.LocationEvent;
import keyboardcrusader.locations.api.PlayerLocationEvent;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {
    @SubscribeEvent
    public static void discoverLocation(final LocationEvent.Discover event) {
        if (!(event.getDiscoverer() instanceof ClientPlayerEntity)) return;
        ClientPlayerEntity playerEntity = (ClientPlayerEntity) event.getDiscoverer();
        playerEntity.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, playerEntity.getSoundCategory(), 1.0F, 1.0F );
    }

    @SubscribeEvent
    public static void enter(final PlayerLocationEvent.Enter event) {
        Locations.LOGGER.debug("Entered " + event.getLocation().getName());
    }

    @SubscribeEvent
    public static void leave(final PlayerLocationEvent.Leave event) {
        Locations.LOGGER.debug("Left");
    }
}