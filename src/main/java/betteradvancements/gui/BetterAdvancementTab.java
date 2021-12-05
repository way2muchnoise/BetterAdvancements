package betteradvancements.gui;

import betteradvancements.advancements.BetterDisplayInfo;
import betteradvancements.advancements.BetterDisplayInfoRegistry;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BetterAdvancementTab extends GuiComponent {
    public static boolean doFade = true;

    private final Minecraft minecraft;
    private final BetterAdvancementsScreen screen;
    private final BetterAdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final Component title;
    private final BetterAdvancementWidget root;
    protected final Map<Advancement, BetterAdvancementWidget> guis = Maps.newLinkedHashMap();
    private final BetterDisplayInfoRegistry betterDisplayInfos;

    protected int scrollX, scrollY;
    private int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    private int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public BetterAdvancementTab(Minecraft mc, BetterAdvancementsScreen betterAdvancementsScreen, BetterAdvancementTabType type, int index, Advancement advancement, DisplayInfo displayInfo) {
        this.minecraft = mc;
        this.screen = betterAdvancementsScreen;
        this.type = type;
        this.index = index;
        this.advancement = advancement;
        this.display = displayInfo;
        this.icon = displayInfo.getIcon();
        this.title = displayInfo.getTitle();
        this.betterDisplayInfos = new BetterDisplayInfoRegistry(advancement);
        this.root = new BetterAdvancementWidget(this, mc, advancement, displayInfo);
        this.addGuiAdvancement(this.root, advancement);
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public Component getTitle() {
        return this.title;
    }

    public void drawTab(PoseStack poseStack, int left, int top, int width, int height, boolean selected) {
        this.type.draw(this, poseStack, left, top, width, height, selected, this.index);
    }

    public void drawIcon(PoseStack poseStack, int left, int top,int width, int height, ItemRenderer renderItem) {
        this.type.drawIcon(poseStack, left, top, width, height, this.index, renderItem, this.icon);
    }

    public void drawContents(PoseStack poseStack, int width, int height) {
        if (!this.centered) {
            this.scrollX = (width - (this.maxX + this.minX)) / 2;
            this.scrollY = (height - (this.maxY + this.minY)) / 2;
            this.centered = true;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 950.0D);
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);
        fill(poseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        poseStack.translate(0.0D, 0.0D, -950.0D);
        RenderSystem.depthFunc(518);
        fill(poseStack, width, height, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        ResourceLocation resourcelocation = this.display.getBackground();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, resourcelocation != null ? resourcelocation : TextureManager.INTENTIONAL_MISSING_TEXTURE);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.scrollX % 16;
        int j = this.scrollY % 16;

        int k = -1;
        for (; k <= 1 + width / 16; k++) {
            int l = -1;
            for (;l <= height / 16; l++) {
                blit(poseStack, i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, 16, 16, 16);
            }
            blit(poseStack, i + 16 * k, j + 16 * l, 0.0F, 0.0F, 16, height % 16, 16, 16);
        }


        this.root.drawConnectivity(poseStack, this.scrollX, this.scrollY, true);
        this.root.drawConnectivity(poseStack, this.scrollX, this.scrollY, false);
        this.root.draw(poseStack, this.scrollX, this.scrollY);
        RenderSystem.depthFunc(518);
        poseStack.translate(0.0D, 0.0D, -950.0D);
        RenderSystem.colorMask(false, false, false, false);
        fill(poseStack, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        poseStack.popPose();
    }

    public void drawToolTips(PoseStack poseStack, int mouseX, int mouseY, int left, int top, int width, int height) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, -200.0D);
        fill(poseStack, 0, 0, width, height, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;

        if (mouseX > 0 && mouseX < width && mouseY > 0 && mouseY < height) {
            for (BetterAdvancementWidget betterAdvancementWidget : this.guis.values()) {
                if (betterAdvancementWidget.isMouseOver(this.scrollX, this.scrollY, mouseX, mouseY)) {
                    flag = true;
                    betterAdvancementWidget.drawHover(poseStack, this.scrollX, this.scrollY, this.fade, left, top);
                    break;
                }
            }
        }

        poseStack.popPose();

        if (doFade && flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isMouseOver(int left, int top, int width, int height, double mouseX, double mouseY) {
        return this.type.isMouseOver(left, top, width, height, this.index, mouseX, mouseY);
    }

    @Nullable
    public static BetterAdvancementTab create(Minecraft mc, BetterAdvancementsScreen betterAdvancementsScreen, int index, Advancement advancement, int width, int height) {
        if (advancement.getDisplay() == null) {
            return null;
        } else {
            BetterAdvancementTabType advancementTabType = BetterAdvancementTabType.getTabType(width,height, index);
            if (advancementTabType == null) {
                return null;
            } else {
                return new BetterAdvancementTab(mc, betterAdvancementsScreen, advancementTabType, index, advancement, advancement.getDisplay());
            }

        }
    }

    public void scroll(double scrollX, double scrollY, int width, int height) {
        if (this.maxX - this.minX > width) {
            this.scrollX = (int)Math.round(Mth.clamp(this.scrollX + scrollX, -(this.maxX - width), -this.minX));
        }

        if (this.maxY - this.minY > height) {
            this.scrollY = (int)Math.round(Mth.clamp(this.scrollY + scrollY, -(this.maxY - height), -this.minY));
        }
    }

    public void addAdvancement(Advancement advancement) {
        if (advancement.getDisplay() != null) {
            BetterAdvancementWidget betterAdvancementEntryScreen = new BetterAdvancementWidget(this, this.minecraft, advancement, advancement.getDisplay());
            this.addGuiAdvancement(betterAdvancementEntryScreen, advancement);
        }
    }

    private void addGuiAdvancement(BetterAdvancementWidget betterAdvancementEntryScreen, Advancement advancement) {
        this.guis.put(advancement, betterAdvancementEntryScreen);
        int left = betterAdvancementEntryScreen.getX();
        int right = left + 28;
        int top = betterAdvancementEntryScreen.getY();
        int bottom = top + 27;
        this.minX = Math.min(this.minX, left);
        this.maxX = Math.max(this.maxX, right);
        this.minY = Math.min(this.minY, top);
        this.maxY = Math.max(this.maxY, bottom);

        for (BetterAdvancementWidget gui : this.guis.values()) {
            gui.attachToParent();
        }
    }

    @Nullable
    public BetterAdvancementWidget getWidget(Advancement advancement) {
        return this.guis.get(advancement);
    }

    public BetterAdvancementsScreen getScreen() {
        return this.screen;
    }

    public BetterDisplayInfo getBetterDisplayInfo(Advancement advancement) {
        return betterDisplayInfos.get(advancement);
    }
}
