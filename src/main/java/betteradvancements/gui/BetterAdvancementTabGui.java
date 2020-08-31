package betteradvancements.gui;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.advancements.BetterDisplayInfoRegistry;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BetterAdvancementTabGui extends AbstractGui {
    public static boolean doFade = true;

    private final Minecraft minecraft;
    private final BetterAdvancementsScreen screen;
    private final BetterAdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final ITextComponent title;
    private final BetterAdvancementEntryGui root;
    protected final Map<Advancement, BetterAdvancementEntryGui> guis = Maps.newLinkedHashMap();
    private final BetterDisplayInfoRegistry betterDisplayInfos;

    protected int scrollX, scrollY;
    private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public BetterAdvancementTabGui(Minecraft mc, BetterAdvancementsScreen betterAdvancementsScreen, BetterAdvancementTabType type, int index, Advancement advancement, DisplayInfo displayInfo) {
        this.minecraft = mc;
        this.screen = betterAdvancementsScreen;
        this.type = type;
        this.index = index;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle();
        this.betterDisplayInfos = new BetterDisplayInfoRegistry(advancement);
        this.root = new BetterAdvancementEntryGui(this, mc, advancement, displayInfo);
        this.addGuiAdvancement(this.root, advancement);
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public ITextComponent getTitle() {
        return this.title;
    }

    public void drawTab(MatrixStack matrixStack, int left, int top, int width, int height, boolean selected) {
        this.type.draw(this, matrixStack, left, top, width, height, selected, this.index);
    }

    public void drawIcon(MatrixStack matrixStack, int left, int top,int width, int height, ItemRenderer renderItem) {
        this.type.drawIcon(matrixStack, left, top, width, height, this.index, renderItem, this.icon);
    }

    public void drawContents(MatrixStack matrixStack, int width, int height) {
        if (!this.centered) {
            this.scrollX = (width - (this.maxX + this.minX)) / 2;
            this.scrollY = (height - (this.maxY + this.minY)) / 2;
            this.centered = true;
        }

        matrixStack.push();
        RenderSystem.enableDepthTest();
        matrixStack.translate(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(matrixStack, 0, 0, width, height, -16777216);
        RenderSystem.depthFunc(515);
        ResourceLocation resourcelocation = this.display.getBackground();

        if (resourcelocation != null) {
            this.minecraft.getTextureManager().bindTexture(resourcelocation);
        } else {
            this.minecraft.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
        }

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.scrollX % 16;
        int j = this.scrollY % 16;

        int k = -1;
        for (; k <= 1 + width / 16; k++) {
            int l = -1;
            for (;l <= height / 16; l++) {
                blit(matrixStack, i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, 16, 16, 16);
            }
            blit(matrixStack, i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, height % 16, 16, 16);
        }


        this.root.drawConnectivity(matrixStack, this.scrollX, this.scrollY, true);
        this.root.drawConnectivity(matrixStack, this.scrollX, this.scrollY, false);
        this.root.draw(matrixStack, this.scrollX, this.scrollY);
        RenderSystem.depthFunc(518);
        matrixStack.translate(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        matrixStack.translate(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        matrixStack.pop();
    }

    public void drawToolTips(MatrixStack matrixStack, int mouseX, int mouseY, int left, int top, int width, int height) {
        fill(matrixStack, 0, 0, width, height, MathHelper.floor(this.fade * 255.0F) << 24);
        boolean flag = false;

        if (mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < height) {
            for (BetterAdvancementEntryGui betterAdvancementEntryGui : this.guis.values()) {
                if (betterAdvancementEntryGui.isMouseOver(this.scrollX, this.scrollY, mouseX, mouseY)) {
                    flag = true;
                    betterAdvancementEntryGui.drawHover(matrixStack, this.scrollX, this.scrollY, this.fade, left, top);
                    break;
                }
            }
        }

        if (doFade && flag) {
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isMouseOver(int left, int top, int width, int height, double mouseX, double mouseY) {
        return this.type.isMouseOver(left, top, width, height, this.index, mouseX, mouseY);
    }

    @Nullable
    public static BetterAdvancementTabGui create(Minecraft mc, BetterAdvancementsScreen betterAdvancementsScreen, int index, Advancement advancement, int width, int height) {
        if (advancement.getDisplay() == null) {
            return null;
        } else {
            BetterAdvancementTabType advancementTabType = BetterAdvancementTabType.getTabType(width,height, index);
            if (advancementTabType == null) {
                return null;
            } else {
                return new BetterAdvancementTabGui(mc, betterAdvancementsScreen, advancementTabType, index, advancement, advancement.getDisplay());
            }

        }
    }

    public void scroll(double scrollX, double scrollY, int width, int height) {
        if (this.maxX - this.minX > width) {
            this.scrollX = (int)Math.round(MathHelper.clamp(this.scrollX + scrollX, -(this.maxX - width), -this.minX));
        }

        if (this.maxY - this.minY > height) {
            this.scrollY = (int)Math.round(MathHelper.clamp(this.scrollY + scrollY, -(this.maxY - height), -this.minY));
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            BetterAdvancementEntryGui betterAdvancementEntryGui = new BetterAdvancementEntryGui(this, this.minecraft, advancement, advancement.getDisplay());
            this.addGuiAdvancement(betterAdvancementEntryGui, advancement);
        }
    }

    private void addGuiAdvancement(BetterAdvancementEntryGui betterAdvancementEntryGui, Advancement advancement) {
        this.guis.put(advancement, betterAdvancementEntryGui);
        int left = betterAdvancementEntryGui.getX();
        int right = left + 28;
        int top = betterAdvancementEntryGui.getY();
        int bottom = top + 27;
        this.minX = Math.min(this.minX, left);
        this.maxX = Math.max(this.maxX, right);
        this.minY = Math.min(this.minY, top);
        this.maxY = Math.max(this.maxY, bottom);

        for (BetterAdvancementEntryGui gui : this.guis.values()) {
            gui.attachToParent();
        }
    }

    @Nullable
    public BetterAdvancementEntryGui getAdvancementGui(Advancement advancement) {
        return this.guis.get(advancement);
    }

    public BetterAdvancementsScreen getScreen() {
        return this.screen;
    }

    public BetterDisplayInfo getBetterDisplayInfo(Advancement advancement) {
        return betterDisplayInfos.get(advancement);
    }
}
