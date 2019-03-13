package betteradvancements.gui;

import betteradvancements.reference.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class GuiBetterAdvancementsButton extends GuiButton {
    public static boolean addToInventory = false;

    public GuiBetterAdvancementsButton(int x, int y, String buttonText) {
        super(-99, x - 28, y - 28, 28, 28, buttonText);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.visible)
        {
            Minecraft mc  = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(Resources.Gui.TABS);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.drawTexturedModalRect(this.x, this.y, 56, 0, 28, 32);
            if (hovered) {
                mc.currentScreen.drawHoveringText("Advancements", mouseX, mouseY);
            }
            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(Items.BOOK), this.x + 6, this.y + 10);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
        if (super.mouseClicked(mouseX, mouseY, modifiers)) {
            Minecraft.getInstance().displayGuiScreen(new GuiScreenBetterAdvancements(Minecraft.getInstance().player.connection.getAdvancementManager()));
            return true;
        }
        return false;
    }
}
