package keyboardcrusader.locations.config;

import keyboardcrusader.locations.Locations;
import keyboardcrusader.locations.config.type.LocationInfo;
import me.shedaniel.clothconfig2.forge.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.forge.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.forge.impl.builders.SubCategoryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import java.util.*;
import java.util.function.Supplier;

public class ConfigRegistry<V extends LocationInfo> {
    private final Map<ResourceLocation, V> names = new HashMap<>();
    private ResourceLocation defaultKey;

    public void setDefault(V defaultValue) {
        defaultKey = defaultValue.getRegistryName();
        registerOrUpdate(defaultValue);
    }

    public void registerIfEmpty(V value) {
        ResourceLocation key = value.getRegistryName();
        if (!exists(key)) {
            names.put(key, value);
        }
    }

    public void registerOrUpdate(V value) {
        ResourceLocation key = value.getRegistryName();
        names.put(key, value);
    }

    public V get(ResourceLocation key) {
        if (exists(key)) {
            return names.get(key);
        }
        return names.get(defaultKey);
    }

    public Collection<V> getValues()
    {
        return Collections.unmodifiableCollection(this.names.values());
    }

    public boolean exists(ResourceLocation key) {
        return names.containsKey(key);
    }

    public List<String> serializeClient() {
        List<String> retVal = new ArrayList<>();
        names.forEach((resourceLocation, v) -> retVal.add(v.serializeClient()));
        return retVal;
    }
    public List<String> serializeCommon() {
        List<String> retVal = new ArrayList<>();
        names.forEach((resourceLocation, v) -> retVal.add(v.serializeCommon()));
        return retVal;
    }

    public void deserializeClient(List<? extends String> strings, Supplier<V> supplier) {
        for (String location_info : strings) {
            String[] info = location_info.split("=");

            V v;
            if (names.containsKey(new ResourceLocation(info[0]))) {
                v = names.get(new ResourceLocation(info[0]));
            }
            else {
                v = (V) supplier.get().setRegistryName(info[0]);
            }
            v.deserializeClient(info[1]);
            registerOrUpdate(v);
        }
    }
    public void deserializeCommon(List<? extends String> strings, Supplier<? extends LocationInfo> supplier) {
        for (String location_info : strings) {
            String[] info = location_info.split("=");

            V v;
            if (names.containsKey(new ResourceLocation(info[0]))) {
                v = names.get(new ResourceLocation(info[0]));
            }
            else {
                v = (V) supplier.get().setRegistryName(info[0]);
            }
            v.deserializeCommon(info[1]);
            registerOrUpdate(v);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public Collection<SubCategoryBuilder> createEntries(ConfigEntryBuilder entryBuilder) {
        Map<String, SubCategoryBuilder> subCategories = new TreeMap<>(Comparator.comparing(String::toLowerCase));

        for (V info : getValues()) {
            String namespace = info.getRegistryName().getNamespace();
            String path = info.getRegistryName().getPath();
            if (!subCategories.containsKey(namespace)) {
                subCategories.put(namespace, entryBuilder.startSubCategory(new TranslationTextComponent(modNameFromID(namespace))).setExpanded(false));
            }
            subCategories.get(namespace).add(new MultiElementListEntry<>(
                    new TranslationTextComponent(path),
                    info,
                    info.createEntries(entryBuilder),
                    false)
            );
        }

        return subCategories.values();
    }

    @OnlyIn(Dist.CLIENT)
    private static String modNameFromID(String id) {
        for (ModInfo mod : ModList.get().getMods()) {
            if (mod.getModId().equals(id)) {
                return mod.getDisplayName();
            }
        }
        return id;
    }
}
