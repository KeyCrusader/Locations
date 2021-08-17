package keyboardcrusader.locations.config;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.MapInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class Common {
    public boolean SELENE_LOADED;
    public boolean ANTIQUE_ATLAS_LOADED;
    public final ForgeConfigSpec.BooleanValue DEATH_LOCATION;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> structure_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> map_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> feature_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> poi_info;

    Common(ForgeConfigSpec.Builder builder) {
        builder.comment("Configuration for Locations").push("Common");

        builder.push("General");

        DEATH_LOCATION = builder
                .comment("Death location")
                .translation("config.locations.death")
                .define("death_location", true);

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
        structure_info.set(LocationsRegistry.STRUCTURES.serializeCommon());
        map_info.set(LocationsRegistry.MAP_MARKERS.serializeCommon());
        feature_info.set(LocationsRegistry.FEATURES.serializeCommon());
        poi_info.set(LocationsRegistry.POIS.serializeCommon());
    }

    public void deserialize() {
        LocationsRegistry.STRUCTURES.deserializeCommon(structure_info.get(), StructureInfo::new);
        LocationsRegistry.MAP_MARKERS.deserializeCommon(map_info.get(), MapInfo::new);
        LocationsRegistry.FEATURES.deserializeCommon(feature_info.get(), FeatureInfo::new);
        LocationsRegistry.POIS.deserializeCommon(poi_info.get(), POIInfo::new);
    }
}
