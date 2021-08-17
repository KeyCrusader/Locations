package keyboardcrusader.locations.config.type;

import keyboardcrusader.locations.Locations;
import net.minecraft.util.ResourceLocation;

public class MapInfo extends LocationInfo {
    public MapInfo(boolean disabled, int distance, ResourceLocation atlasMarker) {
        super(disabled, distance, atlasMarker);
    }

    public MapInfo() {
        this(false, 300, new ResourceLocation(Locations.MODID, "none"));
    }
}
