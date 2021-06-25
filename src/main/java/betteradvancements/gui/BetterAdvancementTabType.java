package betteradvancements.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.advancements.AdvancementTabType;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

public class BetterAdvancementTabType {
    public static final BetterAdvancementTabType ABOVE = new BetterAdvancementTabType(0, 0, 28, 32, AdvancementTabType.ABOVE);
    public static final BetterAdvancementTabType BELOW = new BetterAdvancementTabType(84, 0, 28, 32, AdvancementTabType.BELOW);
    public static final BetterAdvancementTabType LEFT = new BetterAdvancementTabType(0, 64, 32, 28, AdvancementTabType.LEFT);
    public static final BetterAdvancementTabType RIGHT = new BetterAdvancementTabType(96, 64, 32, 28, AdvancementTabType.RIGHT);

    public static BetterAdvancementTabType getTabType(int width, int height, int index) {
        int horizontal = width / 32;
        int vertical = height / 32;

        if (index < horizontal) {
            return ABOVE;
        } else if (index < 2 * horizontal) {
            return BELOW;
        } else if (index < 2 * horizontal + vertical) {
            return RIGHT;
        } else if (index < 2 * horizontal + 2 * vertical) {
            return LEFT;
        } else {
            return null;
        }
    }

    private final int textureX;
    private final int textureY;
    private final int width;
    private final int height;
    private final AdvancementTabType tabType;

    private BetterAdvancementTabType(int textureX, int textureY, int width, int height, AdvancementTabType tabType) {
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = width;
        this.height = height;
        this.tabType = tabType;
    }

    public void draw(AbstractGui gui, MatrixStack matrixStack, int x, int y, int width, int height, boolean selected, int index) {
        int i = this.textureX;
        index %= getMax(width, height);

        if (index > 0) {
            i += this.width;
        }

        if (x + this.width == width) {
            i += this.width;
        }

        int j = selected ? this.textureY + this.height : this.textureY;
        gui.blit(matrixStack, x + this.getX(index, width, height), y + this.getY(index, width, height), i, j, this.width, this.height);
    }

    public void drawIcon(MatrixStack matrixStack, int left, int top, int width, int height, int index, ItemRenderer renderItem, ItemStack stack) {
        int i = left + this.getX(index, width, height);
        int j = top + this.getY(index, width, height);

        switch (tabType)
        {
            case ABOVE:
                i += 6;
                j += 9;
                break;
            case BELOW:
                i += 6;
                j += 6;
                break;
            case LEFT:
                i += 10;
                j += 5;
                break;
            case RIGHT:
                i += 6;
                j += 5;
        }

        renderItem.renderAndDecorateItem(null, stack, i, j);
    }

    public int getX(int index, int width, int height) {
        index %= getMax(width, height);
        switch (tabType)
        {
            case ABOVE:
                return (this.width + 4) * index;
            case BELOW:
                return (this.width + 4) * index;
            case LEFT:
                return -this.width + 4;
            case RIGHT:
                return width - 4;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public int getY(int index, int width, int height) {
        index %= getMax(width, height);
        switch (tabType)
        {
            case ABOVE:
                return -this.height + 4;
            case BELOW:
                return height - 4;
            case LEFT:
                return this.height * index;
            case RIGHT:
                return this.height * index;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public boolean isMouseOver(int left, int top, int width, int height, int index, double mouseX, double mouseY) {
        int i = left + this.getX(index, width, height);
        int j = top + this.getY(index, width, height);
        return mouseX > i && mouseX < i + this.width && mouseY > j && mouseY < j + this.height;
    }

    private int getMax(int width, int height) {
        switch (tabType) {
            case LEFT:
            case RIGHT:
                return height / 32;
            case ABOVE:
            case BELOW:
                return width / 32;
            default:
                return tabType.getMax();
        }
    }
}
