package keyboardcrusader.locations.config;

import hunternif.mc.impl.atlas.registry.MarkerType;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.INameGenerator;
import keyboardcrusader.locations.api.NameGenerators;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class LocationInfo {
    private boolean disabled;
    private int distance;
    private boolean useSquareBounds;
    private INameGenerator nameGenerator;
    private String icon;
    private float xpModifier;
    private ResourceLocation atlasMarker;

    public LocationInfo() {
        this(false, 300, false, NameGenerators.get(Locations.MODID+":none"), "default", 1.0F, new ResourceLocation(Locations.MODID, "none"));
    }

    public LocationInfo(boolean disabled, int distance, boolean useSquareBounds, INameGenerator nameGenerator, String icon, float xpModifier, ResourceLocation atlasMarker) {
        this.disabled = disabled;
        this.distance = distance;
        this.useSquareBounds = useSquareBounds;
        this.nameGenerator = nameGenerator;
        this.icon = icon;
        this.xpModifier = xpModifier;
        this.atlasMarker = atlasMarker;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public int getDistance() {
        return this.distance;
    }

    public boolean useSquareBounds() {
        return this.useSquareBounds;
    }

    public INameGenerator getNameGenerator() {
        return this.nameGenerator;
    }

    public String getIcon() {
        return this.icon;
    }

    public float getXPModifier() {
        return this.xpModifier;
    }

    public boolean hasAtlasMarker() {
        return !(this.atlasMarker == null);
    }

    public ResourceLocation getAtlasMarker() {
        return this.atlasMarker;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setUseSquareBounds(boolean useSquareBounds) {
        this.useSquareBounds = useSquareBounds;
    }

    public void setNameGenerator(INameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setXpModifier(Float xpModifier) {
        this.xpModifier = xpModifier;
    }

    public void setAtlasMarker(ResourceLocation atlasMarker) {
        this.atlasMarker = atlasMarker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationInfo info  = (LocationInfo) o;

        if (info.isDisabled() != this.isDisabled()) return false;
        if (info.getDistance() != this.getDistance()) return false;
        if (info.useSquareBounds() != this.useSquareBounds()) return false;
        if (info.getNameGenerator() != this.getNameGenerator()) return false;
        if (!info.getIcon().equals(this.getIcon())) return false;
        return true;
    }
}
