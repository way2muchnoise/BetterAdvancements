package betteradvancements.common.gui;

import betteradvancements.common.reference.Resources;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;


public class BetterAdvancementsScreenButton extends AbstractButton {
    public static boolean addToInventory = false;

    public BetterAdvancementsScreenButton(int x, int y, Component buttonText) {
        super(x - 28, y - 28, 28, 28, buttonText);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible)
        {
            Minecraft mc  = Minecraft.getInstance();
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
            guiGraphics.blit(Resources.Gui.TABS, this.getX(), this.getY(), 56, 0, 28, 32);
            if (this.isHovered) {
                guiGraphics.renderTooltip(mc.font, Component.literal("Advancements"), mouseX, mouseY);
            }
            RenderSystem.defaultBlendFunc();
            guiGraphics.renderFakeItem(new ItemStack(Items.BOOK), this.getX() + 6, this.getY() + 10);
        }
    }

    @Override
    public void onPress() {
        Minecraft.getInstance().setScreen(new BetterAdvancementsScreen(Minecraft.getInstance().player.connection.getAdvancements()));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
