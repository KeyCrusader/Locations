package keyboardcrusader.locations.generators;

import keyboardcrusader.locations.api.INameGenerator;
import net.minecraft.util.ResourceLocation;

public abstract class BaseNameGenerator implements INameGenerator {
    private final ResourceLocation id;

    public BaseNameGenerator(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public String getRegistryID() {
        return id.toString();
    }

    @Override
    public abstract String generateName(ResourceLocation registryName, float temperature);
}
