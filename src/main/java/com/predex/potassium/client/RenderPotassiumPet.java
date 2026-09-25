package com.predex.potassium.client;

import com.predex.potassium.pet.EntityPotassiumPet;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public final class RenderPotassiumPet extends RenderLiving<EntityPotassiumPet> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("textures/entity/wolf/wolf.png");

    public RenderPotassiumPet(RenderManager manager) {
        super(manager, new ModelPotassiumPet(), 0.12F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPotassiumPet entity) {
        return TEXTURE;
    }

    @Override
    protected void preRenderCallback(EntityPotassiumPet entity, float partialTicks) {
        float scale = 0.48F;
        String type = entity.getPetType();

        if ("king_dragon".equals(type) || "black_dragon".equals(type)
                || "shadow_dragon".equals(type) || "wyvern".equals(type)) {
            scale = 0.60F;
        } else if ("butterfly".equals(type) || "bee".equals(type)
                || "moon_rabbit".equals(type)) {
            scale = 0.38F;
        }

        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    public void doRender(EntityPotassiumPet entity, double x, double y, double z,
                          float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        try {
            float[] rgb = color(entity.getPetType());
            GlStateManager.color(rgb[0], rgb[1], rgb[2], 1.0F);
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private float[] color(String type) {
        if ("predex".equals(type)) return new float[] {0.20F, 0.55F, 1.0F};
        if (type.contains("dragon") || "wyvern".equals(type)) return new float[] {0.55F, 0.12F, 0.85F};
        if ("devil".equals(type) || "inferno".equals(type)) return new float[] {1.0F, 0.18F, 0.12F};
        if ("heart_cat".equals(type)) return new float[] {1.0F, 0.30F, 0.55F};
        if ("voidling".equals(type) || "ghost".equals(type)) return new float[] {0.48F, 0.18F, 0.85F};
        if ("bee".equals(type)) return new float[] {1.0F, 0.80F, 0.18F};
        if ("robot".equals(type) || "astronaut".equals(type)) return new float[] {0.65F, 0.75F, 0.85F};
        return new float[] {0.90F, 0.90F, 0.90F};
    }
}
