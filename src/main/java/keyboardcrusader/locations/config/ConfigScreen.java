package keyboardcrusader.locations.config;

import hunternif.mc.impl.atlas.registry.MarkerType;
import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.LocationsRegistry;
import me.shedaniel.clothconfig2.forge.api.ConfigBuilder;
import me.shedaniel.clothconfig2.forge.api.ConfigCategory;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen {
    public static void registerModsPage() {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (client, parent) -> getClothConfig(parent).build());

    }

    public static ConfigBuilder getClothConfig(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslationTextComponent("locations.config.title"))
                .setSavingRunnable(Config::save)
                .setDefaultBackgroundTexture(new ResourceLocation("minecraft:textures/block/oak_planks.png"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        ConfigCategory general = builder.getOrCreateCategory(new TranslationTextComponent("locations.config.general"));
        general.addEntry(
                entryBuilder.startBooleanToggle(new TranslationTextComponent("locations.config.screen"), Config.CLIENT.SHOW_SCREEN.get())
                        .setDefaultValue(true)
                        .setSaveConsumer(Config.CLIENT.SHOW_SCREEN::set)
                        .setTooltip(new TranslationTextComponent("locations.config.screen"))
                        .build());
        LocationsRegistry.LOCATIONS.createEntries(entryBuilder).forEach(abstractConfigListEntry -> general.addEntry(abstractConfigListEntry.build()));

        ConfigCategory structures = builder.getOrCreateCategory(new TranslationTextComponent("locations.config.structures"));
        LocationsRegistry.STRUCTURES.createEntries(entryBuilder).forEach(abstractConfigListEntries -> structures.addEntry(abstractConfigListEntries.build()));

        ConfigCategory features = builder.getOrCreateCategory(new TranslationTextComponent("locations.config.features"));
        LocationsRegistry.FEATURES.createEntries(entryBuilder).forEach(abstractConfigListEntries -> features.addEntry(abstractConfigListEntries.build()));

        ConfigCategory poi = builder.getOrCreateCategory(new TranslationTextComponent("locations.config.poi"));
        LocationsRegistry.POIS.createEntries(entryBuilder).forEach(abstractConfigListEntries -> poi.addEntry(abstractConfigListEntries.build()));

        builder.transparentBackground();

        return builder;
    }
}
