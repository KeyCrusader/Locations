package keyboardcrusader.locations.config;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.config.type.FeatureInfo;
import keyboardcrusader.locations.config.type.LocationInfo;
import keyboardcrusader.locations.config.type.POIInfo;
import keyboardcrusader.locations.config.type.StructureInfo;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Client {
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> location_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> structure_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> feature_info;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> poi_info;
    public final ForgeConfigSpec.ConfigValue<Boolean> SHOW_SCREEN;

    Client(ForgeConfigSpec.Builder builder) {
        builder.comment("Configuration for Locations").push("Client");

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

        SHOW_SCREEN = builder
                .comment("Show screen")
                .translation("config.locations.settings")
                .define("show_screen", true);
    }

    public void serialize() {
        location_info.set(LocationsRegistry.LOCATIONS.serializeClient());
        structure_info.set(LocationsRegistry.STRUCTURES.serializeClient());
        feature_info.set(LocationsRegistry.FEATURES.serializeClient());
        poi_info.set(LocationsRegistry.POIS.serializeClient());
    }

    public void deserialize() {
        LocationsRegistry.LOCATIONS.deserializeClient(location_info.get(), LocationInfo::new);
        LocationsRegistry.STRUCTURES.deserializeClient(structure_info.get(), StructureInfo::new);
        LocationsRegistry.FEATURES.deserializeClient(feature_info.get(), FeatureInfo::new);
        LocationsRegistry.POIS.deserializeClient(poi_info.get(), POIInfo::new);
    }
}
