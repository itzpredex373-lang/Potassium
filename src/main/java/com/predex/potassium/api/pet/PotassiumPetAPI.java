package com.predex.potassium.api.pet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Public, versioned pet API for future child mods and community integrations.
 *
 * This API is client-side cosmetic only. Registered pets never become Forge
 * entities and are never synchronized to a Minecraft server.
 */
public final class PotassiumPetAPI {
    public static final int VERSION = 1;

    private static final Map<String, PetDefinition> PETS =
            new LinkedHashMap<String, PetDefinition>();

    private PotassiumPetAPI() {}

    public static synchronized boolean register(PetDefinition definition) {
        if (!isValid(definition)) return false;
        if (definition.getApiVersion() != VERSION) return false;
        if (PETS.containsKey(definition.getId())) return false;
        PETS.put(definition.getId(), definition);
        return true;
    }

    public static synchronized boolean replace(PetDefinition definition) {
        if (!isValid(definition)) return false;
        if (definition.getApiVersion() != VERSION) return false;
        PETS.put(definition.getId(), definition);
        return true;
    }

    public static synchronized boolean unregister(String id) {
        return PETS.remove(id) != null;
    }

    public static synchronized PetDefinition get(String id) {
        return PETS.get(id);
    }

    public static synchronized boolean contains(String id) {
        return PETS.containsKey(id);
    }

    public static synchronized List<PetDefinition> getAll() {
        return Collections.unmodifiableList(
                new ArrayList<PetDefinition>(PETS.values()));
    }

    private static boolean isValid(PetDefinition definition) {
        if (definition == null || definition.getRenderer() == null) return false;
        String id = definition.getId();
        if (id == null || id.length() < 1 || id.length() > 48) return false;
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (!((c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9') || c == '_' || c == '-')) {
                return false;
            }
        }
        return true;
    }
}
