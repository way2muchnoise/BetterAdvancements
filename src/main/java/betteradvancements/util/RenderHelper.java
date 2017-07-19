package betteradvancements.util;

import net.minecraft.client.gui.Gui;

public class RenderHelper extends net.minecraft.client.renderer.RenderHelper {
    private RenderHelper() {}

    public static void renderRepeating(Gui screen, int x, int y, int width, int height, int textureX, int textureY, int textureWidth, int textureHeight) {
        for (int i = 0; i < width; i += textureWidth) {
            int drawX = x + i;
            int drawWidth = Math.min(textureWidth, width - i);

            for (int l = 0; l < height; l += textureHeight) {
                int drawY = y + l;
                int drawHeight = Math.min(textureHeight, height - l);
                screen.drawTexturedModalRect(drawX, drawY, textureX, textureY, drawWidth, drawHeight);
            }
        }
    }
}
