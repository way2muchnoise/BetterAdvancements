package betteradvancements.common.util;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
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
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, drawX, drawY, textureX, textureY, drawWidth, drawHeight, 256, 256);
            }
        }
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

        // Calculate the rectangle bounds based on whether x is increasing or decreasing
        float minX = Math.min(x, x2);
        float maxX = Math.max(x, x2) + width;
        float minY, maxY;

        if (xHigh) {
            minY = y;
            maxY = y2 + width;
        } else {
            minY = y;
            maxY = y2 + width;
        }

        // Draw the rectangle using fill
        guiGraphics.fill((int)minX, (int)minY, (int)maxX, (int)maxY, color);
    }
}
