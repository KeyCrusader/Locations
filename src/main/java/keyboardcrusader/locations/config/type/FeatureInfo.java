package keyboardcrusader.locations.config.type;

import keyboardcrusader.locations.Locations;
import net.minecraft.util.ResourceLocation;

public class FeatureInfo extends LocationInfo {
    public FeatureInfo(boolean disabled, int distance, ResourceLocation atlasMarker) {
        super(disabled, distance, atlasMarker);
    }

    public FeatureInfo() {
        this(true, 300, new ResourceLocation(Locations.MODID, "none"));
    }
}
