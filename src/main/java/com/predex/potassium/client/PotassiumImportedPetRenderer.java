package com.predex.potassium.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lightweight renderer for .potpet packs.
 *
 * The pack format uses simple ModelRenderer boxes, which keeps imported pets
 * compatible with the 1.8.9 renderer without adding another model engine.
 */
final class PotassiumImportedPetRenderer implements com.predex.potassium.api.pet.PetRenderer {
    private final ModelBase model = new ModelBase() {};
    private final List<Part> parts = new ArrayList<Part>();
    private float red;
    private float green;
    private float blue;
    private float bob = 0.20F;
    private float bobSpeed = 0.16F;
    private float sway = 0.12F;
    private float swaySpeed = 0.11F;
    private ResourceLocation texture;

    private static final class Part {
        final ModelRenderer renderer;
        final String role;

        Part(ModelRenderer renderer, String role) {
            this.renderer = renderer;
            this.role = role;
        }
    }

    private PotassiumImportedPetRenderer() {
        red = 0.55F;
        green = 0.65F;
        blue = 0.80F;
    }

    static PotassiumImportedPetRenderer fromJson(
            JsonObject modelJson, JsonObject animationJson, byte[] textureBytes, String textureId)
            throws Exception {
        PotassiumImportedPetRenderer renderer = new PotassiumImportedPetRenderer();

        if (modelJson.has("color") && modelJson.get("color").isJsonArray()) {
            JsonArray c = modelJson.getAsJsonArray("color");
            if (c.size() >= 3) {
                renderer.setColor(c.get(0).getAsFloat(), c.get(1).getAsFloat(), c.get(2).getAsFloat());
            }
        }

        JsonArray boxes = modelJson.getAsJsonArray("parts");
        if (boxes == null || boxes.size() == 0 || boxes.size() > 32) {
            throw new IllegalArgumentException("model.json needs 1-32 parts");
        }

        int textureWidth = getInt(modelJson, "textureWidth", 32);
        int textureHeight = getInt(modelJson, "textureHeight", 32);
        if (textureWidth < 1 || textureWidth > 256 || textureHeight < 1 || textureHeight > 256) {
            throw new IllegalArgumentException("invalid texture size");
        }
        renderer.model.textureWidth = textureWidth;
        renderer.model.textureHeight = textureHeight;

        for (int i = 0; i < boxes.size(); i++) {
            JsonObject box = boxes.get(i).getAsJsonObject();
            int w = getInt(box, "width", 1);
            int h = getInt(box, "height", 1);
            int d = getInt(box, "depth", 1);
            if (w < 1 || h < 1 || d < 1 || w > 32 || h > 32 || d > 32) {
                throw new IllegalArgumentException("invalid part size");
            }

            ModelRenderer part = new ModelRenderer(renderer.model,
                    getInt(box, "textureX", 0), getInt(box, "textureY", 0));
            part.addBox(
                    getFloat(box, "x", 0.0F),
                    getFloat(box, "y", 0.0F),
                    getFloat(box, "z", 0.0F),
                    w, h, d);
            renderer.parts.add(new Part(part, getString(box, "role", "body")));
        }

        if (animationJson != null) {
            renderer.setAnimation(
                    getFloat(animationJson, "bob", renderer.bob),
                    getFloat(animationJson, "bobSpeed", renderer.bobSpeed),
                    getFloat(animationJson, "sway", renderer.sway),
                    getFloat(animationJson, "swaySpeed", renderer.swaySpeed));
        }

        if (textureBytes != null) {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(textureBytes));
            if (image == null) throw new IllegalArgumentException("invalid PNG texture");
            DynamicTexture dynamicTexture = new DynamicTexture(image);
            renderer.texture = Minecraft.getMinecraft().getTextureManager()
                    .getDynamicTextureLocation("potassium_pet_" + textureId, dynamicTexture);
        }

        return renderer;
    }

    private void setColor(float r, float g, float b) {
        // Values outside 0..1 are clamped to keep packs visually safe.
        red = clamp(r);
        green = clamp(g);
        blue = clamp(b);
    }

    private void setAnimation(float ignoredBob, float ignoredBobSpeed,
                              float ignoredSway, float ignoredSwaySpeed) {
        bob = ignoredBob;
        bobSpeed = ignoredBobSpeed;
        sway = ignoredSway;
        swaySpeed = ignoredSwaySpeed;
    }

    @Override
    public void render(float ageInTicks, float scale) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.scale(scale, scale, scale);
            if (texture != null) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            }
            GlStateManager.color(red, green, blue, 1.0F);

            float bobAmount = (float)Math.sin(ageInTicks * bobSpeed) * bob;
            float tailSway = (float)Math.sin(ageInTicks * swaySpeed) * sway;
            float step = (float)Math.sin(ageInTicks * 0.34F) * 0.30F;

            GlStateManager.translate(0.0F, bobAmount * 0.0625F, 0.0F);
            for (Part part : parts) {
                if ("head".equals(part.role)) {
                    part.renderer.rotateAngleY = (float)Math.sin(ageInTicks * 0.07F) * 0.08F;
                } else if ("leg".equals(part.role)) {
                    part.renderer.rotateAngleX = step;
                } else if ("leg2".equals(part.role)) {
                    part.renderer.rotateAngleX = -step;
                } else if ("tail".equals(part.role)) {
                    part.renderer.rotateAngleY = tailSway;
                } else if ("wing".equals(part.role)) {
                    part.renderer.rotateAngleZ =
                            (float)Math.sin(ageInTicks * 0.42F) * 0.35F;
                }
                part.renderer.render(0.0625F);
            }
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();
        }
    }

    private static int getInt(JsonObject o, String key, int fallback) {
        return o.has(key) ? o.get(key).getAsInt() : fallback;
    }

    private static float getFloat(JsonObject o, String key, float fallback) {
        return o.has(key) ? o.get(key).getAsFloat() : fallback;
    }

    private static String getString(JsonObject o, String key, String fallback) {
        return o.has(key) ? o.get(key).getAsString() : fallback;
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
