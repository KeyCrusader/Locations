package keyboardcrusader.locations.config.type;

import hunternif.mc.impl.atlas.registry.MarkerType;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.config.Config;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.impl.builders.DropdownMenuBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class LocationInfo extends ForgeRegistryEntry<LocationInfo> {
    private boolean disabled;
    private int distance;
    private ResourceLocation atlasMarker;

    public LocationInfo(boolean disabled, int distance, ResourceLocation atlasMarker) {
        this.disabled = disabled;
        this.distance = distance;
        this.atlasMarker = atlasMarker;
    }

    public LocationInfo() {
        this(false, 300, new ResourceLocation(Locations.MODID, "none"));
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
        setAtlasMarker(new ResourceLocation(strings[1]));
    }

    public String serializeCommon() {
        return getRegistryName().toString() + "=" + isDisabled();
    }

    public void deserializeCommon(String string) {
        String[] strings = string.split(";");
        setDisabled(Boolean.parseBoolean(strings[0]));
    }

    @OnlyIn(Dist.CLIENT)
    public List<AbstractConfigListEntry<?>> createEntries(ConfigEntryBuilder builder) {
        List<AbstractConfigListEntry<?>> entries = new ArrayList<>();
        // Enabled
        entries.add(builder.startBooleanToggle(new TranslationTextComponent("locations.config.disabled"), isDisabled())
                .setDefaultValue(false)
                .setSaveConsumer(this::setDisabled)
                .setTooltip(new TranslationTextComponent("locations.config.disabled.tooltip"))
                .build());
        // Render distance
        entries.add(builder.startIntSlider(new TranslationTextComponent("locations.config.render_distance"), getDistance(), 0, 1000)
                .setDefaultValue(300)
                .setSaveConsumer(this::setDistance)
                .setTooltip(new TranslationTextComponent("locations.config.render_distance.tooltip"))
                .build());
        // Antique Atlas
        if (Config.COMMON.ANTIQUE_ATLAS_LOADED) {
            entries.add(builder.startDropdownMenu(new TranslationTextComponent("locations.config.atlas_marker"), DropdownMenuBuilder.TopCellElementBuilder.of(getAtlasMarker(), ResourceLocation::tryCreate))
                    .setDefaultValue(new ResourceLocation(Locations.MODID, "none"))
                    .setSelections(MarkerType.REGISTRY.keySet())
                    .setSuggestionMode(false)
                    .setSaveConsumer(this::setAtlasMarker)
                    .setTooltip(new TranslationTextComponent("locations.config.atlas_marker.tooltip"))
                    .build());
        }
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationInfo)) return false;

        LocationInfo info  = (LocationInfo) o;

        if (info.isDisabled() != isDisabled()) return false;
        if (info.getDistance() != getDistance()) return false;
        if (info.getAtlasMarker() != getAtlasMarker()) return false;
        return true;
    }
}
