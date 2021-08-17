package keyboardcrusader.locations.config;

import keyboardcrusader.locations.config.type.LocationInfo;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigRegistry<V extends LocationInfo> {
    private final Map<ResourceLocation, V> names = new HashMap<>();
    private final ResourceLocation defaultKey;

    public ConfigRegistry(V defaultValue) {
        registerOrUpdate(defaultValue);
        this.defaultKey = defaultValue.getRegistryName();
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

            V v = (V) supplier.get().setRegistryName(info[0]);
            v.deserializeClient(info[1]);
            registerOrUpdate(v);
        }
    }
    public void deserializeCommon(List<? extends String> strings, Supplier<? extends LocationInfo> supplier) {
        for (String location_info : strings) {
            String[] info = location_info.split("=");

            V v = (V) supplier.get().setRegistryName(info[0]);
            v.deserializeCommon(info[1]);
            registerOrUpdate(v);
        }
    }
}
