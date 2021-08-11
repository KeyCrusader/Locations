package keyboardcrusader.locations.events.mod;

import hunternif.mc.impl.atlas.registry.MarkerType;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.config.LocationsConfig;
import keyboardcrusader.locations.events.IconSpriteUploader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventHandler {
    @SubscribeEvent
    public static void particleFactory(final ParticleFactoryRegisterEvent event) {
        IconSpriteUploader.LOCATION_ICONS_SPRITES  = new IconSpriteUploader(Minecraft.getInstance().getTextureManager());
        ((IReloadableResourceManager) Minecraft.getInstance().getResourceManager()).addReloadListener(IconSpriteUploader.LOCATION_ICONS_SPRITES);
    }

    @SubscribeEvent
    public static void initClient(final FMLClientSetupEvent event) {
        if (LocationsConfig.SERVER.ANTIQUE_ATLAS_LOADED) {
            MarkerType.register(new ResourceLocation(Locations.MODID, "waystone"), new MarkerType(new ResourceLocation(Locations.MODID, "textures/markers/waystone.png")));
            MarkerType.register(new ResourceLocation(Locations.MODID, "mineshaft"), new MarkerType(new ResourceLocation(Locations.MODID, "textures/markers/mineshaft.png")));
            MarkerType.register(new ResourceLocation(Locations.MODID, "ship"), new MarkerType(new ResourceLocation(Locations.MODID, "textures/markers/ship.png")));
        }
    }
}
