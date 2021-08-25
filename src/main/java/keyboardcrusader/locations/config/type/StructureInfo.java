package keyboardcrusader.locations.config.type;

import com.google.common.collect.Lists;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.LocationsRegistry;
import keyboardcrusader.locations.api.NameGenerator;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.impl.builders.DropdownMenuBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

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
        setNameGenerator(LocationsRegistry.NAME_GENERATORS.getValue(new ResourceLocation(strings[2])));
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<AbstractConfigListEntry<?>> createEntries(ConfigEntryBuilder builder) {
        List<AbstractConfigListEntry<?>> entries = super.createEntries(builder);
        // Square bounds
        entries.add(builder.startBooleanToggle(new TranslationTextComponent("locations.config.square_bounds"), useSquareBounds())
                .setDefaultValue(false)
                .setSaveConsumer(this::setUseSquareBounds)
                .setTooltip(new TranslationTextComponent("locations.config.square_bounds.tooltip"))
                .build());
        // XP modifier
        entries.add(builder.startFloatField(new TranslationTextComponent("locations.config.xp_modifier"), getXPModifier())
                .setDefaultValue(1.0F)
                .setSaveConsumer(this::setXpModifier)
                .setTooltip(new TranslationTextComponent("locations.config.xp_modifier.tooltip"))
                .build());
        // Name generator
        entries.add(builder.startDropdownMenu(new TranslationTextComponent("locations.config.name_generator"), DropdownMenuBuilder.TopCellElementBuilder.of(getNameGenerator().getRegistryName(), ResourceLocation::tryCreate))
                .setDefaultValue(LocationsRegistry.NAME_GENERATORS.getDefaultKey())
                .setSelections(Lists.newArrayList(LocationsRegistry.NAME_GENERATORS.getKeys()))
                .setSuggestionMode(false)
                .setSaveConsumer(resourceLocation -> setNameGenerator(LocationsRegistry.NAME_GENERATORS.getValue(resourceLocation)))
                .setTooltip(new TranslationTextComponent("locations.config.name_generator.tooltip"))
                .build());
        // Has POIs
        entries.add(builder.startBooleanToggle(new TranslationTextComponent("locations.config.hasPOIs"), hasPOIs())
                .setDefaultValue(false)
                .setSaveConsumer(this::setHasPOIs)
                .setTooltip(new TranslationTextComponent("locations.config.hasPOIs.tooltip"))
                .build());
        return entries;
    }
}
