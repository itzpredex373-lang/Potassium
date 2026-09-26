package com.predex.potassium.client;

public final class PotassiumPetTypes {
    /**
     * Built-in Potassium pets. All 20 use original procedural 3D models and
     * lightweight client-side animation. Extra pet types are intentionally
     * not part of the base mod.
     */
    public static final String[] TYPES = {
        "predex",
        "king_dragon",
        "devil",
        "black_dragon",
        "shadow_dragon",
        "kitsune",
        "cyber_cat",
        "mini_phoenix",
        "wolf",
        "bunny",
        "spirit",
        "inferno",
        "voidling",
        "guardian",
        "tiny_knight",
        "mini_golem",
        "mini_ender_dragon",
        "crystal_fairy",
        "tiny_reaper",
        "moon_cat"
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
        if ("kitsune".equals(type)) return "Kitsune";
        if ("cyber_cat".equals(type)) return "Cyber Cat";
        if ("mini_phoenix".equals(type)) return "Mini Phoenix";
        if ("wolf".equals(type)) return "Wolf";
        if ("bunny".equals(type)) return "Bunny";
        if ("spirit".equals(type)) return "Spirit";
        if ("inferno".equals(type)) return "Inferno";
        if ("voidling".equals(type)) return "Voidling";
        if ("guardian".equals(type)) return "Guardian";
        if ("tiny_knight".equals(type)) return "Tiny Knight";
        if ("mini_golem".equals(type)) return "Mini Golem";
        if ("mini_ender_dragon".equals(type)) return "Mini Ender Dragon";
        if ("crystal_fairy".equals(type)) return "Crystal Fairy";
        if ("tiny_reaper".equals(type)) return "Tiny Reaper";
        if ("moon_cat".equals(type)) return "Moon Cat";
        String clean = type.replace('_', ' ');
        return Character.toUpperCase(clean.charAt(0)) + clean.substring(1);
    }
}
