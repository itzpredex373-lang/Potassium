package com.predex.potassium.client;

public final class PotassiumPetTypes {
    public static final String[] TYPES = {
        "predex", "king_dragon", "devil", "black_dragon", "wyvern",
        "shadow_dragon", "robot", "fox", "kitsune", "cat",
        "heart_cat", "wolf", "dog", "bunny", "dino",
        "bee", "butterfly", "crow", "capybara", "stag",
        "spirit", "inferno", "ender", "slime", "mini_me",
        "guardian", "ghost", "astronaut", "voidling", "moon_rabbit",
        "mini_phoenix", "dragon_egg", "shadow_fox", "cyber_cat", "mini_golem",
        "ghost_bunny", "tiny_knight", "slime_king", "void_orb", "mini_astral_wolf",
        "pixel_robot", "mini_ender_dragon", "crystal_fairy", "tiny_reaper", "moon_cat"
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
        if ("shadow_dragon".equals(type)) return "Shadow Dragon";
        if ("heart_cat".equals(type)) return "Heart Cat";
        if ("mini_me".equals(type)) return "Mini-Me";
        if ("voidling".equals(type)) return "Voidling";
        if ("moon_rabbit".equals(type)) return "Moon Rabbit";
        if ("mini_phoenix".equals(type)) return "Mini Phoenix";
        if ("dragon_egg".equals(type)) return "Dragon Egg";
        if ("shadow_fox".equals(type)) return "Shadow Fox";
        if ("cyber_cat".equals(type)) return "Cyber Cat";
        if ("mini_golem".equals(type)) return "Mini Golem";
        if ("ghost_bunny".equals(type)) return "Ghost Bunny";
        if ("tiny_knight".equals(type)) return "Tiny Knight";
        if ("slime_king".equals(type)) return "Slime King";
        if ("void_orb".equals(type)) return "Void Orb";
        if ("mini_astral_wolf".equals(type)) return "Mini Astral Wolf";
        if ("pixel_robot".equals(type)) return "Pixel Robot";
        if ("mini_ender_dragon".equals(type)) return "Mini Ender Dragon";
        if ("crystal_fairy".equals(type)) return "Crystal Fairy";
        if ("tiny_reaper".equals(type)) return "Tiny Reaper";
        if ("moon_cat".equals(type)) return "Moon Cat";
        if ("capybara".equals(type)) return "Capybara";
        if ("kitsune".equals(type)) return "Kitsune";
        if ("astronaut".equals(type)) return "Astronaut";
        String clean = type.replace('_', ' ');
        return Character.toUpperCase(clean.charAt(0)) + clean.substring(1);
    }
}
