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
                .defineList("location_info", Lists.newArrayList("locations:death=false", "locations:spawn=false", "locations:default=false"), it -> it instanceof String);

        structure_info = builder
                .comment("Structure settings")
                .translation("config.locations.settings")
                .defineList("structure_info", Lists.newArrayList("minecraft:mansion=false;false;locations:none;1.0;false", "minecraft:ocean_ruin=false;false;locations:none;1.0;false", "minecraft:desert_pyramid=false;false;locations:none;1.0;false", "minecraft:mineshaft=false;false;locations:mineshaft;1.0;false", "minecraft:monument=false;false;locations:none;1.0;false", "minecraft:nether_fossil=true;false;locations:none;1.0;false", "minecraft:igloo=false;false;locations:none;1.0;false", "minecraft:bastion_remnant=false;false;locations:none;1.0;false", "minecraft:fortress=false;false;locations:none;1.0;false", "minecraft:pillager_outpost=false;false;locations:outpost;1.0;false", "minecraft:shipwreck=false;false;locations:none;1.0;false", "minecraft:endcity=false;false;locations:none;1.0;false", "minecraft:jungle_pyramid=false;false;locations:none;1.0;false", "minecraft:swamp_hut=false;false;locations:none;1.0;false", "minecraft:stronghold=false;false;locations:none;1.0;false", "locations:default=false;false;locations:none;1.0;false", "minecraft:ruined_portal=false;false;locations:none;1.0;false", "minecraft:village=false;true;locations:village;1.0;true", "minecraft:buried_treasure=true;false;locations:none;1.0;false"), it -> it instanceof String);

        feature_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("feature_info", Lists.newArrayList("minecraft:no_op=true", "minecraft:lake=true", "minecraft:no_surface_ore=true", "waystones:waystone=false", "minecraft:ore=true", "minecraft:chorus_plant=true", "minecraft:fill_layer=true", "waystones:sandy_waystone=false", "minecraft:desert_well=false", "minecraft:glowstone_blob=true", "minecraft:fossil=true", "minecraft:seagrass=true", "minecraft:disk=true", "minecraft:blue_ice=true", "minecraft:tree=true", "minecraft:sea_pickle=true", "minecraft:simple_block=true", "minecraft:delta_feature=true", "minecraft:huge_brown_mushroom=true", "minecraft:end_island=true", "minecraft:decorated=true", "minecraft:huge_fungus=true", "minecraft:freeze_top_layer=true", "minecraft:random_boolean_selector=true", "minecraft:end_spike=true", "minecraft:ice_spike=true", "minecraft:coral_claw=true", "minecraft:twisting_vines=true", "minecraft:random_patch=true", "minecraft:ice_patch=true", "minecraft:netherrack_replace_blobs=true", "minecraft:no_bonemeal_flower=true", "minecraft:flower=true", "minecraft:end_gateway=false", "minecraft:basalt_pillar=true", "minecraft:simple_random_selector=true", "minecraft:spring_feature=true", "minecraft:monster_room=false", "minecraft:kelp=true", "minecraft:random_selector=true", "minecraft:coral_mushroom=true", "locations:default=true", "minecraft:iceberg=false", "minecraft:basalt_columns=true", "minecraft:huge_red_mushroom=true", "minecraft:forest_rock=true", "waystones:mossy_waystone=false", "minecraft:bonus_chest=true", "minecraft:block_pile=true", "minecraft:weeping_vines=true", "minecraft:void_start_platform=true", "minecraft:bamboo=true", "minecraft:emerald_ore=true", "minecraft:nether_forest_vegetation=true", "minecraft:coral_tree=true", "minecraft:vines=true"), it -> it instanceof String);

        poi_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("poi_info", Lists.newArrayList("minecraft:cleric=false;false;false", "minecraft:toolsmith=false;false;false", "minecraft:cartographer=false;false;false", "minecraft:weaponsmith=false;false;false", "minecraft:beehive=true;false;false", "minecraft:librarian=false;false;false", "minecraft:bee_nest=true;false;false", "minecraft:butcher=false;false;false", "minecraft:fletcher=false;false;false", "minecraft:mason=false;false;false", "minecraft:home=true;false;false", "minecraft:fisherman=false;false;false", "minecraft:lodestone=false;false;true", "minecraft:leatherworker=false;false;false", "minecraft:unemployed=true;false;false", "minecraft:armorer=false;false;false", "minecraft:shepherd=false;false;false", "minecraft:nether_portal=false;true;true", "minecraft:nitwit=true;false;false", "locations:default=false;false;false", "minecraft:meeting=false;false;false", "minecraft:farmer=false;false;false"), it -> it instanceof String);
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
