package com.predex.potassium.pet;

public final class PotassiumPetTypes {
    public static final String[] TYPES = {
        "predex", "king_dragon", "devil", "black_dragon", "wyvern",
        "shadow_dragon", "robot", "fox", "kitsune", "cat",
        "heart_cat", "wolf", "dog", "bunny", "dino",
        "bee", "butterfly", "crow", "capybara", "stag",
        "spirit", "inferno", "ender", "slime", "mini_me",
        "guardian", "ghost", "astronaut", "voidling", "moon_rabbit"
    };

    private PotassiumPetTypes() {}

    public static int indexOf(String type) {
        if (type == null) return 0;
        for (int i = 0; i < TYPES.length; i++) {
            if (TYPES[i].equals(type)) return i;
        }
        return 0;
    }

    public static String get(int index) {
        if (index < 0 || index >= TYPES.length) return TYPES[0];
        return TYPES[index];
    }

    public static String displayName(String type) {
        if ("predex".equals(type)) return "Predex Pet";
        if ("king_dragon".equals(type)) return "Mini King Dragon";
        if ("devil".equals(type)) return "Mini Devil";
        if ("black_dragon".equals(type)) return "Black Dragon";
        if ("wyvern".equals(type)) return "Wyvern";
        if ("shadow_dragon".equals(type)) return "Shadow Dragon";
        if ("mini_me".equals(type)) return "Mini-Me";
        if ("heart_cat".equals(type)) return "Heart Cat";
        if ("moon_rabbit".equals(type)) return "Moon Rabbit";
        if ("voidling".equals(type)) return "Voidling";
        if ("capybara".equals(type)) return "Capybara";
        if ("kitsune".equals(type)) return "Kitsune";
        if ("butterfly".equals(type)) return "Butterfly";
        if ("astronaut".equals(type)) return "Astronaut";
        return type.substring(0, 1).toUpperCase() + type.substring(1).replace('_', ' ');
    }
}
