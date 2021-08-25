package keyboardcrusader.locations.config.type;

import keyboardcrusader.locations.Locations;
import me.shedaniel.clothconfig2.forge.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class POIInfo extends LocationInfo {
    private boolean multiBlock;
    private boolean permanent;

    public POIInfo(boolean disabled, int distance, ResourceLocation atlasMarker, boolean multiBlock, boolean permanent) {
        super(disabled, distance, atlasMarker);
        this.multiBlock = multiBlock;
        this.permanent = permanent;
    }

    public POIInfo() {
        this(false, 300, new ResourceLocation(Locations.MODID, "none"), false, false);
    }

    public boolean isMultiBlock() {
        return multiBlock;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setMultiBlock(boolean multiBlock) {
        this.multiBlock = multiBlock;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }


    @Override
    public String serializeCommon() {
        return super.serializeCommon() + ";" + isMultiBlock() + ";" + isPermanent();
    }

    @Override
    public void deserializeCommon(String string) {
        String[] strings = string.split(";");
        super.deserializeCommon(string);
        setMultiBlock(Boolean.parseBoolean(strings[1]));
        setPermanent(Boolean.parseBoolean(strings[2]));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public List<AbstractConfigListEntry<?>> createEntries(ConfigEntryBuilder builder) {
        List<AbstractConfigListEntry<?>> entries = super.createEntries(builder);
        // Multi block
        entries.add(builder.startBooleanToggle(new TranslationTextComponent("locations.config.multiBlock"), isMultiBlock())
                .setDefaultValue(false)
                .setSaveConsumer(this::setMultiBlock)
                .setTooltip(new TranslationTextComponent("locations.config.multiBlock.tooltip"))
                .build());
        // In a location
        entries.add(builder.startBooleanToggle(new TranslationTextComponent("locations.config.permanent"), isPermanent())
                .setDefaultValue(false)
                .setSaveConsumer(this::setPermanent)
                .setTooltip(new TranslationTextComponent("locations.config.permanent.tooltip"))
                .build());
        return entries;
    }
}
