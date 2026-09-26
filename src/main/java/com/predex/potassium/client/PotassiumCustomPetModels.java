package com.predex.potassium.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/**
 * Original procedural client-only pet models.
 * Geometry is built from ModelRenderer boxes and animated at render time.
 */
public final class PotassiumCustomPetModels {
    private static final ModelBase BASE = new ModelBase() {};
    private static final ResourceLocation MINI_PHOENIX_TEXTURE =
            new ResourceLocation("potassium", "textures/pets/mini_phoenix.png");
    public static final MiniPhoenix PHOENIX = new MiniPhoenix();
    public static final TinyReaper REAPER = new TinyReaper();
    public static final CyberCat CYBER_CAT = new CyberCat();

    private PotassiumCustomPetModels() {}

    public static boolean isCustom(String type) {
        return "mini_phoenix".equals(type)
                || "tiny_reaper".equals(type)
                || "cyber_cat".equals(type);
    }

    public static boolean render(String type, float age, float scale) {
        ModelBase model;
        if ("mini_phoenix".equals(type)) model = PHOENIX;
        else if ("tiny_reaper".equals(type)) model = REAPER;
        else if ("cyber_cat".equals(type)) model = CYBER_CAT;
        else return false;

        GlStateManager.pushMatrix();
        try {
            GlStateManager.scale(scale, scale, scale);
            if ("mini_phoenix".equals(type)) {
                GlStateManager.enableTexture2D();
                net.minecraft.client.Minecraft.getMinecraft().getTextureManager().bindTexture(MINI_PHOENIX_TEXTURE);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GlStateManager.disableTexture2D();
            }
            model.render(null, 0.0F, 0.0F, age, 0.0F, 0.0F, 0.0625F);
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
        return true;
    }

    public static final class MiniPhoenix extends ModelBase {
        private final ModelRenderer body = box(0, 0, 5, 4, 7, -2.5F, -2.0F, -3.5F);
        private final ModelRenderer head = box(32, 0, 4, 4, 4, -2.0F, -5.0F, -2.0F);
        private final ModelRenderer beak = box(0, 34, 2, 1, 2, -1.0F, -4.5F, -5.0F);
        private final ModelRenderer wingL = box(17, 18, 1, 2, 7, 2.5F, -1.5F, -3.0F);
        private final ModelRenderer wingR = box(0, 18, 1, 2, 7, -3.5F, -1.5F, -3.0F);
        private final ModelRenderer tailL = box(34, 18, 2, 2, 5, 0.5F, -1.0F, 2.5F);
        private final ModelRenderer tailR = box(42, 18, 2, 2, 5, -2.5F, -1.0F, 2.5F);
        private final ModelRenderer crest = box(51, 18, 1, 3, 2, -0.5F, -8.0F, -0.5F);

        public MiniPhoenix() { textureWidth = 64; textureHeight = 64; }

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            float flap = (float) Math.sin(ageInTicks * 0.42F) * 0.62F;
            float tail = (float) Math.sin(ageInTicks * 0.18F) * 0.12F;
            float bob = (float) Math.sin(ageInTicks * 0.20F) * 0.45F;
            GlStateManager.translate(0.0F, bob * scale, 0.0F);
            head.rotateAngleY = netHeadYaw * 0.017453292F;
            head.rotateAngleX = headPitch * 0.017453292F;
            wingL.rotateAngleZ = -flap; wingR.rotateAngleZ = flap;
            tailL.rotateAngleY = tail; tailR.rotateAngleY = -tail;
            crest.rotateAngleZ = (float) Math.sin(ageInTicks * 0.30F) * 0.10F;
            color(1.0F, 1.0F, 1.0F, 1.0F);
            body.render(scale); head.render(scale); beak.render(scale);
            wingL.render(scale); wingR.render(scale);
            tailL.render(scale); tailR.render(scale); crest.render(scale);
        }
    }

    public static final class TinyReaper extends ModelBase {
        private final ModelRenderer robe = box(6, 7, 4, -3.0F, -1.0F, -2.0F);
        private final ModelRenderer hood = box(6, 5, 5, -3.0F, -6.0F, -2.5F);
        private final ModelRenderer face = box(4, 3, 3, -2.0F, -5.0F, -4.0F);
        private final ModelRenderer armL = box(2, 6, 2, 3.0F, -1.0F, -1.0F);
        private final ModelRenderer armR = box(2, 6, 2, -5.0F, -1.0F, -1.0F);
        private final ModelRenderer blade = box(1, 1, 7, 4.5F, -7.0F, -1.0F);
        private final ModelRenderer bladeTip = box(1, 1, 3, 4.5F, -8.0F, -3.0F);

        public TinyReaper() { textureWidth = 32; textureHeight = 32; }

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            float sway = (float) Math.sin(ageInTicks * 0.16F) * 0.16F;
            float armSwing = (float) Math.sin(ageInTicks * 0.25F) * 0.20F;
            float scythe = (float) Math.sin(ageInTicks * 0.12F) * 0.28F;
            float bob = (float) Math.sin(ageInTicks * 0.18F) * 0.22F;
            GlStateManager.translate(0.0F, bob * scale, 0.0F);
            hood.rotateAngleY = netHeadYaw * 0.017453292F;
            hood.rotateAngleX = headPitch * 0.017453292F;
            face.rotateAngleY = hood.rotateAngleY;
            armL.rotateAngleX = armSwing; armR.rotateAngleX = -armSwing;
            robe.rotateAngleZ = sway;
            blade.rotateAngleZ = -1.05F + scythe; bladeTip.rotateAngleZ = -1.05F + scythe;
            color(0.07F, 0.07F, 0.09F, 1.0F); robe.render(scale);
            color(0.13F, 0.12F, 0.18F, 1.0F); hood.render(scale);
            color(0.55F, 0.02F, 0.70F, 1.0F); face.render(scale);
            color(0.08F, 0.07F, 0.12F, 1.0F); armL.render(scale); armR.render(scale);
            color(0.72F, 0.72F, 0.80F, 1.0F); blade.render(scale); bladeTip.render(scale);
        }
    }

    public static final class CyberCat extends ModelBase {
        private final ModelRenderer body = box(5, 4, 7, -2.5F, -2.0F, -3.5F);
        private final ModelRenderer head = box(5, 4, 5, -2.5F, -5.5F, -3.5F);
        private final ModelRenderer earL = box(2, 2, 2, 1.0F, -8.0F, -2.0F);
        private final ModelRenderer earR = box(2, 2, 2, -3.0F, -8.0F, -2.0F);
        private final ModelRenderer legFL = box(2, 4, 2, 1.0F, 1.0F, -2.5F);
        private final ModelRenderer legFR = box(2, 4, 2, -3.0F, 1.0F, -2.5F);
        private final ModelRenderer legBL = box(2, 4, 2, 1.0F, 1.0F, 1.0F);
        private final ModelRenderer legBR = box(2, 4, 2, -3.0F, 1.0F, 1.0F);
        private final ModelRenderer tail = box(2, 2, 6, 2.0F, -1.5F, 2.0F);
        private final ModelRenderer visor = box(4, 1, 1, -2.0F, -4.5F, -4.1F);

        public CyberCat() { textureWidth = 32; textureHeight = 32; }

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingAmount,
                           float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            float walk = (float) Math.sin(ageInTicks * 0.38F) * 0.42F;
            float tailWave = (float) Math.sin(ageInTicks * 0.28F) * 0.30F;
            float ear = (float) Math.sin(ageInTicks * 0.50F) * 0.08F;
            float bob = (float) Math.abs(Math.sin(ageInTicks * 0.38F)) * 0.12F;
            GlStateManager.translate(0.0F, bob * scale, 0.0F);
            head.rotateAngleY = netHeadYaw * 0.017453292F;
            head.rotateAngleX = headPitch * 0.017453292F;
            earL.rotateAngleZ = ear; earR.rotateAngleZ = -ear;
            legFL.rotateAngleX = walk; legBR.rotateAngleX = walk;
            legFR.rotateAngleX = -walk; legBL.rotateAngleX = -walk;
            tail.rotateAngleY = tailWave;
            color(0.08F, 0.10F, 0.14F, 1.0F); body.render(scale);
            color(0.12F, 0.15F, 0.22F, 1.0F); head.render(scale);
            color(0.16F, 0.20F, 0.28F, 1.0F); earL.render(scale); earR.render(scale);
            color(0.07F, 0.09F, 0.13F, 1.0F);
            legFL.render(scale); legFR.render(scale); legBL.render(scale); legBR.render(scale);
            color(0.10F, 0.13F, 0.20F, 1.0F); tail.render(scale);
            color(0.10F, 0.90F, 1.0F, 1.0F); visor.render(scale);
        }
    }

    private static ModelRenderer box(int x, int y, int z, float px, float py, float pz) {
        ModelRenderer renderer = new ModelRenderer(BASE, 0, 0);
        renderer.addBox(px, py, pz, x, y, z);
        return renderer;
    }

    private static ModelRenderer box(int texX, int texY, int x, int y, int z,
                                     float px, float py, float pz) {
        ModelRenderer renderer = new ModelRenderer(BASE, texX, texY);
        renderer.addBox(px, py, pz, x, y, z);
        return renderer;
    }

    private static void color(float r, float g, float b, float a) {
        GlStateManager.color(r, g, b, a);
    }
}
