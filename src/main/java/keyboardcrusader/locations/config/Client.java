package keyboardcrusader.locations.config;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.MapInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class Client {
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> structure_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> map_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> feature_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> poi_info;

    Client(ForgeConfigSpec.Builder builder) {
        builder.comment("Configuration for Locations").push("Client");

        builder.push("General");

        structure_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("location_info", Lists.newArrayList(), it -> it instanceof String);

        map_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("map_info", Lists.newArrayList(), it -> it instanceof String);

        feature_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("feature_info", Lists.newArrayList(), it -> it instanceof String);

        poi_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("poi_info", Lists.newArrayList(), it -> it instanceof String);
    }

    public void serialize() {
        structure_info.set(LocationsRegistry.STRUCTURES.serializeClient());
        map_info.set(LocationsRegistry.MAP_MARKERS.serializeClient());
        feature_info.set(LocationsRegistry.FEATURES.serializeClient());
        poi_info.set(LocationsRegistry.POIS.serializeClient());
    }

    public void deserialize() {
        LocationsRegistry.STRUCTURES.deserializeClient(structure_info.get(), StructureInfo::new);
        LocationsRegistry.MAP_MARKERS.deserializeClient(map_info.get(), MapInfo::new);
        LocationsRegistry.FEATURES.deserializeClient(feature_info.get(), FeatureInfo::new);
        LocationsRegistry.POIS.deserializeClient(poi_info.get(), POIInfo::new);
    }
}
