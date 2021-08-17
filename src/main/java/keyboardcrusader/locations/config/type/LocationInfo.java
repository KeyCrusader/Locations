package keyboardcrusader.locations.config.type;

import keyboardcrusader.locations.Locations;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class LocationInfo extends ForgeRegistryEntry<LocationInfo> {
    private boolean disabled;
    private int distance;
    private ResourceLocation atlasMarker;

    public LocationInfo(boolean disabled, int distance, ResourceLocation atlasMarker) {
        this.disabled = disabled;
        this.distance = distance;
        this.atlasMarker = atlasMarker;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getDistance() {
        return distance;
    }

    public boolean hasAtlasMarker() {
        return !(atlasMarker.equals(new ResourceLocation(Locations.MODID, "none")));
    }

    public ResourceLocation getAtlasMarker() {
        return atlasMarker;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setAtlasMarker(ResourceLocation atlasMarker) {
        this.atlasMarker = atlasMarker;
    }

    public String serializeClient() {
        return getRegistryName().toString() + "=" + getDistance() + ";" + getAtlasMarker().toString();
    }

    public void deserializeClient(String string) {
        String[] strings = string.split(";");
        setDistance(Integer.parseInt(strings[0]));
        setAtlasMarker(ResourceLocation.tryCreate(strings[1]));
    }

    public String serializeCommon() {
        return getRegistryName().toString() + "=" + isDisabled();
    }

    public void deserializeCommon(String string) {
        String[] strings = string.split(";");
        setDisabled(Boolean.parseBoolean(strings[0]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapInfo info  = (MapInfo) o;

        if (info.isDisabled() != isDisabled()) return false;
        if (info.getDistance() != getDistance()) return false;
        if (info.getAtlasMarker() != getAtlasMarker()) return false;
        return true;
    }
}
