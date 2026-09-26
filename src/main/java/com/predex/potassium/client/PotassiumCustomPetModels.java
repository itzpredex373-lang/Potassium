package com.predex.potassium.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

/**
 * Twenty original, procedural 3D companion models.
 *
 * No third-party/Lunar assets are used. Each pet has its own geometry profile,
 * palette and lightweight idle animation so the base mod stays self-contained.
 */
public final class PotassiumCustomPetModels {
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

        PetModel model = new PetModel(type);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.scale(scale, scale, scale);
            model.render(null, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
        return true;
    }

    /**
     * A compact procedural model factory. The first two parts are body/head;
     * parts 2-5 are legs/wings/arms, part 6 is a tail or weapon and part 7 is
     * an extra signature piece. The geometry differs for every built-in pet.
     */
    private static final class PetModel extends ModelBase {
        private final String type;
        private final ModelRenderer[] p = new ModelRenderer[8];

        private PetModel(String type) {
            this.type = type;
            textureWidth = 32;
            textureHeight = 32;
            build();
        }

        private void build() {
            if ("predex".equals(type)) {
                p[0]=box(-3,-3,-2,6,7,5); p[1]=box(-2,-8,-2,4,5,4);
                p[2]=box(3,-2,-1,2,5,2); p[3]=box(-5,-2,-1,2,5,2);
                p[4]=box(1,3,-1,2,3,2); p[5]=box(-3,3,-1,2,3,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-1,-6,-3,2,2,1);
            } else if ("king_dragon".equals(type)) {
                p[0]=box(-3,-2,-3,6,5,7); p[1]=box(-2,-7,-4,4,4,4);
                p[2]=box(3,-3,-1,4,1,7); p[3]=box(-7,-3,-1,4,1,7);
                p[4]=box(1,2,-2,2,4,2); p[5]=box(-3,2,-2,2,4,2);
                p[6]=box(-2,-1,4,4,2,7); p[7]=box(-1,-9,-2,2,2,3);
            } else if ("devil".equals(type)) {
                p[0]=box(-3,-2,-2,6,6,5); p[1]=box(-2,-7,-3,4,5,4);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(-1,0,3,2,2,5); p[7]=box(-3,-10,-1,2,3,2);
            } else if ("black_dragon".equals(type)) {
                p[0]=box(-3,-2,-4,6,5,8); p[1]=box(-2,-8,-5,4,5,4);
                p[2]=box(3,-3,-1,5,1,8); p[3]=box(-8,-3,-1,5,1,8);
                p[4]=box(1,2,-3,2,4,2); p[5]=box(-3,2,-3,2,4,2);
                p[6]=box(-2,-1,4,4,2,8); p[7]=box(-1,-11,-3,2,3,2);
            } else if ("shadow_dragon".equals(type)) {
                p[0]=box(-3,-3,-3,6,6,7); p[1]=box(-2,-8,-4,4,4,4);
                p[2]=box(3,-2,-1,5,1,6); p[3]=box(-8,-2,-1,5,1,6);
                p[4]=box(1,2,-2,2,4,2); p[5]=box(-3,2,-2,2,4,2);
                p[6]=box(-2,-1,4,4,2,7); p[7]=box(-1,-10,-2,2,3,2);
            } else if ("kitsune".equals(type)) {
                p[0]=box(-3,-2,-2,6,6,6); p[1]=box(-3,-7,-4,6,5,4);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(2,-3,2,2,2,6); p[7]=box(-2,-10,-2,4,4,1);
            } else if ("cyber_cat".equals(type)) {
                p[0]=box(-3,-2,-3,6,5,7); p[1]=box(-3,-7,-4,6,5,4);
                p[2]=box(1,1,-2,2,4,2); p[3]=box(-3,1,-2,2,4,2);
                p[4]=box(1,1,1,2,4,2); p[5]=box(-3,1,1,2,4,2);
                p[6]=box(2,-2,2,2,2,6); p[7]=box(-2,-4,-5,4,1,1);
            } else if ("mini_phoenix".equals(type)) {
                p[0]=box(-3,-2,-3,6,5,6); p[1]=box(-2,-7,-4,4,4,4);
                p[2]=box(3,-2,-3,1,3,7); p[3]=box(-4,-2,-3,1,3,7);
                p[4]=box(1,1,0,2,2,2); p[5]=box(-3,1,0,2,2,2);
                p[6]=box(-2,-1,3,4,2,6); p[7]=box(-1,-9,-5,2,3,2);
            } else if ("wolf".equals(type)) {
                p[0]=box(-3,-2,-3,6,6,7); p[1]=box(-2,-7,-4,4,5,4);
                p[2]=box(1,1,-2,2,4,2); p[3]=box(-3,1,-2,2,4,2);
                p[4]=box(1,1,1,2,4,2); p[5]=box(-3,1,1,2,4,2);
                p[6]=box(2,-2,3,2,2,6); p[7]=box(-1,-10,-2,2,3,2);
            } else if ("bunny".equals(type)) {
                p[0]=box(-3,-2,-2,6,5,6); p[1]=box(-2,-7,-3,4,4,4);
                p[2]=box(1,1,-1,2,4,2); p[3]=box(-3,1,-1,2,4,2);
                p[4]=box(1,1,1,2,3,2); p[5]=box(-3,1,1,2,3,2);
                p[6]=box(2,-1,2,2,2,4); p[7]=box(-3,-12,-2,2,6,2);
            } else if ("spirit".equals(type)) {
                p[0]=box(-3,-3,-2,6,6,5); p[1]=box(-2,-8,-2,4,4,4);
                p[2]=box(3,-2,-1,2,2,5); p[3]=box(-5,-2,-1,2,2,5);
                p[4]=box(1,2,-1,2,2,2); p[5]=box(-3,2,-1,2,2,2);
                p[6]=box(-1,-1,2,2,2,5); p[7]=box(-1,-11,-1,2,3,2);
            } else if ("inferno".equals(type)) {
                p[0]=box(-3,-2,-3,6,6,6); p[1]=box(-2,-8,-3,4,5,4);
                p[2]=box(3,-2,-1,2,5,2); p[3]=box(-5,-2,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-1,-12,-1,2,4,2);
            } else if ("voidling".equals(type)) {
                p[0]=box(-3,-3,-3,6,6,6); p[1]=box(-2,-8,-3,4,4,4);
                p[2]=box(3,-2,-1,2,4,2); p[3]=box(-5,-2,-1,2,4,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-1,-11,-1,2,3,2);
            } else if ("guardian".equals(type)) {
                p[0]=box(-4,-3,-4,8,7,8); p[1]=box(-3,-9,-3,6,5,6);
                p[2]=box(4,-1,-1,2,5,2); p[3]=box(-6,-1,-1,2,5,2);
                p[4]=box(2,3,-2,2,3,2); p[5]=box(-4,3,-2,2,3,2);
                p[6]=box(-1,-1,4,2,2,5); p[7]=box(-1,-12,-1,2,3,2);
            } else if ("tiny_knight".equals(type)) {
                p[0]=box(-3,-1,-2,6,5,5); p[1]=box(-3,-8,-2,6,5,5);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,2,-1,2,3,2); p[5]=box(-3,2,-1,2,3,2);
                p[6]=box(4,-4,-1,1,2,8); p[7]=box(-2,-4,-4,4,2,1);
            } else if ("mini_golem".equals(type)) {
                p[0]=box(-4,-2,-3,8,7,6); p[1]=box(-3,-9,-3,6,5,5);
                p[2]=box(4,-1,-1,3,5,2); p[3]=box(-7,-1,-1,3,5,2);
                p[4]=box(2,3,-1,2,4,2); p[5]=box(-4,3,-1,2,4,2);
                p[6]=box(-1,-1,3,2,2,5); p[7]=box(-2,-5,-4,4,2,1);
            } else if ("mini_ender_dragon".equals(type)) {
                p[0]=box(-3,-2,-4,6,5,8); p[1]=box(-2,-8,-5,4,5,5);
                p[2]=box(3,-3,-1,4,1,8); p[3]=box(-7,-3,-1,4,1,8);
                p[4]=box(1,2,-3,2,4,2); p[5]=box(-3,2,-3,2,4,2);
                p[6]=box(-2,-1,4,4,2,8); p[7]=box(-1,-11,-3,2,3,2);
            } else if ("crystal_fairy".equals(type)) {
                p[0]=box(-2,-2,-2,4,5,4); p[1]=box(-2,-8,-2,4,4,4);
                p[2]=box(2,-3,-1,1,4,6); p[3]=box(-3,-3,-1,1,4,6);
                p[4]=box(1,2,-1,1,3,1); p[5]=box(-2,2,-1,1,3,1);
                p[6]=box(-1,-1,2,2,2,4); p[7]=box(-1,-11,-1,2,3,2);
            } else if ("tiny_reaper".equals(type)) {
                p[0]=box(-3,-1,-2,6,6,4); p[1]=box(-3,-8,-2,6,5,4);
                p[2]=box(3,-1,-1,2,5,2); p[3]=box(-5,-1,-1,2,5,2);
                p[4]=box(1,3,-1,2,3,2); p[5]=box(-3,3,-1,2,3,2);
                p[6]=box(4,-7,-1,1,2,8); p[7]=box(-1,-10,-1,2,2,1);
            } else { // moon_cat
                p[0]=box(-3,-2,-3,6,5,7); p[1]=box(-3,-7,-4,6,5,4);
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
        }

        private void setColor() {
            float r=0.45F, g=0.55F, b=0.70F;
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
            GlStateManager.color(r, g, b, 1.0F);
        }

        private static ModelRenderer box(float x, float y, float z, int w, int h, int d) {
            ModelRenderer part = new ModelRenderer(new ModelBase() {}, 0, 0);
            part.addBox(x, y, z, w, h, d);
            return part;
        }
    }
}
