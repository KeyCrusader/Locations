package keyboardcrusader.locations;

import keyboardcrusader.locations.api.NameGenerator;
import keyboardcrusader.locations.config.*;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.MapInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraftforge.registries.IForgeRegistry;

public class LocationsRegistry {
    public static IForgeRegistry<NameGenerator> NAME_GENERATORS = null;
    public static ConfigRegistry<StructureInfo> STRUCTURES = null;
    public static ConfigRegistry<MapInfo> MAP_MARKERS = null;
    public static ConfigRegistry<FeatureInfo> FEATURES = null;
    public static ConfigRegistry<POIInfo> POIS = null;
}
