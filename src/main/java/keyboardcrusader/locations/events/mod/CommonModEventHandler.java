package keyboardcrusader.locations.events.mod;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.capability.LocationsCap;
import keyboardcrusader.locations.config.LocationInfo;
import keyboardcrusader.locations.config.LocationsConfig;
import keyboardcrusader.locations.events.forge.ServerForgeEventHandler;
import keyboardcrusader.locations.network.PacketHandler;
import net.mehvahdjukaar.selene.map.CustomDecorationType;
import net.mehvahdjukaar.selene.map.MapDecorationHandler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@Mod.EventBusSubscriber(modid = Locations.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEventHandler {
    private static Map<String, CustomDecorationType<?, ?>> DECORATION_TYPES;

    @SubscribeEvent
    public static void loadComplete(final FMLLoadCompleteEvent event) {

        boolean configChanged = false;
        for (Structure<?> location : Registry.STRUCTURE_FEATURE) {
            if (!LocationsConfig.LOCATION_INFO.containsKey(location.getRegistryName().getNamespace())) {
                LocationsConfig.LOCATION_INFO.put(location.getRegistryName().getNamespace(), new TreeMap<>(Comparator.comparing(String::toLowerCase)));
            }
            if (!LocationsConfig.LOCATION_INFO.get(location.getRegistryName().getNamespace()).containsKey(location.getRegistryName().getPath())) {
                LocationsConfig.LOCATION_INFO.get(location.getRegistryName().getNamespace()).put(location.getRegistryName().getPath(), new LocationInfo());
                configChanged = true;
            }
        }

        // Register all vanilla useful map markers
        if (!LocationsConfig.LOCATION_INFO.containsKey("map")) {
            LocationsConfig.LOCATION_INFO.put("map", new TreeMap<>(Comparator.comparing(String::toLowerCase)));
        }
        for (int i = 8; i <= 26; i++) {
            if (!LocationsConfig.LOCATION_INFO.get("map").containsKey(MapDecoration.Type.values()[i].name())) {
                LocationsConfig.LOCATION_INFO.get("map").put(MapDecoration.Type.values()[i].name().toLowerCase(), new LocationInfo());
                configChanged = true;
            }
        }

        // If Selene is loaded register all custom map markers
        if (LocationsConfig.SERVER.SELENE_LOADED) {
            DECORATION_TYPES = ObfuscationReflectionHelper.getPrivateValue(MapDecorationHandler.class, null, "DECORATION_TYPES");
            for (CustomDecorationType decorationType : DECORATION_TYPES.values()) {
                if (!LocationsConfig.LOCATION_INFO.containsKey(decorationType.getId().getNamespace())) {
                    LocationsConfig.LOCATION_INFO.put(decorationType.getId().getNamespace(), new TreeMap<>(Comparator.comparing(String::toLowerCase)));
                }
                if (!LocationsConfig.LOCATION_INFO.get(decorationType.getId().getNamespace()).containsKey(decorationType.getId().getPath())) {
                    LocationsConfig.LOCATION_INFO.get(decorationType.getId().getNamespace()).put(decorationType.getId().getPath(), new LocationInfo());
                    configChanged = true;
                }
            }
        }

        if (configChanged) {
            LocationsConfig.CLIENT.serialize();
            LocationsConfig.SERVER.serialize();
            LocationsConfig.clientSpec.save();
            LocationsConfig.serverSpec.save();
        }
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            LocationsCap.register();
            PacketHandler.register();
        });
    }

    @SubscribeEvent
    public static void readConfig(final ModConfig.Loading event) {
        if (event.getConfig().getModId().equals(Locations.MODID)) {
            if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
                LocationsConfig.CLIENT.deserialize();
            }
            if (event.getConfig().getType() == ModConfig.Type.COMMON) {
                LocationsConfig.SERVER.deserialize();
            }
        }
    }
}
