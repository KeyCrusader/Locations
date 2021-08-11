package keyboardcrusader.locations.api;

import java.util.HashMap;
import java.util.Map;

public class NameGenerators {
    private static final Map<String, INameGenerator> NAME_GENERATORS = new HashMap<>();

    public static void register(INameGenerator nameGenerator) {
        if (NAME_GENERATORS.containsKey(nameGenerator.getRegistryID())) {
            throw new IllegalArgumentException(nameGenerator.getRegistryID() + " name generator already exists");
        }
        else {
            NAME_GENERATORS.put(nameGenerator.getRegistryID(), nameGenerator);
        }
    }

    public static Map<String, INameGenerator> getNameGenerators() {
        return NAME_GENERATORS;
    }

    public static INameGenerator get(String registryID) {
        return NAME_GENERATORS.get(registryID);
    }
}
