package keyboardcrusader.locations.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * Extend this class and register with RegistryEvent.Register<NameGenerator> to add name generators
 */
public abstract class NameGenerator extends ForgeRegistryEntry<NameGenerator> {
    private final String name;

    protected NameGenerator(String name) {
        this.name = name;
    }

    public abstract String generateName(ResourceLocation registryName, float temperature);
    public String getDisplayName() {
        return name;
    }
}