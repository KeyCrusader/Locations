package keyboardcrusader.locations.events.forge;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.LocationEvent;
import keyboardcrusader.locations.api.PlayerLocationEvent;
import keyboardcrusader.locations.capability.Location;
import keyboardcrusader.locations.config.Config;
import keyboardcrusader.locations.screens.OverlayScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler {
    private static final OverlayScreen OVERLAY_SCREEN = new OverlayScreen();

    @SubscribeEvent
    public static void discoverLocation(final LocationEvent.Discover event) {
        if (!(event.getDiscoverer() instanceof ClientPlayerEntity)) return;
        if (event.getLocation().getSource() == Location.Source.STRUCTURE || event.getLocation().getSource() == Location.Source.FEATURE) {
            ClientPlayerEntity playerEntity = (ClientPlayerEntity) event.getDiscoverer();
            playerEntity.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, playerEntity.getSoundCategory(), 1.0F, 1.0F);
        }
        if (event.getLocation().isPermanent()) {
            OVERLAY_SCREEN.showMessage("Discovered " + event.getLocation().getName(), 2);
        }
    }

    @SubscribeEvent
    public static void enterLocation(final PlayerLocationEvent.Enter event) {
        OVERLAY_SCREEN.showMessage(event.getLocation().getName(), 1);
    }

    @SubscribeEvent
    public static void renderOverlay(final RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        if (Config.CLIENT.SHOW_SCREEN.get()) {
            OVERLAY_SCREEN.render(event.getMatrixStack());
        }
    }
    @SubscribeEvent
    public static void renderTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.CLIENT) {
            OVERLAY_SCREEN.doTick();
        }
    }
}