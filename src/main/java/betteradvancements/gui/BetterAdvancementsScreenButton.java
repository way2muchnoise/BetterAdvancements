package betteradvancements.gui;

import betteradvancements.reference.Resources;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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
            this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            this.blit(poseStack, this.x, this.y, 56, 0, 28, 32);
            if (this.isHovered) {
                mc.screen.renderTooltip(poseStack, new TextComponent("Advancements"), mouseX, mouseY);
            }
            RenderSystem.defaultBlendFunc();
            mc.getItemRenderer().renderAndDecorateFakeItem(new ItemStack(Items.BOOK), this.x + 6, this.y + 10);
        }
    }

    @Override
    public void onPress() {
        Minecraft.getInstance().setScreen(new BetterAdvancementsScreen(Minecraft.getInstance().player.connection.getAdvancements()));
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
