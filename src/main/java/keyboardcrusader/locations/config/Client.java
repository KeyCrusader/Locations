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
                .defineList("location_info", Lists.newArrayList("locations:death=300;locations:none", "locations:spawn=300;locations:none", "locations:default=300;locations:none"), it -> it instanceof String);

        structure_info = builder
                .comment("Structure settings")
                .translation("config.locations.settings")
                .defineList("structure_info", Lists.newArrayList("minecraft:mansion=300;locations:none", "minecraft:ocean_ruin=300;locations:none", "minecraft:desert_pyramid=300;locations:none", "minecraft:mineshaft=300;locations:none", "minecraft:monument=300;locations:none", "minecraft:nether_fossil=300;locations:none", "minecraft:igloo=300;locations:none", "minecraft:bastion_remnant=300;locations:none", "minecraft:fortress=300;locations:none", "minecraft:pillager_outpost=300;locations:none", "minecraft:shipwreck=300;locations:none", "minecraft:endcity=300;locations:none", "minecraft:jungle_pyramid=300;locations:none", "minecraft:swamp_hut=300;locations:none", "minecraft:stronghold=300;locations:none", "locations:default=300;locations:none", "minecraft:ruined_portal=300;locations:none", "minecraft:village=300;locations:none", "minecraft:buried_treasure=300;locations:none"), it -> it instanceof String);

        feature_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("feature_info", Lists.newArrayList("minecraft:no_op=300;locations:none", "minecraft:lake=300;locations:none", "minecraft:no_surface_ore=300;locations:none", "waystones:waystone=300;locations:none", "minecraft:ore=300;locations:none", "minecraft:chorus_plant=300;locations:none", "minecraft:fill_layer=300;locations:none", "waystones:sandy_waystone=300;locations:none", "minecraft:desert_well=300;locations:none", "minecraft:glowstone_blob=300;locations:none", "minecraft:fossil=300;locations:none", "minecraft:seagrass=300;locations:none", "minecraft:disk=300;locations:none", "minecraft:blue_ice=300;locations:none", "minecraft:tree=300;locations:none", "minecraft:sea_pickle=300;locations:none", "minecraft:simple_block=300;locations:none", "minecraft:delta_feature=300;locations:none", "minecraft:huge_brown_mushroom=300;locations:none", "minecraft:end_island=300;locations:none", "minecraft:decorated=300;locations:none", "minecraft:huge_fungus=300;locations:none", "minecraft:freeze_top_layer=300;locations:none", "minecraft:random_boolean_selector=300;locations:none", "minecraft:end_spike=300;locations:none", "minecraft:ice_spike=300;locations:none", "minecraft:coral_claw=300;locations:none", "minecraft:twisting_vines=300;locations:none", "minecraft:random_patch=300;locations:none", "minecraft:ice_patch=300;locations:none", "minecraft:netherrack_replace_blobs=300;locations:none", "minecraft:no_bonemeal_flower=300;locations:none", "minecraft:flower=300;locations:none", "minecraft:end_gateway=300;locations:none", "minecraft:basalt_pillar=300;locations:none", "minecraft:simple_random_selector=300;locations:none", "minecraft:spring_feature=300;locations:none", "minecraft:monster_room=300;locations:none", "minecraft:kelp=300;locations:none", "minecraft:random_selector=300;locations:none", "minecraft:coral_mushroom=300;locations:none", "locations:default=300;locations:none", "minecraft:iceberg=300;locations:none", "minecraft:basalt_columns=300;locations:none", "minecraft:huge_red_mushroom=300;locations:none", "minecraft:forest_rock=300;locations:none", "waystones:mossy_waystone=300;locations:none", "minecraft:bonus_chest=300;locations:none", "minecraft:block_pile=300;locations:none", "minecraft:weeping_vines=300;locations:none", "minecraft:void_start_platform=300;locations:none", "minecraft:bamboo=300;locations:none", "minecraft:emerald_ore=300;locations:none", "minecraft:nether_forest_vegetation=300;locations:none", "minecraft:coral_tree=300;locations:none", "minecraft:vines=300;locations:none"), it -> it instanceof String);

        poi_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("poi_info", Lists.newArrayList("minecraft:cleric=300;locations:none", "minecraft:toolsmith=300;locations:none", "minecraft:cartographer=300;locations:none", "minecraft:weaponsmith=300;locations:none", "minecraft:beehive=300;locations:none", "minecraft:librarian=300;locations:none", "minecraft:bee_nest=300;locations:none", "minecraft:butcher=300;locations:none", "minecraft:fletcher=300;locations:none", "minecraft:mason=300;locations:none", "minecraft:home=300;locations:none", "minecraft:fisherman=300;locations:none", "minecraft:lodestone=300;locations:none", "minecraft:leatherworker=300;locations:none", "minecraft:unemployed=300;locations:none", "minecraft:armorer=300;locations:none", "minecraft:shepherd=300;locations:none", "minecraft:nether_portal=300;locations:none", "minecraft:nitwit=300;locations:none", "locations:default=300;locations:none", "minecraft:meeting=300;locations:none", "minecraft:farmer=300;locations:none"), it -> it instanceof String);

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
