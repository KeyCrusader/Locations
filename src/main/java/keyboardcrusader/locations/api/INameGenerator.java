package keyboardcrusader.locations.api;

import net.minecraft.util.ResourceLocation;

public interface INameGenerator {
    String getRegistryID();
    String generateName(ResourceLocation registryName, float temperature);
}