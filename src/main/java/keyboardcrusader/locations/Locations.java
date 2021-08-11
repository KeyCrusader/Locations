package keyboardcrusader.locations;

import keyboardcrusader.locations.api.NameGenerators;
import keyboardcrusader.locations.config.LocationsConfig;
import keyboardcrusader.locations.events.forge.ServerForgeEventHandler;
import keyboardcrusader.locations.generators.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, LocationsConfig.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LocationsConfig.serverSpec);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> LocationsConfig.getClothConfig().setParentScreen(parent).build());

        NameGenerators.register(new NoneNameGenerator(new ResourceLocation(Locations.MODID, "none")));
        NameGenerators.register(new VillageNameGenerator(new ResourceLocation(Locations.MODID, "village")));
        NameGenerators.register(new CastleNameGenerator(new ResourceLocation(Locations.MODID, "castle")));
        NameGenerators.register(new UndergroundVillageNameGenerator(new ResourceLocation(Locations.MODID, "underground_village")));
        NameGenerators.register(new MineshaftNameGenerator(new ResourceLocation(Locations.MODID, "mineshaft")));

        if (ModList.get().isLoaded("waystones")) {
            MinecraftForge.EVENT_BUS.register(ServerForgeEventHandler.WaystoneServerForgeEventHandler.class);
        }
        LocationsConfig.SERVER.SELENE_LOADED = ModList.get().isLoaded("selene");
        LocationsConfig.SERVER.ANTIQUE_ATLAS_LOADED = ModList.get().isLoaded("antiqueatlas");
    }
}
