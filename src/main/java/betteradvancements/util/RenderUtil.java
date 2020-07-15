package betteradvancements.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil {
    private RenderUtil() {}

    public static void renderRepeating(AbstractGui abstractGui, MatrixStack matrixStack, int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
        for (int i = 0; i < width; i += textureWidth) {
            int drawX = x + i;
            int drawWidth = Math.min(textureWidth, width - i);

            for (int l = 0; l < height; l += textureHeight) {
                int drawY = y + l;
                int drawHeight = Math.min(textureHeight, height - l);
                abstractGui.blit(matrixStack, drawX, drawY, textureX, textureY, drawWidth, drawHeight);
            }
        }
    }

    public static void setColor(int color) {
        RenderSystem.color3f(((color >> 16) & 255) / 255F, ((color >> 8) & 255) / 255F, (color & 255) / 255F);
    }
    
    public static void drawRect(double x, double y, double x2, double y2, double width, int color) {
        if (y > y2) {
            double tempY = y;
            double tempX = x;
            y = y2;
            x = x2;
            y2 = tempY;
            x2 = tempX;
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderUtil.setColor(color);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        boolean xHigh = false;
        if (x < x2) {
            xHigh = true;
        }
        bufferbuilder.pos(x, xHigh ? y + width : y, 0.0D).endVertex();
        bufferbuilder.pos(x2, xHigh ? y2 + width : y2, 0.0D).endVertex();
        bufferbuilder.pos(x2 + width, xHigh ? y2 : y2 + width, 0.0D).endVertex();
        bufferbuilder.pos(x + width, xHigh ? y : y + width, 0.0D).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
