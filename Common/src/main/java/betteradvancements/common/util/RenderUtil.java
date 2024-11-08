package betteradvancements.common.util;

import com.mojang.blaze3d.platform.GlStateManager;
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
    
    public static void drawRect(float x, float y, float x2, float y2, float width, int color) {
        if (y > y2) {
            float tempY = y;
            float tempX = x;
            y = y2;
            x = x2;
            y2 = tempY;
            x2 = tempX;
        }
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderUtil.setColor(color);
        BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        boolean xHigh = x < x2;
        bufferbuilder.addVertex(x, xHigh ? y + width : y, 0.0F);
        bufferbuilder.addVertex(x2, xHigh ? y2 + width : y2, 0.0F);
        bufferbuilder.addVertex(x2 + width, xHigh ? y2 : y2 + width, 0.0F);
        bufferbuilder.addVertex(x + width, xHigh ? y : y + width, 0.0F);
        RenderSystem.disableBlend();
    }
}
