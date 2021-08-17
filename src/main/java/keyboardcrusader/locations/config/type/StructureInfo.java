package keyboardcrusader.locations.config.type;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.api.NameGenerator;
import net.minecraft.util.ResourceLocation;

public class StructureInfo extends LocationInfo {
    private boolean useSquareBounds;
    private NameGenerator nameGenerator;
    private float xpModifier;
    private boolean hasPOIs;

    public StructureInfo() {
        this(false, 300, new ResourceLocation(Locations.MODID, "none"), false, LocationsRegistry.NAME_GENERATORS.getValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey()), 1.0F, false);
    }

    public StructureInfo(boolean disabled, int distance, ResourceLocation atlasMarker, boolean useSquareBounds, NameGenerator nameGenerator, float xpModifier, boolean hasPOIs) {
        super(disabled, distance, atlasMarker);
        this.useSquareBounds = useSquareBounds;
        this.nameGenerator = nameGenerator;
        this.xpModifier = xpModifier;
        this.hasPOIs = hasPOIs;
    }

    public boolean useSquareBounds() {
        return this.useSquareBounds;
    }

    public NameGenerator getNameGenerator() {
        return this.nameGenerator;
    }

    public float getXPModifier() {
        return this.xpModifier;
    }

    public boolean hasPOIs() {
        return this.hasPOIs;
    }

    public void setUseSquareBounds(boolean useSquareBounds) {
        this.useSquareBounds = useSquareBounds;
    }

    public void setNameGenerator(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    public void setXpModifier(Float xpModifier) {
        this.xpModifier = xpModifier;
    }

    public void setHasPOIs(boolean hasPOIs) {
        this.hasPOIs = hasPOIs;
    }

    @Override
    public String serializeCommon() {
        return super.serializeCommon() + ";" + useSquareBounds() + ";" + getNameGenerator().getRegistryName() + ";" + getXPModifier() + ";" + hasPOIs();
    }

    @Override
    public void deserializeCommon(String string) {
        super.deserializeCommon(string);
        String[] strings = string.split(";");
        setUseSquareBounds(Boolean.parseBoolean(strings[1]));
        setNameGenerator(LocationsRegistry.NAME_GENERATORS.getValue(ResourceLocation.tryCreate(strings[2])));
        setXpModifier(Float.parseFloat(strings[3]));
        setHasPOIs(Boolean.parseBoolean(strings[4]));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StructureInfo info  = (StructureInfo) o;

        if (info.useSquareBounds() != this.useSquareBounds()) return false;
        if (info.getNameGenerator() != this.getNameGenerator()) return false;
        return super.equals(info);
    }

}
