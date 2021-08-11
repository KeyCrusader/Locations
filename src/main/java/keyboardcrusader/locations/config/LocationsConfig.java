package keyboardcrusader.locations.config;

import com.google.common.collect.Lists;
import hunternif.mc.impl.atlas.registry.MarkerType;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.api.INameGenerator;
import keyboardcrusader.locations.api.NameGenerators;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.clothconfig2.forge.api.ConfigBuilder;
import me.shedaniel.clothconfig2.forge.api.ConfigCategory;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.forge.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.forge.impl.builders.SubCategoryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class LocationsConfig implements ConfigData {
    public static Map<String, Map<String, LocationInfo>> LOCATION_INFO = new TreeMap<>(Comparator.comparing(String::toLowerCase));

    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    public static final ForgeConfigSpec clientSpec = CLIENT_BUILDER.build();

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static final Server SERVER = new Server(SERVER_BUILDER);
    public static final ForgeConfigSpec serverSpec = SERVER_BUILDER.build();

    public static ConfigBuilder getClothConfig() {
        ConfigBuilder builder = ConfigBuilder.create().setSavingRunnable(() -> {
            CLIENT.serialize();
            SERVER.serialize();
        });
        builder.setTitle(new TranslationTextComponent("locations.config.title"));
        builder.setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(new TranslationTextComponent("locations.config.general"));

        general.addEntry(entryBuilder.startBooleanToggle(new TranslationTextComponent("locations.config.death_location"), SERVER.DEATH_LOCATION.get()).setDefaultValue(true).setSaveConsumer(SERVER.DEATH_LOCATION::set).setTooltip(new TranslationTextComponent("locations.config.death_location.tooltip")).build());

        List<ResourceLocation> atlasMarkers = new ArrayList<>();
        atlasMarkers.add(new ResourceLocation(Locations.MODID, "none"));
        if (LocationsConfig.SERVER.ANTIQUE_ATLAS_LOADED) {
            atlasMarkers.addAll(MarkerType.REGISTRY.keySet());
        }

        for (String namespace :LOCATION_INFO.keySet()) {
            SubCategoryBuilder subcategory = entryBuilder.startSubCategory(new TranslationTextComponent(namespace)).setExpanded(false);
            for (String path : LOCATION_INFO.get(namespace).keySet()) {
                LocationInfo current = LOCATION_INFO.get(namespace).get(path);

                subcategory.add(new MultiElementListEntry<>(
                        new TranslationTextComponent(path),
                        current,
                        Lists.newArrayList(
                                entryBuilder.startBooleanToggle(new TranslationTextComponent("locations.config.disabled"), current.isDisabled()).setDefaultValue(false).setSaveConsumer(current::setDisabled).setTooltip(new TranslationTextComponent("locations.config.disabled.tooltip")).build(),
                                entryBuilder.startIntSlider(new TranslationTextComponent("locations.config.render_distance"), current.getDistance(), 0, 1000).setDefaultValue(300).setSaveConsumer(current::setDistance).setTooltip(new TranslationTextComponent("locations.config.render_distance.tooltip")).build(),
                                entryBuilder.startBooleanToggle(new TranslationTextComponent("locations.config.square_bounds"), current.useSquareBounds()).setDefaultValue(false).setSaveConsumer(current::setUseSquareBounds).setTooltip(new TranslationTextComponent("locations.config.square_bounds.tooltip")).build(),
                                entryBuilder.startDropdownMenu(new TranslationTextComponent("locations.config.name_generator"), DropdownMenuBuilder.TopCellElementBuilder.of(current.getNameGenerator().getRegistryID(), NameGenerators::get)).setDefaultValue(NameGenerators.get("locations:none")).setSelections(Lists.newArrayList(NameGenerators.getNameGenerators().keySet())).setSuggestionMode(false).setSaveConsumer(s -> current.setNameGenerator((INameGenerator) s)).setTooltip(new TranslationTextComponent("locations.config.name_generator.tooltip")).build(),
                                entryBuilder.startStrField(new TranslationTextComponent("locations.config.icon"), current.getIcon()).setDefaultValue("default").setSaveConsumer(current::setIcon).setTooltip(new TranslationTextComponent("locations.config.icon.tooltip")).build(),
                                entryBuilder.startFloatField(new TranslationTextComponent("locations.config.xp_modifier"), current.getXPModifier()).setDefaultValue(1.0F).setSaveConsumer(current::setXpModifier).setTooltip(new TranslationTextComponent("locations.config.xp_modifier.tooltip")).build(),
                                entryBuilder.startDropdownMenu(new TranslationTextComponent("locations.config.atlas_marker"), DropdownMenuBuilder.TopCellElementBuilder.of(current.getAtlasMarker(), ResourceLocation::new)).setDefaultValue(new ResourceLocation(Locations.MODID, "none")).setSelections(atlasMarkers).setSuggestionMode(false).setSaveConsumer(s -> current.setAtlasMarker(s)).setTooltip(new TranslationTextComponent("locations.config.atlas_marker.tooltip")).build()),
                        false)
                );
            }
            general.addEntry(subcategory.build());
        }

        builder.transparentBackground();

        return builder;
    }
}
