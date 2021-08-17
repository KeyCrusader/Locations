package keyboardcrusader.locations.events.mod;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.api.NameGenerator;
import keyboardcrusader.locations.capability.LocationsCap;
import keyboardcrusader.locations.config.*;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.MapInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import keyboardcrusader.locations.generators.*;
import keyboardcrusader.locations.network.PacketHandler;
import net.mehvahdjukaar.selene.map.CustomDecorationType;
import net.mehvahdjukaar.selene.map.MapDecorationHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEventHandler {
    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            LocationsCap.register();
            PacketHandler.register();
        });
    }

    @SubscribeEvent
    public static void loadComplete(final FMLLoadCompleteEvent event) {

        // Register all structures
        Registry.STRUCTURE_FEATURE.forEach(structure -> LocationsRegistry.STRUCTURES.registerIfEmpty((StructureInfo) new StructureInfo().setRegistryName(structure.getRegistryName())));
        // Register useful map markers
        for (int i = 8; i <= 26; i++) {
            LocationsRegistry.MAP_MARKERS.registerIfEmpty((MapInfo) new MapInfo().setRegistryName("minecraft", MapDecoration.Type.values()[i].name().toLowerCase()));
        }

        // If Selene is loaded register all custom map markers
        if (LocationsConfig.COMMON.SELENE_LOADED) {
            Map<String, CustomDecorationType<?, ?>> DECORATION_TYPES = ObfuscationReflectionHelper.getPrivateValue(MapDecorationHandler.class, null, "DECORATION_TYPES");
            if (DECORATION_TYPES != null) {
                DECORATION_TYPES.values().forEach(customDecorationType -> LocationsRegistry.MAP_MARKERS.registerIfEmpty((MapInfo) new MapInfo().setRegistryName(customDecorationType.getId())));
            }
        }

        // Register all features
        Registry.FEATURE.forEach(feature -> LocationsRegistry.FEATURES.registerIfEmpty((FeatureInfo) new FeatureInfo().setRegistryName(feature.getRegistryName())));

        // Register all POIs
        Registry.POINT_OF_INTEREST_TYPE.forEach(pointOfInterestType -> LocationsRegistry.POIS.registerIfEmpty((POIInfo) new POIInfo().setRegistryName(pointOfInterestType.getRegistryName())));

        LocationsConfig.save();
    }

    @SubscribeEvent
    public static void readConfig(final ModConfig.Loading event) {
        if (event.getConfig().getModId().equals(Locations.MODID)) {
            Locations.LOGGER.debug("Read config event");
            if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
                LocationsConfig.CLIENT.deserialize();
            }
            if (event.getConfig().getType() == ModConfig.Type.COMMON) {
                LocationsConfig.COMMON.deserialize();
            }
        }
    }

    @SubscribeEvent
    public static void createRegistries(final RegistryEvent.NewRegistry event) {
        LocationsRegistry.NAME_GENERATORS = new RegistryBuilder<NameGenerator>()
                .setName(new ResourceLocation(Locations.MODID, "name_generators"))
                .setType(NameGenerator.class)
                .setIDRange(0, Integer.MAX_VALUE - 1)
                .setDefaultKey(new ResourceLocation(Locations.MODID, "none"))
                .create();

        LocationsRegistry.STRUCTURES = new ConfigRegistry<>((StructureInfo) new StructureInfo().setRegistryName("default"));
        LocationsRegistry.MAP_MARKERS = new ConfigRegistry<>((MapInfo) new MapInfo().setRegistryName("default"));
        LocationsRegistry.FEATURES = new ConfigRegistry<>((FeatureInfo) new FeatureInfo().setRegistryName("default"));
        LocationsRegistry.POIS = new ConfigRegistry<>((POIInfo) new POIInfo().setRegistryName("default"));
    }

    @SubscribeEvent
    public static void setNameGenerators(final RegistryEvent.Register<NameGenerator> event) {
        event.getRegistry().register(new CastleNameGenerator("Castle").setRegistryName(Locations.MODID, "castle"));
        event.getRegistry().register(new MineshaftNameGenerator("Mineshaft").setRegistryName(Locations.MODID, "mineshaft"));
        event.getRegistry().register(new NoneNameGenerator("None").setRegistryName(Locations.MODID, "none"));
        event.getRegistry().register(new UndergroundVillageNameGenerator("Underground Village").setRegistryName(Locations.MODID, "village_underground"));
        event.getRegistry().register(new VillageNameGenerator("Surface Village").setRegistryName(Locations.MODID, "village"));
        event.getRegistry().register(new OutpostNameGenerator("Outpost").setRegistryName(Locations.MODID, "outpost"));
    }
}
