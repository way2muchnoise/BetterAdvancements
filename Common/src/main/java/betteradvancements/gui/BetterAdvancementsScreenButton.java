package betteradvancements.gui;

import betteradvancements.reference.Resources;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class BetterAdvancementsScreenButton extends AbstractButton {
    public static boolean addToInventory = false;

    public BetterAdvancementsScreenButton(int x, int y, Component buttonText) {
        super(x - 28, y - 28, 28, 28, buttonText);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible)
        {
            Minecraft mc  = Minecraft.getInstance();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, Resources.Gui.TABS);
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.getHeight();
            this.blit(poseStack, this.getX(), this.getY(), 56, 0, 28, 32);
            if (this.isHovered) {
                mc.screen.renderTooltip(poseStack, Component.literal("Advancements"), mouseX, mouseY);
            }
            RenderSystem.defaultBlendFunc();
            mc.getItemRenderer().renderAndDecorateFakeItem(poseStack, new ItemStack(Items.BOOK), this.getX() + 6, this.getY() + 10);
        }
    }

    @Override
    public void onPress() {
        Minecraft.getInstance().setScreen(new BetterAdvancementsScreen(Minecraft.getInstance().player.connection.getAdvancements()));
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
