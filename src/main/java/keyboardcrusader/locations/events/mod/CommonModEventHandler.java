package keyboardcrusader.locations.events.mod;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.api.NameGenerator;
import keyboardcrusader.locations.capability.LocationsCap;
import keyboardcrusader.locations.config.Config;
import keyboardcrusader.locations.config.ConfigRegistry;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.LocationInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import keyboardcrusader.locations.generators.*;
import keyboardcrusader.locations.network.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.RegistryBuilder;

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
        LocationsRegistry.LOCATIONS.setDefault(new LocationInfo().setRegistryName(Locations.MODID, "default"));
        LocationsRegistry.LOCATIONS.registerIfEmpty(new LocationInfo().setRegistryName(Locations.MODID, "death"));
        LocationsRegistry.LOCATIONS.registerIfEmpty(new LocationInfo().setRegistryName(Locations.MODID, "spawn"));

        LocationsRegistry.STRUCTURES.setDefault((StructureInfo) new StructureInfo().setRegistryName(Locations.MODID, "default"));
        LocationsRegistry.FEATURES.setDefault((FeatureInfo) new FeatureInfo().setRegistryName(Locations.MODID, "default"));
        LocationsRegistry.POIS.setDefault((POIInfo) new POIInfo().setRegistryName(Locations.MODID, "default"));


        // Register all structures
        Registry.STRUCTURE_FEATURE.forEach(structure -> LocationsRegistry.STRUCTURES.registerIfEmpty((StructureInfo) new StructureInfo().setRegistryName(structure.getRegistryName())));

        // Register all features
        Registry.FEATURE.forEach(feature -> LocationsRegistry.FEATURES.registerIfEmpty((FeatureInfo) new FeatureInfo().setRegistryName(feature.getRegistryName())));

        // Register all POIs
        Registry.POINT_OF_INTEREST_TYPE.forEach(pointOfInterestType -> LocationsRegistry.POIS.registerIfEmpty((POIInfo) new POIInfo().setRegistryName(pointOfInterestType.getRegistryName())));

        Config.save();
    }

    @SubscribeEvent
    public static void readConfig(final ModConfig.Loading event) {
        if (event.getConfig().getModId().equals(Locations.MODID)) {
            if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
                Config.CLIENT.deserialize();
            }
            if (event.getConfig().getType() == ModConfig.Type.COMMON) {
                Config.COMMON.deserialize();
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

        LocationsRegistry.LOCATIONS = new ConfigRegistry<>();
        LocationsRegistry.STRUCTURES = new ConfigRegistry<>();
        LocationsRegistry.FEATURES = new ConfigRegistry<>();
        LocationsRegistry.POIS = new ConfigRegistry<>();
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
