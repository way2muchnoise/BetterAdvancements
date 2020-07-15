package betteradvancements.gui;

import betteradvancements.reference.Resources;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class BetterAdvancementsScreenButtonWidget extends Widget {
    public static boolean addToInventory = false;

    public BetterAdvancementsScreenButtonWidget(int x, int y, ITextComponent buttonText) {
        super(x - 28, y - 28, 28, 28, buttonText);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible)
        {
            Minecraft mc  = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(Resources.Gui.TABS);
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.blit(matrixStack, this.x, this.y, 56, 0, 28, 32);
            if (this.isHovered) {
                mc.currentScreen.renderTooltip(matrixStack, new StringTextComponent("Advancements"), mouseX, mouseY);
            }
            RenderSystem.enableRescaleNormal();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderHelper.enableStandardItemLighting();
            mc.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(Items.BOOK), this.x + 6, this.y + 10);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int modifiers) {
        if (super.mouseClicked(mouseX, mouseY, modifiers)) {
            Minecraft.getInstance().displayGuiScreen(new BetterAdvancementsScreen(Minecraft.getInstance().player.connection.getAdvancementManager()));
            return true;
        }
        return false;
    }
}
