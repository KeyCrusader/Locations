package keyboardcrusader.locations;

import keyboardcrusader.locations.config.Config;
import keyboardcrusader.locations.config.ConfigScreen;
import keyboardcrusader.locations.events.AntiqueAtlasEventHandler;
import keyboardcrusader.locations.events.WaystoneEventHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Locations.MODID)
public class Locations {

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "locations";

    public Locations() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);

        Config.COMMON.WAYSTONES_LOADED = ModList.get().isLoaded("waystones");
        if (Config.COMMON.WAYSTONES_LOADED) MinecraftForge.EVENT_BUS.register(WaystoneEventHandler.class);

        Config.COMMON.ANTIQUE_ATLAS_LOADED = ModList.get().isLoaded("antiqueatlas");
        if (Config.COMMON.ANTIQUE_ATLAS_LOADED) MinecraftForge.EVENT_BUS.register(AntiqueAtlasEventHandler.class);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ConfigScreen::registerModsPage);
    }
}
