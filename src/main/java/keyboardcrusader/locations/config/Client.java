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

public class Client {
    protected final ForgeConfigSpec.ConfigValue<List<? extends String>> location_info;

    Client(ForgeConfigSpec.Builder builder) {
        builder.comment("Configuration for Locations").push("Common");

        builder.push("General");

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
                        LocationsConfig.LOCATION_INFO.get(namespace).get(path).getDistance() + ";" +
                        LocationsConfig.LOCATION_INFO.get(namespace).get(path).getIcon()
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
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setDistance(Integer.parseInt(split[1]));
                LocationsConfig.LOCATION_INFO.get(location[0]).get(location[1]).setIcon(split[2]);
            }
            else {
                LocationsConfig.LOCATION_INFO.get(location[0]).put(location[1], new LocationInfo(false, Integer.parseInt(split[1]), false, NameGenerators.get("locations:none"), split[2], 1.0F, new ResourceLocation(Locations.MODID, "none")));
            }
        }
    }

}
