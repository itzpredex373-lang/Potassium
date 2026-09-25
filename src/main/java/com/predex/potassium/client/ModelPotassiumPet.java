package com.predex.potassium.client;

import com.predex.potassium.pet.EntityPotassiumPet;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public final class ModelPotassiumPet extends ModelBase {
    private final ModelRenderer body;
    private final ModelRenderer head;
    private final ModelRenderer leftEar;
    private final ModelRenderer rightEar;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;
    private final ModelRenderer tail;
    private final ModelRenderer leftWing;
    private final ModelRenderer rightWing;
    private final ModelRenderer hornLeft;
    private final ModelRenderer hornRight;

    public ModelPotassiumPet() {
        textureWidth = 64;
        textureHeight = 32;

        body = box(0, 0, -3, 6, 5, 4, -3, 8);
        head = box(0, 0, -3, 5, 5, 5, -2.5F, 3);
        leftEar = box(0, 0, -1, 2, 3, 2, -3.2F, 0);
        rightEar = box(0, 0, -1, 2, 3, 2, 1.2F, 0);
        leftLeg = box(0, 0, -1, 2, 3, 2, -2.6F, 5);
        rightLeg = box(0, 0, -1, 2, 3, 2, 0.6F, 5);
        tail = box(0, 0, 0, 2, 2, 4, -1, 8);
        leftWing = box(0, 0, 0, 1, 4, 4, -4.2F, 2);
        rightWing = box(0, 0, 0, 1, 4, 4, 3.2F, 2);
        hornLeft = box(0, 0, 0, 1, 3, 1, -2.0F, -2);
        hornRight = box(0, 0, 0, 1, 3, 1, 1.0F, -2);
    }

    private ModelRenderer box(int tx, int ty, int x, int w, int h, int d,
                              float px, float py) {
        ModelRenderer part = new ModelRenderer(this, tx, ty);
        part.addBox(x, py, -2.0F, w, h, d);
        part.setRotationPoint(px, 0.0F, 0.0F);
        return part;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount,
                        float ageInTicks, float headYaw, float headPitch,
                        float scale) {
        EntityPotassiumPet pet = (EntityPotassiumPet) entity;
        String type = pet.getPetType();

        leftWing.showModel = isWinged(type);
        rightWing.showModel = isWinged(type);
        hornLeft.showModel = isHorned(type);
        hornRight.showModel = isHorned(type);

        float bob = (float) Math.sin((pet.ticksExisted + ageInTicks) * 0.18F) * 0.10F;
        body.rotationPointY = bob;
        head.rotationPointY = bob - 1.0F;

        body.render(scale);
        head.render(scale);
        leftEar.render(scale);
        rightEar.render(scale);
        leftLeg.render(scale);
        rightLeg.render(scale);
        tail.render(scale);
        if (isWinged(type)) {
            leftWing.render(scale);
            rightWing.render(scale);
        }
        if (isHorned(type)) {
            hornLeft.render(scale);
            hornRight.render(scale);
        }
    }

    private boolean isWinged(String type) {
        return type.contains("dragon") || "wyvern".equals(type)
                || "butterfly".equals(type) || "bee".equals(type)
                || "crow".equals(type) || "ghost".equals(type)
                || "spirit".equals(type) || "astronaut".equals(type);
    }

    private boolean isHorned(String type) {
        return "devil".equals(type) || "inferno".equals(type)
                || "voidling".equals(type) || "guardian".equals(type)
                || "king_dragon".equals(type);
    }
}
