package com.predex.potassium.client;

import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * Client-only own-player name tag.
 *
 * Uses the scoreboard/team prefix supplied by the server, so common server
 * ranks are preserved without sending or modifying any server data.
 */
public final class PotassiumOwnNameTag {
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!PerformanceProfileManager.isQoLAllowed()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null || mc.theWorld == null) return;

        RenderManager renderManager = mc.getRenderManager();
        double x = player.lastTickPosX
                + (player.posX - player.lastTickPosX) * event.partialTicks
                - renderManager.renderPosX;
        double y = player.lastTickPosY
                + (player.posY - player.lastTickPosY) * event.partialTicks
                - renderManager.renderPosY
                + player.height + 0.55D;
        double z = player.lastTickPosZ
                + (player.posZ - player.lastTickPosZ) * event.partialTicks
                - renderManager.renderPosZ;

        String name = ScorePlayerTeam.formatPlayerName(
                mc.theWorld.getScoreboard().getPlayersTeam(player.getName()),
                player.getName());

        if (name == null || name.length() == 0) return;

        float scale = 0.02666667F;

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate((float) x, (float) y, (float) z);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-scale, -scale, scale);

            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            int width = mc.fontRendererObj.getStringWidth(name) / 2;
            int backgroundColor = 0x40000000;

            GlStateManager.disableTexture2D();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.25F);
            GL11.glVertex2f(-width - 2, -1.0F);
            GL11.glVertex2f(-width - 2, 8.0F);
            GL11.glVertex2f(width + 2, 8.0F);
            GL11.glVertex2f(width + 2, -1.0F);
            GL11.glEnd();
            GlStateManager.enableTexture2D();

            mc.fontRendererObj.drawString(name,
                    -mc.fontRendererObj.getStringWidth(name) / 2,
                    0, 0xFFFFFFFF);

            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
