package keyboardcrusader.locations.generators;

import keyboardcrusader.locations.Locations;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.stream.Collectors;

public class NoneNameGenerator extends BaseNameGenerator {
    public NoneNameGenerator(ResourceLocation id) {
        super(id);
    }

    @Override
    public String generateName(ResourceLocation registryName, float temperature) {
        return Arrays.stream(registryName.toString().split(":")[1]
                .replaceAll("_", " ")
                .split("\\s+"))
                .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }
}
