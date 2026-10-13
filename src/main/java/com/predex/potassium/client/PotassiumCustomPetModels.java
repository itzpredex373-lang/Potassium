package com.predex.potassium.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Twenty original, procedural 3D companion models.
 *
 * No third-party/Lunar assets are used. Each pet has its own geometry profile,
 * palette and lightweight idle animation so the base mod stays self-contained.
 */
public final class PotassiumCustomPetModels {
    private static final PetModel[] MODELS = createModels();
    private static final Map<String, ResourceLocation> TEXTURES = new HashMap<String, ResourceLocation>();

    private PotassiumCustomPetModels() {}

    public static boolean isCustom(String type) {
        return PotassiumPetTypes.indexOf(type) >= 0
                && isBuiltIn(type);
    }

    private static boolean isBuiltIn(String type) {
        return "predex".equals(type)
                || "king_dragon".equals(type)
                || "devil".equals(type)
                || "black_dragon".equals(type)
                || "shadow_dragon".equals(type)
                || "kitsune".equals(type)
                || "cyber_cat".equals(type)
                || "mini_phoenix".equals(type)
                || "wolf".equals(type)
                || "bunny".equals(type)
                || "spirit".equals(type)
                || "inferno".equals(type)
                || "voidling".equals(type)
                || "guardian".equals(type)
                || "tiny_knight".equals(type)
                || "mini_golem".equals(type)
                || "mini_ender_dragon".equals(type)
                || "crystal_fairy".equals(type)
                || "tiny_reaper".equals(type)
                || "moon_cat".equals(type);
    }

    public static boolean render(String type, float age, float scale) {
        if (!isBuiltIn(type)) return false;

        PetModel model = MODELS[PotassiumPetTypes.indexOf(type)];
        GlStateManager.pushMatrix();
        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture(type));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.scale(scale, scale, scale);
            model.render(null, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
        return true;
    }

    private static ResourceLocation getTexture(String type) {
        ResourceLocation cached = TEXTURES.get(type);
        if (cached != null) return cached;

        // Prefer the bundled, original 20-pet texture atlas. Crop the selected
        // 64x64 tile into a DynamicTexture so the existing per-part UV mapping
        // continues to work without changing model geometry.
        try {
            String[] atlasTypes = new String[]{
                "predex", "king_dragon", "devil", "black_dragon", "shadow_dragon",
                "kitsune", "cyber_cat", "mini_phoenix", "wolf", "bunny",
                "spirit", "inferno", "voidling", "guardian", "tiny_knight",
                "mini_golem", "mini_ender_dragon", "crystal_fairy", "tiny_reaper", "moon_cat"
            };
            int index = -1;
            for (int i = 0; i < atlasTypes.length; i++) {
                if (atlasTypes[i].equals(type)) { index = i; break; }
            }
            if (index >= 0) {
                ResourceLocation atlasLocation = new ResourceLocation("potassium", "textures/pets/pet_atlas.png");
                BufferedImage atlas = javax.imageio.ImageIO.read(
                    Minecraft.getMinecraft().getResourceManager().getResource(atlasLocation).getInputStream());
                if (atlas != null && atlas.getWidth() >= 320 && atlas.getHeight() >= 256) {
                    BufferedImage tile = atlas.getSubimage((index % 5) * 64, (index / 5) * 64, 64, 64);
                    ResourceLocation location = Minecraft.getMinecraft().getTextureManager()
                        .getDynamicTextureLocation("potassium_pet_" + type, new DynamicTexture(tile));
                    TEXTURES.put(type, location);
                    return location;
                }
            }
        } catch (Exception ignored) {
            // Keep the procedural fallback below for development or damaged resource packs.
        }

        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        int[] palette = paletteFor(type);
        boolean cute = isCute(type);
        boolean dangerous = isDangerous(type);
        for (int y = 0; y < 64; y++) {
            for (int x = 0; x < 64; x++) {
                int tileX = x & 15;
                int tileY = y & 15;
                int color = palette[(x / 16 + (y / 16) * 2) % palette.length];
                // Distinct scales/plates on dangerous pets; soft speckles on cute pets.
                if (dangerous && ((tileX + tileY) % 7 == 0 || tileY < 2)) {
                    color = palette[2];
                } else if (cute && ((tileX - tileY + 32) % 9 == 0 || (tileX % 8 == 1 && tileY % 8 == 1))) {
                    color = palette[1];
                } else if (!cute && !dangerous && ((tileX * 3 + tileY * 5) % 13 == 0)) {
                    color = palette[1];
                }
                // Each quadrant has a clear signature mark so UV-mapped parts
                // remain visually distinct instead of becoming one flat color.
                if (tileX >= 5 && tileX <= 10 && tileY >= 5 && tileY <= 10) {
                    if (dangerous && (tileY == 6 || tileY == 9 || tileX == 5 || tileX == 10)) color = palette[3];
                    if (cute && ((tileX == 6 || tileX == 9) && tileY >= 6 && tileY <= 9)) color = 0xFFFFF4FC;
                }
                image.setRGB(x, y, color);
            }
        }

        // A tiny face motif is placed in the first UV tile, used most visibly
        // by the head cubes. Cute pets get round eyes; dangerous pets get slanted eyes.
        int eye = dangerous ? palette[3] : (cute ? 0xFF252033 : palette[2]);
        for (int ex : new int[]{5, 9}) {
            for (int ey = 6; ey <= 9; ey++) image.setRGB(ex, ey, eye);
        }
        if (dangerous) {
            image.setRGB(4, 5, eye); image.setRGB(5, 6, eye);
            image.setRGB(11, 5, eye); image.setRGB(10, 6, eye);
        } else if (cute) {
            image.setRGB(5, 5, 0xFFFFFFFF); image.setRGB(10, 5, 0xFFFFFFFF);
        }

        ResourceLocation location = Minecraft.getMinecraft().getTextureManager()
                .getDynamicTextureLocation("potassium_pet_" + type, new DynamicTexture(image));
        TEXTURES.put(type, location);
        return location;
    }

    private static boolean isCute(String type) {
        return "kitsune".equals(type) || "cyber_cat".equals(type) || "bunny".equals(type)
                || "mini_phoenix".equals(type) || "crystal_fairy".equals(type) || "moon_cat".equals(type);
    }

    private static boolean isDangerous(String type) {
        return "predex".equals(type) || "king_dragon".equals(type) || "devil".equals(type)
                || "black_dragon".equals(type) || "shadow_dragon".equals(type)
                || "inferno".equals(type) || "voidling".equals(type)
                || "mini_ender_dragon".equals(type) || "tiny_reaper".equals(type);
    }

    private static int[] paletteFor(String type) {
        if ("predex".equals(type)) return new int[]{0xFF101526, 0xFF1678F5, 0xFF05070D, 0xFFFF3EAB};
        if ("king_dragon".equals(type)) return new int[]{0xFF29104E, 0xFF7039D4, 0xFF100A20, 0xFFFFB52E};
        if ("devil".equals(type)) return new int[]{0xFF4B0A18, 0xFFB5162A, 0xFF1C050A, 0xFFFF6A27};
        if ("black_dragon".equals(type)) return new int[]{0xFF171A20, 0xFF3B414B, 0xFF07090D, 0xFFEF3148};
        if ("shadow_dragon".equals(type)) return new int[]{0xFF21112F, 0xFF62308A, 0xFF090610, 0xFFB84DFF};
        if ("kitsune".equals(type)) return new int[]{0xFFFFA44A, 0xFFFFD6A0, 0xFF9B4A24, 0xFFFAF3E7};
        if ("cyber_cat".equals(type)) return new int[]{0xFF153442, 0xFF27C9E8, 0xFF07151E, 0xFFB8FAFF};
        if ("mini_phoenix".equals(type)) return new int[]{0xFFCE3B16, 0xFFFF9C27, 0xFF762016, 0xFFFFE66D};
        if ("wolf".equals(type)) return new int[]{0xFF606B78, 0xFFB6C0CA, 0xFF333A43, 0xFF9BC7F5};
        if ("bunny".equals(type)) return new int[]{0xFFF3CDE0, 0xFFFFF0F7, 0xFFCE91B7, 0xFFEA9AB8};
        if ("spirit".equals(type)) return new int[]{0xFF1A596B, 0xFF5DE4E8, 0xFF0A2638, 0xFFB8FFFF};
        if ("inferno".equals(type)) return new int[]{0xFF66180D, 0xFFFF4A12, 0xFF240806, 0xFFFFD447};
        if ("voidling".equals(type)) return new int[]{0xFF170D2C, 0xFF4E2A83, 0xFF070410, 0xFFB34DFF};
        if ("guardian".equals(type)) return new int[]{0xFF28666B, 0xFF54D4CB, 0xFF15363B, 0xFFB6FFFF};
        if ("tiny_knight".equals(type)) return new int[]{0xFF596575, 0xFFB5C3D0, 0xFF303946, 0xFFFFD77A};
        if ("mini_golem".equals(type)) return new int[]{0xFF6B6B68, 0xFFAAA99D, 0xFF353633, 0xFF7DD8C4};
        if ("mini_ender_dragon".equals(type)) return new int[]{0xFF241238, 0xFF6938A0, 0xFF0B0712, 0xFF67F5BC};
        if ("crystal_fairy".equals(type)) return new int[]{0xFF91E7F5, 0xFFE7FFFF, 0xFF4E9FCB, 0xFFFFA7DE};
        if ("tiny_reaper".equals(type)) return new int[]{0xFF20202D, 0xFF53536D, 0xFF08080F, 0xFFB7F4FF};
        return new int[]{0xFF6962A5, 0xFFB8A8FF, 0xFF302A52, 0xFFFFE7A6};
    }

    private static PetModel[] createModels() {
        PetModel[] models = new PetModel[PotassiumPetTypes.TYPES.length];
        for (int i = 0; i < models.length; i++) {
            models[i] = new PetModel(PotassiumPetTypes.TYPES[i]);
        }
        return models;
    }

    /**
     * A compact procedural model factory. The first two parts are body/head;
     * parts 2-5 are legs/wings/arms, part 6 is a tail or weapon and part 7 is
     * an extra signature piece. The geometry differs for every built-in pet.
     */
    private static final class PetModel extends ModelBase {
        private final String type;
        private final ModelRenderer[] p = new ModelRenderer[8];
        private ModelRenderer[] details = new ModelRenderer[0];

        private PetModel(String type) {
            this.type = type;
            textureWidth = 64;
            textureHeight = 64;
            build();
            details = createDetails();
        }

        private void build() {
            if ("predex".equals(type)) {
                p[0]=box(-3,-3,-2,6,7,5); p[1]=head(-2,-8,-2,4,5,4);
                p[2]=box(3,-2,-1,2,5,2); p[3]=box(-5,-2,-1,2,5,2);
                p[4]=box(1,3,-1,2,3,2); p[5]=box(-3,3,-1,2,3,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-1,-6,-3,2,2,1);
            } else if ("king_dragon".equals(type)) {
                p[0]=box(-3,-2,-3,6,5,7); p[1]=head(-2,-7,-4,4,4,4);
                p[2]=box(3,-3,-1,4,1,7); p[3]=box(-7,-3,-1,4,1,7);
                p[4]=box(1,2,-2,2,4,2); p[5]=box(-3,2,-2,2,4,2);
                p[6]=box(-2,-1,4,4,2,7); p[7]=box(-1,-9,-2,2,2,3);
            } else if ("devil".equals(type)) {
                p[0]=box(-3,-2,-2,6,6,5); p[1]=head(-2,-7,-3,4,5,4);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(-1,0,3,2,2,5); p[7]=box(-3,-10,-1,2,3,2);
            } else if ("black_dragon".equals(type)) {
                p[0]=box(-3,-2,-4,6,5,8); p[1]=head(-2,-8,-5,4,5,4);
                p[2]=box(3,-3,-1,5,1,8); p[3]=box(-8,-3,-1,5,1,8);
                p[4]=box(1,2,-3,2,4,2); p[5]=box(-3,2,-3,2,4,2);
                p[6]=box(-2,-1,4,4,2,8); p[7]=box(-1,-11,-3,2,3,2);
            } else if ("shadow_dragon".equals(type)) {
                p[0]=box(-3,-3,-3,6,6,7); p[1]=head(-2,-8,-4,4,4,4);
                p[2]=box(3,-2,-1,5,1,6); p[3]=box(-8,-2,-1,5,1,6);
                p[4]=box(1,2,-2,2,4,2); p[5]=box(-3,2,-2,2,4,2);
                p[6]=box(-2,-1,4,4,2,7); p[7]=box(-1,-10,-2,2,3,2);
            } else if ("kitsune".equals(type)) {
                p[0]=box(-3,-2,-2,6,6,6); p[1]=head(-3,-7,-4,6,5,4);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(2,-3,2,2,2,6); p[7]=box(-2,-10,-2,4,4,1);
            } else if ("cyber_cat".equals(type)) {
                p[0]=box(-3,-2,-3,6,5,7); p[1]=head(-3,-7,-4,6,5,4);
                p[2]=box(1,1,-2,2,4,2); p[3]=box(-3,1,-2,2,4,2);
                p[4]=box(1,1,1,2,4,2); p[5]=box(-3,1,1,2,4,2);
                p[6]=box(2,-2,2,2,2,6); p[7]=box(-2,-4,-5,4,1,1);
            } else if ("mini_phoenix".equals(type)) {
                p[0]=box(-3,-2,-3,6,5,6); p[1]=head(-2,-7,-4,4,4,4);
                p[2]=box(3,-2,-3,1,3,7); p[3]=box(-4,-2,-3,1,3,7);
                p[4]=box(1,1,0,2,2,2); p[5]=box(-3,1,0,2,2,2);
                p[6]=box(-2,-1,3,4,2,6); p[7]=box(-1,-9,-5,2,3,2);
            } else if ("wolf".equals(type)) {
                p[0]=box(-3,-2,-3,6,6,7); p[1]=head(-2,-7,-4,4,5,4);
                p[2]=box(1,1,-2,2,4,2); p[3]=box(-3,1,-2,2,4,2);
                p[4]=box(1,1,1,2,4,2); p[5]=box(-3,1,1,2,4,2);
                p[6]=box(2,-2,3,2,2,6); p[7]=box(-1,-10,-2,2,3,2);
            } else if ("bunny".equals(type)) {
                p[0]=box(-3,-2,-2,6,5,6); p[1]=head(-2,-7,-3,4,4,4);
                p[2]=box(1,1,-1,2,4,2); p[3]=box(-3,1,-1,2,4,2);
                p[4]=box(1,1,1,2,3,2); p[5]=box(-3,1,1,2,3,2);
                p[6]=box(2,-1,2,2,2,4); p[7]=box(-3,-12,-2,2,6,2);
            } else if ("spirit".equals(type)) {
                p[0]=box(-3,-3,-2,6,6,5); p[1]=head(-2,-8,-2,4,4,4);
                p[2]=box(3,-2,-1,2,2,5); p[3]=box(-5,-2,-1,2,2,5);
                p[4]=box(1,2,-1,2,2,2); p[5]=box(-3,2,-1,2,2,2);
                p[6]=box(-1,-1,2,2,2,5); p[7]=box(-1,-11,-1,2,3,2);
            } else if ("inferno".equals(type)) {
                p[0]=box(-3,-2,-3,6,6,6); p[1]=head(-2,-8,-3,4,5,4);
                p[2]=box(3,-2,-1,2,5,2); p[3]=box(-5,-2,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-1,-12,-1,2,4,2);
            } else if ("voidling".equals(type)) {
                p[0]=box(-3,-3,-3,6,6,6); p[1]=head(-2,-8,-3,4,4,4);
                p[2]=box(3,-2,-1,2,4,2); p[3]=box(-5,-2,-1,2,4,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-1,-11,-1,2,3,2);
            } else if ("guardian".equals(type)) {
                p[0]=box(-4,-3,-4,8,7,8); p[1]=head(-3,-9,-3,6,5,6);
                p[2]=box(4,-1,-1,2,5,2); p[3]=box(-6,-1,-1,2,5,2);
                p[4]=box(2,3,-2,2,3,2); p[5]=box(-4,3,-2,2,3,2);
                p[6]=box(-1,-1,4,2,2,5); p[7]=box(-1,-12,-1,2,3,2);
            } else if ("tiny_knight".equals(type)) {
                p[0]=box(-3,-1,-2,6,5,5); p[1]=head(-3,-8,-2,6,5,5);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(4,-4,-1,1,2,8); p[7]=box(-2,-4,-4,4,2,1);
            } else if ("mini_golem".equals(type)) {
                p[0]=box(-4,-2,-3,8,7,6); p[1]=head(-3,-9,-3,6,5,5);
                p[2]=box(4,-1,-1,3,5,2); p[3]=box(-7,-1,-1,3,5,2);
                p[4]=box(2,3,-1,2,4,2); p[5]=box(-4,3,-1,2,4,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-2,-5,-4,4,2,1);
            } else if ("mini_ender_dragon".equals(type)) {
                p[0]=box(-3,-2,-4,6,5,8); p[1]=head(-2,-8,-5,4,5,5);
                p[2]=box(3,-3,-1,4,1,8); p[3]=box(-7,-3,-1,4,1,8);
                p[4]=box(1,2,-3,2,4,2); p[5]=box(-3,2,-3,2,4,2);
                p[6]=box(-2,-1,4,4,2,8); p[7]=box(-1,-11,-3,2,3,2);
            } else if ("crystal_fairy".equals(type)) {
                p[0]=box(-2,-2,-2,4,5,4); p[1]=head(-2,-8,-2,4,4,4);
                p[2]=box(2,-3,-1,1,4,6); p[3]=box(-3,-3,-1,1,4,6);
                p[4]=box(1,2,-1,1,3,1); p[5]=box(-2,2,-1,1,3,1);
                p[6]=box(-1,-1,2,2,2,4); p[7]=box(-1,-11,-1,2,3,2);
            } else if ("tiny_reaper".equals(type)) {
                p[0]=box(-3,-1,-2,6,6,4); p[1]=head(-3,-8,-2,6,5,4);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,3,-1,2,3,2); p[5]=box(-3,3,-1,2,3,2);
                p[6]=box(4,-7,-1,1,2,8); p[7]=box(-1,-10,-1,2,2,1);
            } else { // moon_cat
                p[0]=box(-3,-2,-3,6,5,7); p[1]=head(-3,-7,-4,6,5,4);
                p[2]=box(1,1,-2,2,4,2); p[3]=box(-3,1,-2,2,4,2);
                p[4]=box(1,1,1,2,4,2); p[5]=box(-3,1,1,2,4,2);
                p[6]=box(2,-2,2,2,2,6); p[7]=box(-1,-10,-3,2,2,2);
            }
        }

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            float bob = (float)Math.sin(ageInTicks * 0.16F) * 0.28F;
            float sway = (float)Math.sin(ageInTicks * 0.11F) * 0.12F;
            float step = (float)Math.sin(ageInTicks * 0.34F) * 0.38F;
            float flap = (float)Math.sin(ageInTicks * 0.42F) * 0.48F;

            GlStateManager.translate(0.0F, bob * scale, 0.0F);
            p[1].rotateAngleY = netHeadYaw * 0.017453292F;
            p[1].rotateAngleX = headPitch * 0.017453292F;
            p[2].rotateAngleX = step;
            p[3].rotateAngleX = -step;
            p[4].rotateAngleX = -step;
            p[5].rotateAngleX = step;
            p[6].rotateAngleY = sway;
            p[7].rotateAngleZ = (float)Math.sin(ageInTicks * 0.23F) * 0.14F;

            if ("king_dragon".equals(type) || "black_dragon".equals(type)
                    || "shadow_dragon".equals(type) || "mini_phoenix".equals(type)
                    || "mini_ender_dragon".equals(type) || "crystal_fairy".equals(type)) {
                p[2].rotateAngleZ = -flap;
                p[3].rotateAngleZ = flap;
            }

            if ("tiny_reaper".equals(type) || "tiny_knight".equals(type)) {
                p[6].rotateAngleZ = -1.05F + sway;
            }

            if ("bunny".equals(type)) {
                p[7].rotateAngleX = (float)Math.sin(ageInTicks * 0.55F) * 0.20F;
            }

            setColor();
            for (ModelRenderer part : p) {
                if (part != null) part.render(scale);
            }
            for (ModelRenderer detail : details) {
                if (detail != null) detail.render(scale);
            }
        }

        private void setColor() {
            float r=1.0F, g=1.0F, b=1.0F;
            if ("predex".equals(type)) { r=0.10F; g=0.45F; b=1.00F; }
            else if ("king_dragon".equals(type)) { r=0.25F; g=0.05F; b=0.75F; }
            else if ("devil".equals(type)) { r=0.80F; g=0.06F; b=0.12F; }
            else if ("black_dragon".equals(type)) { r=0.04F; g=0.05F; b=0.08F; }
            else if ("shadow_dragon".equals(type)) { r=0.20F; g=0.04F; b=0.35F; }
            else if ("kitsune".equals(type)) { r=1.00F; g=0.32F; b=0.06F; }
            else if ("cyber_cat".equals(type)) { r=0.08F; g=0.55F; b=0.85F; }
            else if ("mini_phoenix".equals(type)) { r=1.00F; g=0.28F; b=0.04F; }
            else if ("wolf".equals(type)) { r=0.55F; g=0.58F; b=0.64F; }
            else if ("bunny".equals(type)) { r=0.92F; g=0.72F; b=0.82F; }
            else if ("spirit".equals(type)) { r=0.25F; g=0.90F; b=0.95F; }
            else if ("inferno".equals(type)) { r=1.00F; g=0.38F; b=0.02F; }
            else if ("voidling".equals(type)) { r=0.12F; g=0.02F; b=0.20F; }
            else if ("guardian".equals(type)) { r=0.20F; g=0.70F; b=0.75F; }
            else if ("tiny_knight".equals(type)) { r=0.42F; g=0.45F; b=0.55F; }
            else if ("mini_golem".equals(type)) { r=0.48F; g=0.50F; b=0.55F; }
            else if ("mini_ender_dragon".equals(type)) { r=0.18F; g=0.02F; b=0.28F; }
            else if ("crystal_fairy".equals(type)) { r=0.35F; g=0.90F; b=1.00F; }
            else if ("tiny_reaper".equals(type)) { r=0.12F; g=0.08F; b=0.18F; }
            else if ("moon_cat".equals(type)) { r=0.50F; g=0.42F; b=0.82F; }
            // Textures carry each pet's full palette; keep geometry untinted.
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }


        /**
         * Signature silhouettes layered over the base body: tall ears for cute
         * pets, horns/spines for dangerous pets, and armor fins for guardians.
         * These are real cuboids, not just texture decals.
         */
        private ModelRenderer[] createDetails() {
            if ("bunny".equals(type)) {
                return new ModelRenderer[]{box(-2,-14,-2,1,7,2), box(1,-14,-2,1,7,2),
                        box(-1,-5,-3,2,1,1)};
            }
            if ("kitsune".equals(type)) {
                return new ModelRenderer[]{box(-3,-10,-2,2,4,2), box(1,-10,-2,2,4,2),
                        box(-2,-2,4,2,2,6), box(0,-2,5,2,2,6), box(2,-2,4,2,2,5)};
            }
            if ("cyber_cat".equals(type) || "moon_cat".equals(type)) {
                return new ModelRenderer[]{box(-3,-10,-2,2,4,2), box(1,-10,-2,2,4,2),
                        box(-1,-3,-4,2,1,1)};
            }
            if ("wolf".equals(type)) {
                return new ModelRenderer[]{box(-2,-9,-2,1,2,2), box(1,-9,-2,1,2,2),
                        box(-2,-1,4,1,1,2)};
            }
            if ("king_dragon".equals(type) || "black_dragon".equals(type)
                    || "shadow_dragon".equals(type) || "mini_ender_dragon".equals(type)) {
                return new ModelRenderer[]{box(-2,-10,-4,1,3,2), box(1,-10,-4,1,3,2),
                        box(-3,-1,2,2,2,3), box(1,-1,2,2,2,3),
                        box(-1,0,5,2,2,3), box(-2,-3,-5,2,2,2)};
            }
            if ("devil".equals(type) || "inferno".equals(type) || "predex".equals(type)) {
                return new ModelRenderer[]{box(-3,-10,-2,2,4,2), box(1,-10,-2,2,4,2),
                        box(-3,-3,2,2,2,2), box(1,-3,2,2,2,2),
                        box(-1,-1,4,2,2,2)};
            }
            if ("tiny_reaper".equals(type) || "voidling".equals(type)) {
                return new ModelRenderer[]{box(-3,-10,-2,2,3,2), box(1,-10,-2,2,3,2),
                        box(-2,-2,2,1,2,2), box(1,-2,2,1,2,2)};
            }
            if ("guardian".equals(type) || "tiny_knight".equals(type) || "mini_golem".equals(type)) {
                return new ModelRenderer[]{box(-5,-2,-2,2,3,4), box(3,-2,-2,2,3,4),
                        box(-2,-5,-4,4,2,1)};
            }
            if ("mini_phoenix".equals(type) || "crystal_fairy".equals(type)) {
                return new ModelRenderer[]{box(-4,-2,-2,2,3,1), box(2,-2,-2,2,3,1),
                        box(-1,-9,-3,2,2,2)};
            }
            if ("spirit".equals(type)) {
                return new ModelRenderer[]{box(-4,-1,-1,1,3,3), box(3,-1,-1,1,3,3),
                        box(-1,-10,-1,2,2,2)};
            }
            return new ModelRenderer[]{box(-3,-9,-2,2,3,2), box(1,-9,-2,2,3,2)};
        }

        private ModelRenderer head(float x, float y, float z, int w, int h, int d) {
            ModelRenderer part = new ModelRenderer(this, 0, 0);
            part.addBox(x, y, z, w, h, d);
            return part;
        }

        private ModelRenderer box(float x, float y, float z, int w, int h, int d) {
            // Give each part its own 16x16 atlas quadrant. This prevents all
            // cubes sampling the same UV patch, which caused the old "same
            // texture everywhere" appearance and makes the mapping deterministic.
            int seed = Math.abs((int)(x * 31.0F + y * 17.0F + z * 13.0F + w * 7 + h * 5 + d * 3));
            int u = (seed % 4) * 16;
            int v = ((seed / 4) % 4) * 16;
            ModelRenderer part = new ModelRenderer(this, u, v);
            part.addBox(x, y, z, w, h, d);
            return part;
        }
    }
}
