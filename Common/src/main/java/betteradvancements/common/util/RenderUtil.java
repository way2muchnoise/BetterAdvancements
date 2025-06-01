package betteradvancements.common.util;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RenderUtil {
    private RenderUtil() {}

    public static void renderRepeating(ResourceLocation texture, GuiGraphics guiGraphics, int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
        for (int i = 0; i < width; i += textureWidth) {
            int drawX = x + i;
            int drawWidth = Math.min(textureWidth, width - i);

            for (int l = 0; l < height; l += textureHeight) {
                int drawY = y + l;
                int drawHeight = Math.min(textureHeight, height - l);
                guiGraphics.blit(RenderType::guiTextured, texture, drawX, drawY, textureX, textureY, drawWidth, drawHeight, 256, 256);
            }
        }
    }

    public static void setColor(int color) {
        RenderSystem.setShaderColor(((color >> 16) & 255) / 255F, ((color >> 8) & 255) / 255F, (color & 255) / 255F, 1.0F);
    }
    
    public static void drawRect(GuiGraphics guiGraphics, float x, float y, float x2, float y2, float width, int color) {
        if (y > y2) {
            float tempY = y;
            float tempX = x;
            y = y2;
            x = x2;
            y2 = tempY;
            x2 = tempX;
        }
        boolean xHigh = x < x2;
        float finalX = x;
        float finalY = y;
        float finalX2 = x2;
        float finalY2 = y2;
        guiGraphics.drawSpecial(multiBufferSource ->
        {
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.gui());

            vertexConsumer.addVertex(finalX, xHigh ? finalY + width : finalY, 0.0F);
            vertexConsumer.addVertex(finalX2, xHigh ? finalY2 + width : finalY2, 0.0F);
            vertexConsumer.addVertex(finalX2 + width, xHigh ? finalY2 : finalY2 + width, 0.0F);
            vertexConsumer.addVertex(finalX + width, xHigh ? finalY : finalY + width, 0.0F);
        });
    }
}
