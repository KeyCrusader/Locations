package keyboardcrusader.locations.config;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.LocationInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Common {
    public boolean WAYSTONES_LOADED;
    public boolean ANTIQUE_ATLAS_LOADED;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> location_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> structure_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> feature_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> poi_info;

    Common(ForgeConfigSpec.Builder builder) {
        builder.comment("Configuration for Locations").push("Common");

        builder.push("General");

        location_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("location_info", Lists.newArrayList(), it -> it instanceof String);

        structure_info = builder
                .comment("Structure settings")
                .translation("config.locations.settings")
                .defineList("structure_info", Lists.newArrayList(), it -> it instanceof String);

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
        location_info.set(LocationsRegistry.LOCATIONS.serializeCommon());
        structure_info.set(LocationsRegistry.STRUCTURES.serializeCommon());
        feature_info.set(LocationsRegistry.FEATURES.serializeCommon());
        poi_info.set(LocationsRegistry.POIS.serializeCommon());
    }

    public void deserialize() {
        LocationsRegistry.LOCATIONS.deserializeCommon(location_info.get(), LocationInfo::new);
        LocationsRegistry.STRUCTURES.deserializeCommon(structure_info.get(), StructureInfo::new);
        LocationsRegistry.FEATURES.deserializeCommon(feature_info.get(), FeatureInfo::new);
        LocationsRegistry.POIS.deserializeCommon(poi_info.get(), POIInfo::new);
    }
}
