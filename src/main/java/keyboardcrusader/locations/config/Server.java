package keyboardcrusader.locations.config;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.NameGenerators;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class Server {
    public boolean SELENE_LOADED;
    public boolean ANTIQUE_ATLAS_LOADED;
    public final ForgeConfigSpec.BooleanValue DEATH_LOCATION;
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> location_info;

    Server(ForgeConfigSpec.Builder builder) {
        builder.comment("Configuration for Locations").push("Common");

        builder.push("General");

        DEATH_LOCATION = builder
                .comment("Death location")
                .translation("config.locations.death")
                .define("death_location", true);

        location_info = builder
                .comment("Location settings")
                .translation("config.locations.settings")
                .defineList("location_info", Lists.newArrayList(), it -> it instanceof String);
    }

    public void serialize() {
        List<String> string_list = new ArrayList<>();
        for (String namespace : LocationsConfig.LOCATION_INFO.keySet()) {
            for (String path : LocationsConfig.LOCATION_INFO.get(namespace).keySet()) {
                string_list.add(
                        namespace + ":" + path + ";" +
                                LocationsConfig.LOCATION_INFO.get(namespace).get(path).isDisabled() + ";" +
                                LocationsConfig.LOCATION_INFO.get(namespace).get(path).useSquareBounds() + ";" +
                                LocationsConfig.LOCATION_INFO.get(namespace).get(path).getNameGenerator().getRegistryID() + ";" +
                                LocationsConfig.LOCATION_INFO.get(namespace).get(path).getXPModifier() + ";" +
                                LocationsConfig.LOCATION_INFO.get(namespace).get(path).getAtlasMarker().toString()
                );
            }
        }
        location_info.set(string_list);
    }

    public void deserialize() {
        for (String string : location_info.get()) {
            String[] split = string.split(";");
            String[] location = split[0].split(":");

            if (!LocationsConfig.LOCATION_INFO.containsKey(location[0])) {
                LocationsConfig.LOCATION_INFO.put(location[0], new TreeMap<>(Comparator.comparing(String::toLowerCase)));
            }

            if (LocationsConfig.LOCATION_INFO.get(location[0]).containsKey(location[1])) {
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setDisabled(Boolean.parseBoolean(split[1]));
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setUseSquareBounds(Boolean.parseBoolean(split[2]));
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setNameGenerator(NameGenerators.get(split[3]));
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setXpModifier(Float.parseFloat(split[4]));
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setAtlasMarker(new ResourceLocation(split[5]));
            }
            else {
                LocationsConfig.LOCATION_INFO.get(location[0]).put(location[1], new LocationInfo(Boolean.parseBoolean(split[1]), 300, Boolean.parseBoolean(split[2]), NameGenerators.get(split[3]), "default", Float.parseFloat(split[4]), new ResourceLocation(split[5])));
            }
        }
    }

}
